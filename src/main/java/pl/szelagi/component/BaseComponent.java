/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.baseexception.StartException;
import pl.szelagi.component.baseexception.StopException;
import pl.szelagi.component.baseexception.multi.MultiStartException;
import pl.szelagi.component.baseexception.multi.MultiStopException;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.component.session.Session;
import pl.szelagi.event.SAPIListener;
import pl.szelagi.event.component.ComponentConstructorEvent;
import pl.szelagi.event.component.ComponentDestructorEvent;
import pl.szelagi.event.player.requestChange.PlayerJoinRequestEvent;
import pl.szelagi.event.player.initialize.InvokeType;
import pl.szelagi.event.player.initialize.PlayerConstructorEvent;
import pl.szelagi.event.player.initialize.PlayerDestructorEvent;
import pl.szelagi.event.player.recovery.PlayerStateRecoveryEvent;
import pl.szelagi.process.RemoteProcess;
import pl.szelagi.util.Debug;
import pl.szelagi.util.IncrementalGenerator;
import pl.szelagi.util.PluginRegistry;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// Component must implement methods:
//    private void systemPlayerConstructor(Player player, InitializeType type)
//    private void systemPlayerDestructor(Player player, UninitializedType type)

public abstract class BaseComponent implements ISessionComponent, SAPIListener {
	private static final IncrementalGenerator incrementalGenerator = new IncrementalGenerator();
	private final UUID uuid = UUID.randomUUID();
	private final long id = incrementalGenerator.next();
	private final String name = generateName();
	private ComponentStatus status = ComponentStatus.NOT_INITIALIZED;
	private boolean isSynchronized = false;

	public @NotNull ComponentStatus status() {
		return status;
	}

	protected void setStatus(ComponentStatus status) {
		this.status = status;
	}

	public boolean isSynchronized() {
		return isSynchronized;
	}

	public void setSynchronized(boolean aSynchronized) {
		isSynchronized = aSynchronized;
	}

	public final UUID getUuid() {
		return uuid;
	}

	public final long getId() {
		return id;
	}

	@Override
	public @NotNull String getName() {
		return name;
	}

	public @NotNull String getIdentifier() {
		return getName() + ":" + getId();
	}

	private char getComponentTypeChar() {
		if (this instanceof Controller)
			return 'C';
		if (this instanceof Board)
			return 'B';
		if (this instanceof Session)
			return 'S';
		return '-';
	}

	private String generateName() {
		var currentJarFile = new File(this
				                              .getClass()
				                              .getProtectionDomain()
				                              .getCodeSource()
				                              .getLocation()
				                              .getFile());
		var plugin = PluginRegistry.getPlugin(currentJarFile.getName());
		var pluginName = plugin != null ? plugin.getName() : currentJarFile.getName();
		return this.getClass()
		           .getSimpleName() + getComponentTypeChar() + '#' + pluginName;
	}

	protected final void callBukkitEvent(Event event) {
		getPlugin().getServer().getScheduler()
		           .runTask(getPlugin(), () -> getPlugin()
				           .getServer()
				           .getPluginManager()
				           .callEvent(event));
	}

	protected final void syncBukkitTask(Runnable runnable) {
		getPlugin().getServer().getScheduler()
		           .runTask(getPlugin(), runnable);
	}

	private List<Player> getOtherPlayers(Player player, Collection<Player> allPlayers) {
		return allPlayers.stream()
		                 .filter(p -> !p.equals(player))
		                 .collect(Collectors.toCollection(ArrayList::new));
	}

	public final void invokeSelfPlayerConstructors() {
		var players = getSession().getPlayers();
		var clone = new ArrayList<>(players);
		clone.forEach((p) -> invokeSelfPlayerConstructor(p, players));
	}

	protected final void invokeSelfPlayerDestructors() {
		var players = getSession().getPlayers();
		var clone = new ArrayList<>(players);
		clone.forEach((p) -> invokeSelfPlayerDestructor(p, players));
	}

	protected final void invokeSelfComponentConstructor() {
		getProcess().invokeSelfListeners(new ComponentConstructorEvent(this, getSession().getPlayers()));
	}

	protected final void invokeSelfComponentDestructor() {
		getProcess().invokeSelfListeners(new ComponentDestructorEvent(this, getSession().getPlayers()));
	}

	private void invokeSelfPlayerConstructor(Player player, Collection<Player> allPlayers) {
		var otherPlayer = getOtherPlayers(player, allPlayers);
		var event = new PlayerConstructorEvent(player, otherPlayer, allPlayers, InvokeType.SELF);
		getProcess().invokeSelfListeners(event);
	}

	private void invokeSelfPlayerDestructor(Player player, Collection<Player> allPlayers) {
		var otherPlayer = getOtherPlayers(player, allPlayers);
		var event = new PlayerDestructorEvent(player, otherPlayer, allPlayers, InvokeType.SELF);
		getProcess().invokeReverseSelfListeners(event);
	}

	@MustBeInvokedByOverriders
	public void componentConstructor(ComponentConstructorEvent event) {
		Debug.send(this, "component constructor");
	}

	@MustBeInvokedByOverriders
	public void componentDestructor(ComponentDestructorEvent event) {
		Debug.send(this, "component destructor");
	}

	@MustBeInvokedByOverriders
	public void playerConstructor(PlayerConstructorEvent event) {
		Debug.send(this, "player constructor: " + event
				.getPlayer().getName());
	}

	@MustBeInvokedByOverriders
	public void playerDestructor(PlayerDestructorEvent event) {
		Debug.send(this, "player destructor: " + event
				.getPlayer().getName());
	}

	@MustBeInvokedByOverriders
	public void playerCanJoin(PlayerJoinRequestEvent event) {}

	@MustBeInvokedByOverriders
	public void playerCanQuit(PlayerQuitEvent event) {}

	@MustBeInvokedByOverriders
	public void playerDestructorRecovery(PlayerStateRecoveryEvent event) {}

	public abstract @Nullable RemoteProcess getParentProcess();

	@Override
	public String toString() {
		return "BaseComponent{" + "id=" + id + ", name='" + name + '\'' + '}';
	}

	protected final void validateStartable() throws StartException {
		if (getParentProcess() == null && !(this instanceof Session))
			throw new StartException("Failed attempt to start " + getName() + ", because parent is not running");

		if (status() != ComponentStatus.NOT_INITIALIZED && status() != ComponentStatus.SHUTDOWN)
			throw new MultiStartException(this);
	}

	protected final void validateDisableable() throws StopException {
		if (status() != ComponentStatus.RUNNING)
			throw new MultiStopException(this);
	}

	protected final void validateNotStartedBefore() throws StartException {
		if (status() != ComponentStatus.NOT_INITIALIZED)
			throw new StartException("board start used");
	}

	protected final void recursivelyInvokeOnAllProcesses(Collection<RemoteProcess> processes, Consumer<RemoteProcess> consumer) {
		Deque<RemoteProcess> queue = new ArrayDeque<>(processes);

		while (!queue.isEmpty()) {
			RemoteProcess currentProcess = queue.poll();
			consumer.accept(currentProcess);
			queue.addAll(currentProcess.getRemoteProcesses());
		}
	}

	private static final Consumer<RemoteProcess> invokePlayerConstructorOnProcess = (process -> {
		var component = process.getComponent();
		if (component == null)
			return;
		component.setSynchronized(true);
		component.invokeSelfPlayerConstructors();
	});

	protected void recursivelyInvokePlayerConstructor(Collection<RemoteProcess> processes) {
		recursivelyInvokeOnAllProcesses(processes, invokePlayerConstructorOnProcess);
	}

	protected void invokePlayerConstructorOnProcess(RemoteProcess processes) {
		invokePlayerConstructorOnProcess.accept(processes);
	}

	/**
	 * This method is used to recursively invoke the constructors of components,
	 * as well as the constructors of player objects, in the exact same order.
	 */
	protected void invokeSelf() {
		var afterChildrenProcesses = getProcess()
				.getRemoteProcesses().size();

		invokeSelfComponentConstructor();

		var beforeChildrenProcesses = getProcess()
				.getRemoteProcesses().size();

		var hasChildrenProcesses = beforeChildrenProcesses > afterChildrenProcesses;
		if (!hasChildrenProcesses) {

			Predicate<RemoteProcess> applicable = (process) -> {
				if (process == null)
					return false;
				var component = process.getComponent();
				if (component == null)
					return false;
				return !component.isSynchronized();
			};

			var firstProcess = getProcess();
			while (applicable.test(firstProcess.getParentRemoteProcess())) {
				firstProcess = firstProcess.getParentRemoteProcess();
			}

			invokePlayerConstructorOnProcess(firstProcess);
			recursivelyInvokePlayerConstructor(firstProcess.getRemoteProcesses());
		}
	}
}
