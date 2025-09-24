/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.manager;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import pl.szelagi.component.container.Container;

import java.util.Set;

@Deprecated
public class ContainerManager {
    public static boolean inSession(Player p) {
        return Container.getForPlayer(p) != null;
    }

    @Nullable
    public static Container container(Player p) {
        return Container.getForPlayer(p);
    }

    @Nullable
    public static <T extends Container> Container container(Player p, Class<T> classType) {
        return Container.getForPlayer(p, classType);
    }

    public static @NotNull @Unmodifiable Set<Container> containers() {
        return Container.containers();
    }
}
