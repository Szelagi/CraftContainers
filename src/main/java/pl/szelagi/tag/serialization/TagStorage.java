/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.tag.serialization;

import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.relative.RelativePoint;

import java.io.Serializable;
import java.util.List;

public record TagStorage(@NotNull String tag,
                         @NotNull RelativePoint relativePoint,
                         @NotNull BlockFace blockFace,
                         @NotNull List<String> args) implements Serializable {
}
