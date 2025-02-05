/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.trade;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TraderOptions {
    private String name = "Trader";
    private List<Trade> trades = new ArrayList<>();
    private EntityType entityType = EntityType.VILLAGER;

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Trade> trades() {
        return trades;
    }

    public void setTrades(List<Trade> trades) {
        this.trades = trades;
    }

    public EntityType entityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TraderOptions that = (TraderOptions) o;
        return Objects.equals(name, that.name) && Objects.equals(trades, that.trades) && entityType == that.entityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, trades, entityType);
    }
}
