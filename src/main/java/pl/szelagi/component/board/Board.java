/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.board;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.Scheduler;
import pl.szelagi.SessionAPI;
import pl.szelagi.buildin.system.SecureZone;
import pl.szelagi.buildin.system.boardwatchdog.BoardWatchDogController;
import pl.szelagi.component.BaseComponent;
import pl.szelagi.component.ComponentStatus;
import pl.szelagi.component.baseexception.StartException;
import pl.szelagi.component.baseexception.StopException;
import pl.szelagi.component.board.event.BoardStartEvent;
import pl.szelagi.component.board.event.BoardStopEvent;
import pl.szelagi.component.board.exception.BoardStartException;
import pl.szelagi.component.board.filemanager.BoardFileManager;
import pl.szelagi.component.session.Session;
import pl.szelagi.event.component.ComponentConstructorEvent;
import pl.szelagi.event.player.initialize.PlayerConstructorEvent;
import pl.szelagi.event.player.initialize.PlayerDestructorEvent;
import pl.szelagi.process.RemoteProcess;
import pl.szelagi.space.Space;
import pl.szelagi.space.SpaceAllocator;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.tag.TagQuery;
import pl.szelagi.tag.TagResolve;
import pl.szelagi.util.Debug;
import pl.szelagi.world.SessionWorldManager;

public abstract class Board extends BaseComponent {
	public final static String SCHEMATIC_CONSTRUCTOR_NAME = "constructor";
	public final static String SCHEMATIC_DESTRUCTOR_NAME = "destructor";
	public final static String SIGN_TAG_DATA_NAME = "tag";
	private final Session session;
	private RemoteProcess remoteProcess;
	private boolean isUsed;
	private Space space;
	private BoardFileManager boardFileManager;
	private TagResolve tagResolve;
	private ISpatial secureZone;

	public Board(Session session) {
		this.session = session;
		this.isUsed = false;
	}

	@MustBeInvokedByOverriders
	public void start() throws StartException {
		start(true, null);
	}

	@MustBeInvokedByOverriders
	public void syncStart() throws StartException {
		start(false, null);
	}

	@MustBeInvokedByOverriders
	public void start(boolean async, @Nullable Runnable thenGenerate) throws StartException {
		validateStartable();
		validateNotStartedBefore();
		setStatus(ComponentStatus.INITIALIZING);

		Debug.send(this, "start");

		remoteProcess = new RemoteProcess(this);
		remoteProcess.registerListener(this);

		space = SpaceAllocator.allocate(SessionWorldManager.getSessionWorld());
		this.boardFileManager = new BoardFileManager(getName(), getSpace());

		if (boardFileManager.existsSignTagData(SIGN_TAG_DATA_NAME)) {
			this.tagResolve = boardFileManager.loadSignTagData(SIGN_TAG_DATA_NAME);
		} else {
			this.tagResolve = new TagResolve();
		}

		Debug.send(this, "generating...");

		Runnable lastAction = () -> {
			if (thenGenerate != null) {
				thenGenerate.run();
			}

			secureZoneValid();

			setStatus(ComponentStatus.RUNNING);

			invokeSelf();

			var event = new BoardStartEvent(this);
			callBukkitEvent(event);
		};

		if (async) {
			Scheduler.runTaskAsync(() -> {
				generate();
				Debug.send(this, "generate done");
				Scheduler.runAndWait(lastAction);
			});
		} else {
			generate();
			Debug.send(this, "generate done");
			lastAction.run();
		}
	}

	private void startBoardSystemControllers(ComponentConstructorEvent event) {
		new BoardWatchDogController(this).start();
		new SecureZone(this).start();
		// TODO: It may be necessary to ensure that
		// all session maps are checked by these controllers
	}

	@MustBeInvokedByOverriders
	public void stop() throws StopException {
		stop(true);
	}

	@MustBeInvokedByOverriders
	public void stop(boolean async) throws StopException {
		validateDisableable();
		setStatus(ComponentStatus.SHUTTING_DOWN);

		Debug.send(this, "stop");

		invokeSelfPlayerDestructors();
		invokeSelfComponentDestructor();

		remoteProcess.destroy();

		// degenerate
		Debug.send(this, "degenerate");

		Runnable lastAction = () -> {
			// deallocate space
			SpaceAllocator.deallocate(space);

			// BoardStopEvent
			var event = new BoardStopEvent(this);
			Bukkit.getPluginManager()
			      .callEvent(event);

			setStatus(ComponentStatus.SHUTDOWN);
		};

		if (async) {
			var scheduler = Bukkit.getScheduler();
			scheduler.runTaskAsynchronously(SessionAPI.getInstance(), () -> {
				degenerate();
				scheduler.runTask(SessionAPI.getInstance(), lastAction);
			});
		} else {
			degenerate();
			lastAction.run();
		}
	}

	protected void generate() {
		Scheduler.runAndWait(() -> {
			getSpace().getCenter().getBlock()
			          .setType(Material.BEDROCK);
		});

		if (boardFileManager.existsSchematic(SCHEMATIC_DESTRUCTOR_NAME)) {
			secureZone = boardFileManager.toSpatial(SCHEMATIC_DESTRUCTOR_NAME, getBase());
		}
		if (boardFileManager.existsSchematic(SCHEMATIC_CONSTRUCTOR_NAME)) {
			boardFileManager.loadSchematic(SCHEMATIC_CONSTRUCTOR_NAME);
		}

		if (tagResolve != null) {
			Scheduler.runAndWait(() -> {
				for (var l : tagResolve.toLocations())
					l.getBlock()
					 .setType(Material.AIR);
			});
		}
	}

	protected void degenerate() {
		if (boardFileManager.existsSchematic(SCHEMATIC_DESTRUCTOR_NAME))
			boardFileManager.loadSchematic(SCHEMATIC_DESTRUCTOR_NAME);
		Scheduler.runAndWait(() -> {
			for (var entity : getSpace().getMobsIn())
				entity.remove();
		});
	}

	public final Location getBase() {
		return getSpace().getCenter();
	}

	public final Space getSpace() {
		return space;
	}

	@Override
	public final @NotNull Session getSession() {
		return session;
	}

	public final @NotNull TagResolve getTagResolve() {
		return tagResolve;
	}

	@Deprecated
	public final @NotNull TagQuery tagQuery(@NotNull String tagName) {
		return tagResolve.query(tagName);
	}

	public final @NotNull TagQuery tags(@NotNull String name) {
		return tagResolve.query(name);
	}

	public final RemoteProcess getProcess() {
		return remoteProcess;
	}

	public final @NotNull JavaPlugin getPlugin() {
		return getSession().getPlugin();
	}

	public final @NotNull BoardFileManager getSchematicStorage() {
		return boardFileManager;
	}

	public final boolean isUsed() {
		return isUsed;
	}

	@Override
	public final RemoteProcess getParentProcess() {
		return session.getProcess();
	}

	protected int getDefaultTime() {
		return 0;
	}

	protected @NotNull WeatherType getDefaultWeather() {
		return WeatherType.CLEAR;
	}

	protected @NotNull Location getStartSpawnLocation() {
		return getSpace().getAbove(getSpace().getCenter());
	}

	@Override
	public void playerConstructor(PlayerConstructorEvent event) {
		super.playerConstructor(event);
		var player = event.getPlayer();
		player.teleport(getStartSpawnLocation());
		player.setPlayerTime(getDefaultTime(), false);
		player.setPlayerWeather(getDefaultWeather());
	}

	@Override
	public void playerDestructor(PlayerDestructorEvent event) {
		super.playerDestructor(event);
		var player = event.getPlayer();
		player.resetPlayerWeather();
		player.resetPlayerTime();
	}

	private void secureZoneValid() throws BoardStartException {
		if (secureZone == null)
			throw new BoardStartException("Method Board.generate() did not define SecureZone");
	}

	protected void setSecureZone(ISpatial spatial) {
		secureZone = spatial;
	}

	public ISpatial getSecureZone() {
		return secureZone;
	}
}
