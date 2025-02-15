/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.process;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.event.SAPIEvent;
import pl.szelagi.event.SAPIListener;
import pl.szelagi.process.exception.MultiRegisterException;
import pl.szelagi.process.exception.NotFoundUnregisterException;
import pl.szelagi.util.timespigot.Time;

import java.util.List;

public interface IControlProcess {
	void registerListener(SAPIListener listener) throws MultiRegisterException;

	void unregisterListener(SAPIListener listener) throws NotFoundUnregisterException;

	/**
	 * invoke listeners only in process
	 */
	void invokeSelfListeners(SAPIEvent event);

	void invokeAllListeners(SAPIEvent event);

	@NotNull ProcessTask runControlledTask(@NotNull Runnable runnable);

	@NotNull ProcessTask runControlledTaskAsynchronously(@NotNull Runnable runnable);

	@NotNull ProcessTask runControlledTaskLater(@NotNull Runnable runnable, @NotNull Time laterTime);

	@NotNull ProcessTask runControlledTaskLaterAsynchronously(@NotNull Runnable runnable, @NotNull Time laterTime);

	@NotNull ProcessTask runControlledTaskTimer(@NotNull Runnable runnable, @NotNull Time laterTime, @NotNull Time repeatTime);

	@NotNull ProcessTask runControlledTaskTimerAsynchronously(@NotNull Runnable runnable, @NotNull Time laterTime, @NotNull Time repeatTime);

	void stopControlledTask(@NotNull ProcessTask processTask);

	List<ProcessTask> getTasks();

	List<SAPIListener> getListeners();
}