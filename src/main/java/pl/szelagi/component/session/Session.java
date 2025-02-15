/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.session;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.buildin.controller.HideOtherPlayers;
import pl.szelagi.buildin.system.loading.LoadingBoard;
import pl.szelagi.buildin.system.recovery.RecoveryPlayerController;
import pl.szelagi.buildin.system.sessionsafecontrolplayers.SessionSafeControlPlayers;
import pl.szelagi.buildin.system.sessionwatchdog.SessionWatchDogController;
import pl.szelagi.component.BaseComponent;
import pl.szelagi.component.ComponentStatus;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.cause.StopCause;
import pl.szelagi.component.session.event.SessionStartEvent;
import pl.szelagi.component.session.event.SessionStopEvent;
import pl.szelagi.component.session.exception.SessionStartException;
import pl.szelagi.component.session.exception.SessionStopException;
import pl.szelagi.component.session.exception.player.initialize.PlayerInSessionException;
import pl.szelagi.component.session.exception.player.initialize.PlayerIsNotAliveException;
import pl.szelagi.component.session.exception.player.initialize.PlayerJoinException;
import pl.szelagi.component.session.exception.player.initialize.RejectedPlayerException;
import pl.szelagi.component.session.exception.player.uninitialize.PlayerNoInThisSession;
import pl.szelagi.component.session.exception.player.uninitialize.PlayerQuitException;
import pl.szelagi.event.component.ComponentConstructorEvent;
import pl.szelagi.event.player.requestChange.PlayerJoinRequestEvent;
import pl.szelagi.event.player.requestChange.PlayerQuitRequestEvent;
import pl.szelagi.event.player.requestChange.type.JoinType;
import pl.szelagi.event.player.requestChange.type.QuitType;
import pl.szelagi.event.player.initialize.InvokeType;
import pl.szelagi.event.player.initialize.PlayerConstructorEvent;
import pl.szelagi.event.player.initialize.PlayerDestructorEvent;
import pl.szelagi.manager.SessionManager;
import pl.szelagi.process.MainProcess;
import pl.szelagi.process.RemoteProcess;
import pl.szelagi.util.Debug;
import pl.szelagi.util.timespigot.Time;

import java.util.ArrayList;
import java.util.List;

public abstract class Session extends BaseComponent {
	protected final ArrayList<Player> players = new ArrayList<>();
	private final MainProcess mainProcess;
	private RemoteProcess remoteProcess;
	private final JavaPlugin plugin;
	private Board currentBoard;
	private RecoveryPlayerController recoveryPlayerController;

	public Session(JavaPlugin plugin) {
		this.plugin = plugin;
		this.mainProcess = new MainProcess(this);
	}

	@MustBeInvokedByOverriders
	public void start() throws SessionStartException, PlayerJoinException {
		validateStartable();
		validateNotStartedBefore();
		setStatus(ComponentStatus.RUNNING);

		Debug.send(this, "start");

		remoteProcess = new RemoteProcess(mainProcess);
		remoteProcess.registerListener(this);

		invokeSelf();

		var event = new SessionStartEvent(this);
		callBukkitEvent(event);

		currentBoard = new LoadingBoard(this);
		currentBoard.syncStart();

		setBoard(getDefaultStartBoard());
	}

	@MustBeInvokedByOverriders
	public void stop(StopCause cause) throws SessionStopException {
		validateDisableable();
		setStatus(ComponentStatus.SHUTDOWN);
		Debug.send(this, "stop");

		var playersArrayCopy = new ArrayList<>(players);
		playersArrayCopy.forEach(this::removePlayer);

		getCurrentBoard().stop();

		invokeSelfPlayerDestructors();
		invokeSelfComponentDestructor();

		//stop and destroy all tasks
		remoteProcess.destroy();
		mainProcess.destroy();

		var event = new SessionStopEvent(this);
		Bukkit.getPluginManager()
		      .callEvent(event);
	}

	@MustBeInvokedByOverriders
	public final void addPlayer(Player player) throws PlayerJoinException {
		PlayerIsNotAliveException.check(player);
		PlayerInSessionException.check(player);
		Debug.send(this, "try add player");
		var canJoinEvent = new PlayerJoinRequestEvent(player, getPlayers(), JoinType.PLUGIN);
		getProcess().invokeAllListeners(canJoinEvent);
		if (canJoinEvent.isCanceled()) {
			assert canJoinEvent.getCancelCause() != null;
			throw new RejectedPlayerException(canJoinEvent.getCancelCause());
		}
		var otherPlayers = new ArrayList<>(getPlayers());

		SessionManager.addRelation(player, this.getSession());
		players.add(player);

		Debug.send(this, "add player");
		var joinEvent = new PlayerConstructorEvent(player, otherPlayers, getPlayers(), InvokeType.CHANGE);
		getProcess().invokeAllListeners(joinEvent);
	}

	@MustBeInvokedByOverriders
	public final void removePlayer(Player player) throws PlayerQuitException {
		PlayerNoInThisSession.check(this, player);
		Debug.send(this, "try remove player");
		var canPlayerQuit = new PlayerQuitRequestEvent(player, getPlayers(), QuitType.PLUGIN_FORCE);
		getProcess().invokeAllListeners(canPlayerQuit);
		if (canPlayerQuit.isCanceled()) {
			assert canPlayerQuit.getCancelCause() != null;
			throw new PlayerQuitException(canPlayerQuit
					                              .getCancelCause()
					                              .message());
		}
		var otherPlayers = new ArrayList<>(getPlayers());

		SessionManager.removeRelation(player);
		players.remove(player);

		Debug.send(this, "remove player");
		var quitEvent = new PlayerDestructorEvent(player, otherPlayers, getPlayers(), InvokeType.CHANGE);
		getProcess().invokeAllListeners(quitEvent);
	}

	@MustBeInvokedByOverriders
	public final void setBoard(Board board) {
		//if (board.isUsed()) throw new BoardIsUsedException(); add to #Board.start()
		board.start(true, () -> {
			currentBoard.stop();
			currentBoard = board;
		});
	}

	public final @NotNull List<Player> getPlayers() {
		return players;
	}

	public final int getPlayerCount() {
		return players.size();
	}

	public final RemoteProcess getProcess() {
		return remoteProcess;
	}

	public final @NotNull MainProcess getMainProcess() {
		return mainProcess;
	}

	public final Board getCurrentBoard() {
		return currentBoard;
	}

	@Override
	public final @NotNull JavaPlugin getPlugin() {
		return plugin;
	}

	@Override
	public @Nullable RemoteProcess getParentProcess() {
		return null;
	}

	@Override
	public final @NotNull Session getSession() {
		return this;
	}

	public final void saveRecovery() {
		recoveryPlayerController.save();
	}

	private void startSessionSystemControllers(ComponentConstructorEvent event) {
		new SessionWatchDogController(this).start();
		new SessionSafeControlPlayers(this).start();
		new HideOtherPlayers(this).start();
		recoveryPlayerController = new RecoveryPlayerController(this);
		recoveryPlayerController.start();
		// run system tasks
		getProcess().runControlledTaskTimer(mainProcess::optimiseTasks, Time.Seconds(60), Time.Seconds((60)));
	}

	protected abstract @NotNull Board getDefaultStartBoard();
}