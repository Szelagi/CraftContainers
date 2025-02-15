/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.process;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.BaseComponent;
import pl.szelagi.component.ComponentType;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.component.session.Session;
import pl.szelagi.component.session.cause.ExceptionCause;
import pl.szelagi.event.SAPIEvent;
import pl.szelagi.event.SAPIListener;
import pl.szelagi.process.exception.MultiDestroyException;
import pl.szelagi.process.exception.MultiRegisterException;
import pl.szelagi.process.exception.NotFoundUnregisterException;
import pl.szelagi.util.ReverseStream;
import pl.szelagi.util.timespigot.Time;

import java.util.ArrayList;
import java.util.List;

public class RemoteProcess extends Process implements IControlProcess {
	private final @NotNull MainProcess parentMainProcess;
	private final @Nullable RemoteProcess parentRemoteProcess;
	private final ArrayList<ProcessTask> tasks = new ArrayList<>();
	private final ArrayList<SAPIListener> listeners = new ArrayList<>();
	private final @Nullable BaseComponent component;
	private final @Nullable ComponentType type;
	private final boolean hasComponent;

	public RemoteProcess(@NotNull MainProcess parentMainProcess) {
		super(parentMainProcess.getPlugin());
		this.parentMainProcess = parentMainProcess;
		this.parentRemoteProcess = null;
		this.component = null;
		this.type = null;
		hasComponent = false;
		parentMainProcess.registerRemoteProcess(this);
	}

	public RemoteProcess(@NotNull BaseComponent baseComponent) {
		super(baseComponent.getPlugin());
		this.parentMainProcess = baseComponent
				.getSession().getMainProcess();
		this.parentRemoteProcess = baseComponent.getParentProcess();
		this.component = baseComponent;
		this.type = ComponentType.toType(baseComponent);
		hasComponent = true;
		parentMainProcess.registerRemoteProcess(this);
		parentRemoteProcess.registerRemoteProcess(this);
	}

	public boolean isController() {
		assert !hasComponent || !(component instanceof Controller) || getComponent() instanceof Controller;
		return hasComponent && type == ComponentType.CONTROLLER;
	}

	public boolean isBoard() {
		assert getComponent() instanceof Board;
		return hasComponent && type == ComponentType.BOARD;
	}

	public boolean isSession() {
		assert getComponent() instanceof Session;
		return hasComponent && type == ComponentType.SESSION;
	}

	public @Nullable BaseComponent getComponent() {
		return component;
	}

	public @Nullable ComponentType getType() {
		return type;
	}

	public boolean hasComponent() {
		return hasComponent;
	}

	public @Nullable RemoteProcess getParentRemoteProcess() {
		return parentRemoteProcess;
	}

	public @NotNull MainProcess getParentMainProcess() {
		return parentMainProcess;
	}

	@Override
	public void destroy() throws MultiDestroyException {
		if (isDestroyed())
			throw new MultiDestroyException("");
		setDestroyed();

		destroyHierarchy();
		destroyListeners();
		destroyTasks();

		parentMainProcess.unregisterRemoteProcess(this);
		if (parentRemoteProcess != null)
			parentRemoteProcess.unregisterRemoteProcess(this);
	}

	public void destroyHierarchy() {
		var remoteProcesses = new ArrayList<>(getRemoteProcesses());
		for (var remoteProcess : remoteProcesses) {
			destroyByType(remoteProcess);
		}
	}

	private void destroyByType(RemoteProcess remoteProcess) {
		if (!remoteProcess.hasComponent()) {
			remoteProcess.destroy();
			return;
		}
		assert remoteProcess.getComponent() != null;
		assert remoteProcess.getType() != null;
		var component = remoteProcess.getComponent();
		switch (remoteProcess.getType()) {
			case CONTROLLER ->
					((Controller) component).stop();
			case BOARD ->
					((Board) component).stop();
			case SESSION ->
					((Session) component).stop(new ExceptionCause("session has been stopped by process"));
			default ->
					throw new RuntimeException("not defined component type");
		}
	}

	private void destroyByType() {

	}

	protected void destroyListeners() {
		listeners.clear();
	}

	protected void destroyTasks() {
		ArrayList<BukkitTask> cloneArrayListTasks = new ArrayList<>(tasks);
		for (var task : cloneArrayListTasks) {
			// this is ProcessTask, cancel method automatic remove task from list
			task.cancel();
		}
	}

	public List<ProcessTask> getTasks() {
		return tasks;
	}

	@Override
	public List<SAPIListener> getListeners() {
		return listeners;
	}

	public void optimiseTasks() {
		var unableTasks = new ArrayList<ProcessTask>();
		boolean isQueued;
		boolean isRunning;
		boolean isExist;
		boolean isCanceled;
		for (var task : getTasks()) {
			isQueued = getPlugin().getServer()
			                      .getScheduler()
			                      .isQueued(task.getTaskId());
			isRunning = getPlugin().getServer()
			                       .getScheduler()
			                       .isCurrentlyRunning(task.getTaskId());
			isExist = isQueued || isRunning;
			isCanceled = task.isCancelled();
			if (isCanceled || !isExist) {
				unableTasks.add(task);
			}
		}

		for (var task : unableTasks) {
			getTasks().remove(task);
		}
	}

	@Override
	public void registerListener(SAPIListener listener) throws MultiRegisterException {
		if (listeners.contains(listener))
			throw new MultiRegisterException("");
		listeners.add(listener);
	}

	@Override
	public void unregisterListener(SAPIListener listener) throws NotFoundUnregisterException {
		var status = listeners.remove(listener);
		if (!status)
			throw new NotFoundUnregisterException("");
	}

	public void invokeSelfListeners(SAPIEvent event) {
		getListeners().forEach(event::call);
	}

	public void invokeReverseSelfListeners(SAPIEvent event) {
		ReverseStream
				.reverse(getListeners().stream())
				.forEach(event::call);
	}

	@Override
	public void invokeAllListeners(SAPIEvent event) {
		// main process
		getParentMainProcess().invokeAllListeners(event);
	}

	@Override
	public @NotNull ProcessTask runControlledTask(@NotNull Runnable runnable) {
		var bukkitTask = getPlugin().getServer()
		                            .getScheduler()
		                            .runTask(getPlugin(), runnable);
		var processTask = new ProcessTask(this, bukkitTask);
		tasks.add(processTask);
		return processTask;
	}

	@Override
	public @NotNull ProcessTask runControlledTaskAsynchronously(@NotNull Runnable runnable) {
		var bukkitTask = getPlugin().getServer()
		                            .getScheduler()
		                            .runTaskAsynchronously(getPlugin(), runnable);
		var processTask = new ProcessTask(this, bukkitTask);
		tasks.add(processTask);
		return processTask;
	}

	@Override
	public @NotNull ProcessTask runControlledTaskLater(@NotNull Runnable runnable, @NotNull Time laterTime) {
		var bukkitTask = getPlugin().getServer()
		                            .getScheduler()
		                            .runTaskLater(getPlugin(), runnable, laterTime.toTicks());
		var processTask = new ProcessTask(this, bukkitTask);
		tasks.add(processTask);
		return processTask;
	}

	@Override
	public @NotNull ProcessTask runControlledTaskLaterAsynchronously(@NotNull Runnable runnable, @NotNull Time laterTime) {
		var bukkitTask = getPlugin().getServer()
		                            .getScheduler()
		                            .runTaskLaterAsynchronously(getPlugin(), runnable, laterTime.toTicks());
		var processTask = new ProcessTask(this, bukkitTask);
		tasks.add(processTask);
		return processTask;
	}

	@Override
	public @NotNull ProcessTask runControlledTaskTimer(@NotNull Runnable runnable, @NotNull Time laterTime, @NotNull Time repeatTime) {
		var bukkitTask = getPlugin().getServer()
		                            .getScheduler()
		                            .runTaskTimer(getPlugin(), runnable, laterTime.toTicks(), repeatTime.toTicks());
		var processTask = new ProcessTask(this, bukkitTask);
		tasks.add(processTask);
		return processTask;
	}

	@Override
	public @NotNull ProcessTask runControlledTaskTimerAsynchronously(@NotNull Runnable runnable, @NotNull Time laterTime, @NotNull Time repeatTime) {
		var bukkitTask = getPlugin().getServer()
		                            .getScheduler()
		                            .runTaskTimerAsynchronously(getPlugin(), runnable, laterTime.toTicks(), repeatTime.toTicks());
		var processTask = new ProcessTask(this, bukkitTask);
		tasks.add(processTask);
		return processTask;
	}

	@Override
	public void stopControlledTask(@NotNull ProcessTask processTask) {
		processTask.getBukkitTask().cancel();
		tasks.remove(processTask);
	}

	@Override
	public String toString() {
		return "RemoteProcess{" + "component=" + component + '}';
	}
}
