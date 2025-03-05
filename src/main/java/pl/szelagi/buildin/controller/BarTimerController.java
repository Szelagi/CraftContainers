/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.util.timespigot.Time;

public class BarTimerController extends Controller {
    private BossBarController bossBarController;
    private final int ticks;
    private final String template;
    private final Runnable runnable;
    private int remainingTicks;

    public BarTimerController(BaseComponent baseComponent, Time time, String template, @Nullable Runnable runnable) {
        super(baseComponent);
        this.ticks = time.toTicks();
        this.template = template;
        this.runnable = runnable;
    }

    public BarTimerController(BaseComponent baseComponent, Time time, String template) {
        super(baseComponent);
        this.ticks = time.toTicks();
        this.template = template;
        this.runnable = null;
    }

    public BarTimerController(BaseComponent baseComponent, Time time, Runnable runnable) {
        super(baseComponent);
        this.ticks = time.toTicks();
        this.template = "Time remaining: %.2fs";
        this.runnable = runnable;
    }

    public BarTimerController(BaseComponent baseComponent, Time time) {
        super(baseComponent);
        this.ticks = time.toTicks();
        this.template = "Time remaining: %.2fs";
        this.runnable = null;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        remainingTicks = ticks;

        bossBarController = new BossBarController(this);
        bossBarController.start();

        bossBarController.bossBar()
                .setStyle(BarStyle.SEGMENTED_12);
        bossBarController.bossBar()
                .setColor(BarColor.BLUE);

        runTaskTimer(this::next, Time.zero(), Time.ticks(5));
    }

    private void next() {
        remainingTicks -= 5;
        if (remainingTicks <= 0) {
            if (runnable != null) {
                runnable.run();
            }
            stop();
            return;
        }
        update();
    }

    private void update() {
        if (bossBarController == null)
            return;

        var bar = bossBarController.bossBar();
        var percent = remainingTicks / (double) ticks;
        var percentNormalized = Math.min(Math.max(0, percent), 1);
        var remaining = (ticks - (ticks - remainingTicks)) / 20d;
        var formattedMessage = String.format(template, remaining);

        bar.setProgress(percentNormalized);
        bar.setTitle(formattedMessage);
    }
}
