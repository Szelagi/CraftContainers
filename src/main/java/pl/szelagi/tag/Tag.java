/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.tag;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.relative.RelativeLocation;

import java.io.Serializable;
import java.util.List;

@Deprecated
public class Tag implements Serializable {
    @NotNull
    private final String name;
    @NotNull
    private final RelativeLocation relativeLocation;
    @NotNull
    private final BlockFace blockFace;
    @NotNull
    private final List<String> args;

    public Tag(@NotNull String name, @NotNull RelativeLocation relativeLocation, @NotNull BlockFace blockFace, @NotNull List<String> args) {
        this.name = name;
        this.blockFace = blockFace;
        this.args = args;

        BlockFaceMapper.updateYaw(relativeLocation, blockFace);
        this.relativeLocation = relativeLocation;
    }

    @Deprecated
    public @NotNull String getName() {
        return name;
    }

    @Deprecated
    public @NotNull Location getLocation() {
        return relativeLocation;
    }

    @Deprecated
    public @NotNull RelativeLocation getRelativeLocation() {
        return relativeLocation;
    }

    @Deprecated
    public @NotNull BlockFace getBlockFace() {
        return blockFace;
    }

    @Deprecated
    public @NotNull List<String> getArgs() {
        return args;
    }

    public @NotNull Location location() {
        return relativeLocation;
    }

    public @NotNull RelativeLocation relativeLocation() {
        return relativeLocation;
    }

    public @NotNull String name() {
        return name;
    }

    public BlockFace blockFace() {
        return blockFace;
    }

    public @NotNull List<String> args() {
        return args;
    }

    public @NotNull Location centeredXZ() {
        var location = location().clone();
        return location.add(new Vector(0.5, 0, 0.5));
    }

    public @NotNull Location centeredXYZ() {
        var location = location().clone();
        return location.add(new Vector(0.5, 0.5, 0.5));
    }
}
