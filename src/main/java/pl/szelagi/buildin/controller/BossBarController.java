/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.component.ComponentDestructor;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.event.internal.player.PlayerDestructor;
import pl.szelagi.component.Controller;

public class BossBarController extends Controller {
    private BossBar bossBar;
    private final BarColor templateBarColor;
    private final BarStyle templateBarStyle;
    private final String templateBarLabel;

    public BossBarController(Component component) {
        super(component);
        this.templateBarColor = BarColor.WHITE;
        this.templateBarStyle = BarStyle.SOLID;
        this.templateBarLabel = "";
    }

    public BossBarController(Component component, BarColor templateBarColor, BarStyle templateBarStyle) {
        super(component);
        this.templateBarColor = templateBarColor;
        this.templateBarStyle = templateBarStyle;
        this.templateBarLabel = "";
    }

    public BossBarController(Component component, BarColor templateBarColor, BarStyle templateBarStyle, String templateBarLabel) {
        super(component);
        this.templateBarColor = templateBarColor;
        this.templateBarStyle = templateBarStyle;
        this.templateBarLabel = templateBarLabel;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        bossBar = Bukkit.createBossBar(templateBarLabel, templateBarColor, templateBarStyle);
        bossBar.setVisible(true);
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar.setVisible(false);
            bossBar = null;
        }
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        if (bossBar != null) {
            bossBar.addPlayer(event.player());
        }
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        if (bossBar != null) {
            bossBar.removePlayer(event.player());
        }
    }

    public BossBar bossBar() {
        return bossBar;
    }
}
