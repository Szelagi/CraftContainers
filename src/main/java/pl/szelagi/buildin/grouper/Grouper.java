/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.grouper;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Grouper<T extends Group> {
    private final List<T> groups = new ArrayList<>();

    public List<T> groups() {
        return groups;
    }

    public void add(T group) {
        groups.add(group);
    }

    public boolean remove(T group) {
        return groups.remove(group);
    }

    public int size() {
        return groups.size();
    }

    public boolean hasPlayer(Player player) {
        return groups.stream().anyMatch(group -> group.hasPlayer(player));
    }

    public @Nullable T findPlayer(Player player) {
        return groups.stream().filter(group -> group.hasPlayer(player)).findFirst().orElse(null);
    }

}
