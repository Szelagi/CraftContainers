/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import pl.szelagi.util.timespigot.Time;

import java.util.concurrent.CountDownLatch;

public class Scheduler {
    private static final BukkitScheduler scheduler = Bukkit.getScheduler();

    public static BukkitTask runTask(Runnable runnable) {
        return scheduler.runTask(plugin(), runnable);
    }

    public static BukkitTask runTaskLater(Runnable runnable, Time delay) {
        return scheduler.runTaskLater(plugin(), runnable, delay.toTicks());
    }

    public static BukkitTask runTaskAsync(Runnable runnable) {
        return scheduler.runTaskAsynchronously(plugin(), runnable);
    }

    public static BukkitTask runTaskLaterAsync(Runnable runnable, Time delay) {
        return scheduler.runTaskLaterAsynchronously(plugin(), runnable, delay.toTicks());
    }

    public static BukkitTask runTaskTimer(Runnable runnable, Time delay, Time period) {
        return scheduler.runTaskTimer(plugin(), runnable, delay.toTicks(), period.toTicks());
    }

    public static BukkitTask runTaskTimerAsync(Runnable runnable, Time delay, Time period) {
        return scheduler.runTaskTimerAsynchronously(plugin(), runnable, delay.toTicks(), period.toTicks());
    }

    public static BukkitTask runAndWait(Runnable runnable) {
        var latch = new CountDownLatch(1);

        var task = Scheduler.runTask(() -> {
            try {
                runnable.run();
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Task interrupted", e);
        }

        return task;
    }

    private static CraftContainers plugin() {
        return CraftContainers.instance();
    }
}
