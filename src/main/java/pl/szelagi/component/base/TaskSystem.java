/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.base;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.util.timespigot.Time;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TaskSystem {
    private final Plugin plugin;
    private final Set<ComponentTask> tasks = new HashSet<>();

    public TaskSystem(Plugin plugin) {
        this.plugin = plugin;
    }

    public void destroy() {
        var toDestroy = new HashSet<>(tasks);
        for (var task : toDestroy) {
            task.cancel();
        }
    }

    public @NotNull ComponentTask runTask(@NotNull Runnable runnable) {
        var bukkitTask = plugin.getServer()
                .getScheduler()
                .runTask(plugin, runnable);
        var processTask = new ComponentTask(this, bukkitTask);
        tasks.add(processTask);
        return processTask;
    }

    public @NotNull ComponentTask runTaskAsync(@NotNull Runnable runnable) {
        var bukkitTask = plugin.getServer()
                .getScheduler()
                .runTaskAsynchronously(plugin, runnable);
        var processTask = new ComponentTask(this, bukkitTask);
        tasks.add(processTask);
        return processTask;
    }

    public @NotNull ComponentTask runTaskLater(@NotNull Runnable runnable, @NotNull Time laterTime) {
        var bukkitTask = plugin.getServer()
                .getScheduler()
                .runTaskLater(plugin, runnable, laterTime.toTicks());
        var processTask = new ComponentTask(this, bukkitTask);
        tasks.add(processTask);
        return processTask;
    }

    public @NotNull ComponentTask runTaskLaterAsync(@NotNull Runnable runnable, @NotNull Time laterTime) {
        var bukkitTask = plugin.getServer()
                .getScheduler()
                .runTaskLaterAsynchronously(plugin, runnable, laterTime.toTicks());
        var processTask = new ComponentTask(this, bukkitTask);
        tasks.add(processTask);
        return processTask;
    }

    public @NotNull ComponentTask runTaskTimer(@NotNull Runnable runnable, @NotNull Time laterTime, @NotNull Time repeatTime) {
        var bukkitTask = plugin.getServer()
                .getScheduler()
                .runTaskTimer(plugin, runnable, laterTime.toTicks(), repeatTime.toTicks());
        var processTask = new ComponentTask(this, bukkitTask);
        tasks.add(processTask);
        return processTask;
    }

    public @NotNull ComponentTask runTaskTimerAsync(@NotNull Runnable runnable, @NotNull Time laterTime, @NotNull Time repeatTime) {
        var bukkitTask = plugin.getServer()
                .getScheduler()
                .runTaskTimerAsynchronously(plugin, runnable, laterTime.toTicks(), repeatTime.toTicks());
        var processTask = new ComponentTask(this, bukkitTask);
        tasks.add(processTask);
        return processTask;
    }

    public void stopTask(@NotNull ComponentTask ComponentTask) {
        ComponentTask.getBukkitTask().cancel();
        tasks.remove(ComponentTask);
    }

    public void optimiseTasks() {
        var unableTasks = new ArrayList<ComponentTask>();
        boolean isQueued;
        boolean isRunning;
        boolean isExist;
        boolean isCanceled;
        for (var task : tasks) {
            isQueued = plugin.getServer()
                    .getScheduler()
                    .isQueued(task.getTaskId());
            isRunning = plugin.getServer()
                    .getScheduler()
                    .isCurrentlyRunning(task.getTaskId());
            isExist = isQueued || isRunning;
            isCanceled = task.isCancelled();
            if (isCanceled || !isExist) {
                unableTasks.add(task);
            }
        }

        for (var task : unableTasks) {
            tasks.remove(task);
        }
    }

}
