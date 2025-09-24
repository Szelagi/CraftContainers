/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util.pool;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import pl.szelagi.CraftContainers;

import java.util.LinkedList;
import java.util.Queue;

public abstract class SimplePool<T> extends Pool<T> {
    private final Queue<T> pool = new LinkedList<>();
    private final int minSize;
    private final BukkitTask task;

    public SimplePool(int minSize) {
        this.minSize = minSize;

        for (int i = 0; i < minSize; i++) {
            pool.add(creator());
        }

        task = Bukkit.getScheduler().runTaskTimer(CraftContainers.instance(), this::balance, 0L, 100L);
    }

    public T acquire() {
        T element;
        if (!pool.isEmpty()) {
            return pool.poll();
        } else {
            return creator();
        }
    }

    public void destroy() {
        task.cancel();
        for (var element : pool) {
            releaser(element);
        }
        pool.clear();
    }

    private void balance() {
        while (pool.size() < minSize) {
            pool.add(creator());
        }
    }
}
