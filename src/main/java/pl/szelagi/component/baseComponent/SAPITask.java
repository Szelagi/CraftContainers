/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.baseComponent;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class SAPITask implements BukkitTask {
    private final BukkitTask bukkitTask;
    private final TaskSystem taskSystem;

    public SAPITask(TaskSystem taskSystem, BukkitTask bukkitTask) {
        this.taskSystem = taskSystem;
        this.bukkitTask = bukkitTask;
    }

    @Override
    public int getTaskId() {
        return bukkitTask.getTaskId();
    }

    @Override
    public @NotNull Plugin getOwner() {
        return bukkitTask.getOwner();
    }

    @Override
    public boolean isSync() {
        return bukkitTask.isSync();
    }

    @Override
    public boolean isCancelled() {
        return bukkitTask.isCancelled();
    }

    @Override
    public void cancel() {
        taskSystem.stopTask(this);
    }

    public BukkitTask getBukkitTask() {
        return bukkitTask;
    }
}
