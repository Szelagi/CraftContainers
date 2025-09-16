/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.editor;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.SessionAPI;
import pl.szelagi.annotation.SingletonComponent;
import pl.szelagi.command.marker.MarkerAddHere;
import pl.szelagi.command.marker.MarkerRemoveId;
import pl.szelagi.component.Controller;
import pl.szelagi.manager.ContainerManager;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;

@SingletonComponent
public class MarkerBlockLogic extends Controller {
    public static final NamespacedKey CREATE_NBT_KEY = new NamespacedKey(SessionAPI.instance(), "addmarker");
    public static final NamespacedKey DELETE_NBT_KEY = new NamespacedKey(SessionAPI.instance(), "removemarker");
    public static final Material MARKER_MATERIAL = Material.REDSTONE_BLOCK;
    public static final Material MARKER_CLEAR_MATERIAL = Material.BARRIER;

    public MarkerBlockLogic(@NotNull BlueprintGameMap parent) {
        super(parent);
    }

    @Override
    public Listeners defineListeners() {
        return super.defineListeners().add(Logic.class);
    }

    public static class Logic implements Listener {
        @EventHandler(ignoreCancelled = true)
        public void onBlockPlace(BlockPlaceEvent event) {
            var type = event.getBlock().getType();
            var player = event.getPlayer();
            if (type != MARKER_MATERIAL && type != MARKER_CLEAR_MATERIAL) return;

            var session = ContainerManager.container(event.getPlayer());
            ListenerManager.first(session, getClass(), MarkerBlockLogic.class, markerBlockLogic -> {
                var componentSession = markerBlockLogic.container();
                var board = componentSession.gameMap();
                if (!(board instanceof BlueprintGameMap blueprintBoard))
                    throw new IllegalStateException("Board is not a blueprint board");

                var item = event.getPlayer().getEquipment().getItemInMainHand();
                var meta = item.getItemMeta();
                if (meta == null) return;

                var location = event.getBlock().getLocation();
                var container = meta.getPersistentDataContainer();
                var name = container.get(CREATE_NBT_KEY, PersistentDataType.STRING);
                if (name != null) {
                    var marker = blueprintBoard.getMarkers().create(name, location);
                    var message = String.format(MarkerAddHere.SUCCESS_TEMPLATE, name, marker.getId());
                    player.sendMessage(message);
                }

                var clear = container.get(DELETE_NBT_KEY, PersistentDataType.BOOLEAN);
                if (clear != null) {
                    var markers = blueprintBoard.getMarkers().removeNearbyMarkers(location, 0.6);
                    for (var marker : markers) {
                        var message = String.format(MarkerRemoveId.SUCCESS_TEMPLATE, marker.getName(), marker.getId());
                        player.sendMessage(message);
                    }
                }

                event.setCancelled(true);
            });
        }

    }
}
