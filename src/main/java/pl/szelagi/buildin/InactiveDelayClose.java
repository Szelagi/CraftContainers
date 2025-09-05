/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.component.Controller;
import pl.szelagi.util.timespigot.Time;

public class InactiveDelayClose extends Controller {
    private final Time delay;
    private BukkitTask closeTask;

    public InactiveDelayClose(@NotNull BaseComponent parent, Time delay) {
        super(parent);
        this.delay = delay;
    }

    public InactiveDelayClose(@NotNull BaseComponent parent) {
        this(parent, Time.seconds(120));
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        runTaskTimer(this::check, Time.seconds(30), Time.seconds(30));
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        if (closeTask != null) {
            closeTask.cancel();
        }
    }

    private void check() {
        if (closeTask != null) return;
        if (!session().players().isEmpty()) return;
        closeTask = runTaskLater(() -> session().stop(), delay);
    }

}
