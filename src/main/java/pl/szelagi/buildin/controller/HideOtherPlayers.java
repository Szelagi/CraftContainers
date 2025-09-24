/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.container.Container;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.event.internal.player.PlayerDestructor;
import pl.szelagi.component.Controller;
import pl.szelagi.manager.ContainerManager;
import pl.szelagi.manager.listener.AdaptedListener;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;

public class HideOtherPlayers extends Controller {
    public HideOtherPlayers(Component component) {
        super(component);
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        for (var player : plugin().getServer()
                .getOnlinePlayers())
            if (!players().contains(player))
                event.player()
                        .hidePlayer(plugin(), player);

        for (var player : players())
            player.showPlayer(plugin(), event.player());
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        for (var player : plugin().getServer()
                .getOnlinePlayers())
            event.player()
                    .showPlayer(plugin(), player);

        for (var player : players())
            player.hidePlayer(plugin(), event.player());
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(MyListener.class);
    }

    public static class MyListener implements AdaptedListener {
        @EventHandler(ignoreCancelled = true)
        public void onPlayerJoin(PlayerJoinEvent event) {
            for (var container : Container.containers()) {
                first(container, HideOtherPlayers.class, hideOtherPlayers -> {
                    for (var player : hideOtherPlayers.players())
                        player.hidePlayer(hideOtherPlayers.plugin(), event.getPlayer());
                });
            }
        }
    }
}
