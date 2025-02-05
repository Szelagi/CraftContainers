/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.trade;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.ISessionComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.event.component.ComponentConstructorEvent;
import pl.szelagi.event.component.ComponentDestructorEvent;
import pl.szelagi.manager.ControllerManager;
import pl.szelagi.manager.SessionManager;

import java.util.ArrayList;

/**
 * Controller responsible for creating an NPC on the map,
 * which allows players to trade items.
 */
public class TraderController extends Controller {
    private static final NamespacedKey TRADE_ID_KEY = new NamespacedKey("trade_controller", "trade_id");
    private final TraderOptions traderOptions;
    private final Location location;
    private @Nullable LivingEntity trader;

    public TraderController(ISessionComponent sessionComponent, TraderOptions traderOptions, Location location) {
        super(sessionComponent);
        this.traderOptions = traderOptions;
        this.location = location;
    }

    @Override
    public void componentConstructor(ComponentConstructorEvent event) {
        super.componentConstructor(event);

        // This event is managed by SessionAPI and triggers when the TradeController is created.
        // It contains the logic required to set up the trading shop.

        // Create a villager and configure its properties.
        trader = (LivingEntity) location.getWorld().spawnEntity(location, traderOptions.entityType());
        trader.setCustomName(traderOptions.name());
        trader.setCustomNameVisible(true);
        trader.setAI(false);
        trader.setInvulnerable(true);
    }

    @Override
    public void componentDestructor(ComponentDestructorEvent event) {
        super.componentDestructor(event);

        // This event is managed by SessionAPI and triggers when the TradeController is removed.
        // It contains the logic needed to remove the trading shop.
        if (trader != null) {
            trader.remove();
            trader = null;
        }
    }

    @Override
    public @Nullable Listener getListener() {
        // Assign a system event listener managed by SessionAPI.
        // The framework will activate this listener when necessary and release it when no longer needed.
        return new TraderListener();
    }

    /**
     * Inner static class for handling trader-related events.
     */
    private static class TraderListener implements Listener {
        private static void openTradeGUI(Player player, TraderOptions traderOptions) {
            int size = Math.max(9, (int) Math.ceil(traderOptions.trades().size() / 9.0) * 9);
            var tradeInventory = Bukkit.createInventory(null, size, Component.text(traderOptions.name()));

            for (int i = 0; i < traderOptions.trades().size(); i++) {
                var costItem = traderOptions.trades().get(i).sellItem();
                var formattedItem = traderOptions.trades().get(i).buyItem().clone();
                var meta = formattedItem.getItemMeta();
                if (meta == null) {
                    meta = Bukkit.getItemFactory().getItemMeta(formattedItem.getType());
                }

                if (meta != null) {
                    var lore = meta.lore();
                    if (lore == null) lore = new ArrayList<>();
                    lore.add(Component.text(""));
                    lore.add(Component.text("§7Price: §f" + formatItemName(costItem)));
                    meta.lore(lore);
                    meta.getPersistentDataContainer().set(TRADE_ID_KEY, PersistentDataType.INTEGER, traderOptions.hashCode());;
                    formattedItem.setItemMeta(meta);
                }
                tradeInventory.setItem(i, formattedItem);
            }

            player.openInventory(tradeInventory);
        }

        private static String formatItemName(ItemStack itemStack) {
            return itemStack.getAmount() + "x " + itemStack.getType().name().replace("_", " ").toLowerCase();
        }

        private static void attemptTrade(Player player, Trade trade) {
            var inventory = player.getInventory();
            if (inventory.containsAtLeast(trade.sellItem(), trade.sellItem().getAmount())) {
                inventory.removeItem(trade.sellItem());
                inventory.addItem(trade.buyItem());
                player.sendMessage("§aTrade successful! You received: " + formatItemName(trade.buyItem()) + "!");
            } else {
                player.sendMessage("§cYou are missing " + formatItemName(trade.sellItem()) + " to complete the trade!");
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onPlayerInteractWithTrader(PlayerInteractEntityEvent event) {
            Player player = event.getPlayer();
            // Filter events that belong to an active session.
            var session = SessionManager.getSession(player);
            if (session == null) return;

            var entity = event.getRightClicked();
            // Filter events to only those involving TraderControllers within the session.
            var controllers = ControllerManager.getControllers(session, TraderController.class);

            // Iterate through all TraderControllers in the session.
            for (var controller : controllers) {
                // Identify the specific controller linked to the event.
                if (controller.trader() != null && controller.trader().equals(entity)) {
                    openTradeGUI(player, controller.traderOptions());
                    break;
                }
            }
        }

        @EventHandler(ignoreCancelled = true)
        public void onInventoryTrade(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;
            // Filter events that belong to an active session.
            var session = SessionManager.getSession(player);
            if (session == null) return;

            var item = event.getCurrentItem();
            if (item == null) return;
            var tradeOptionsHash = item.getPersistentDataContainer().get(TRADE_ID_KEY, PersistentDataType.INTEGER);
            // Ignore events that do not contain trade metadata.
            if (tradeOptionsHash == null) return;

            event.setCancelled(true);

            // Filter events to only those involving TraderControllers within the session.
            var controllers = ControllerManager.getControllers(session, TraderController.class);
            for (var controller : controllers) {
                var options = controller.traderOptions();
                if (!tradeOptionsHash.equals(options.hashCode())) continue;
                // Identify the specific controller linked to the event.

                int slot = event.getSlot();
                if (slot < 0 || slot >= options.trades().size()) return;

                attemptTrade(player, options.trades().get(slot));
                break;
            }
        }
    }

    public TraderOptions traderOptions() {
        return traderOptions;
    }

    public Location location() {
        return location;
    }

    public @Nullable LivingEntity trader() {
        return trader;
    }
}
