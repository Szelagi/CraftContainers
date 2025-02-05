/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.trade;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public record Trade(ItemStack buyItem, ItemStack sellItem) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Trade trade = (Trade) o;
        return Objects.equals(buyItem, trade.buyItem) && Objects.equals(sellItem, trade.sellItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(buyItem, sellItem);
    }
}
