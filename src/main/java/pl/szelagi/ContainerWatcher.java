package pl.szelagi;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import pl.szelagi.component.container.Container;

import java.util.HashSet;

public class ContainerWatcher implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var container = Container.getForPlayer(player);
        if (container == null) return;
        container.removePlayer(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPluginDisable(PluginDisableEvent event) {
        if (!CraftContainers.instance().equals(event.getPlugin())) return;
        var containersCopy = new HashSet<>(Container.containers());

        for (var container : containersCopy) {
            container.stop();
        }
    }
}
