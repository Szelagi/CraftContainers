/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.state.manual;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.state.InstanceCreator;
import pl.szelagi.state.PlayerState;

import java.io.Serializable;
import java.util.HashMap;
import java.util.function.Predicate;

public class PlayerManualContainer<T extends PlayerState> implements Serializable {
    private final HashMap<Player, T> map = new HashMap<>();

    @NotNull
    public T create(@NotNull Player player, @NotNull InstanceCreator<T> creator) throws ManualContainerException {
        if (map.containsKey(player))
            throw new ManualContainerException("player " + player + " multi initialization");
        map.put(player, creator.get(player));
        return get(player);
    }

    @NotNull
    public T get(@NotNull Player player) throws ManualContainerException {
        var record = map.get(player);
        if (record == null)
            throw new ManualContainerException("player " + player + " is not initialized");
        return record;
    }

    @NotNull
    public T remove(@NotNull Player player) throws ManualContainerException {
        var record = map.remove(player);
        if (record == null)
            throw new ManualContainerException("remove not exists player " + player);
        return record;
    }

    public boolean isExists(@NotNull Player player) {
        return map.containsKey(player);
    }

    public @Nullable T find(Predicate<T> predicate) {
        return map.values().stream()
                .filter(predicate).findFirst()
                .orElse(null);
    }
}
