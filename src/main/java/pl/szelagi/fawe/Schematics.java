/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.fawe;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.allocator.IAllocate;
import pl.szelagi.fawe.massive.FlexSchematic;

import java.io.File;

public class Schematics {
    private static void massiveLoader(FlexSchematic schematic, ClipboardAndHolder cah) {
        var origin = schematic.getOrigin();
        FaweOperations.massivePaste(cah.holder(), origin);
    }

    private static void massiveCleaner(FlexSchematic schematic, Location min, Location max) {
        FaweOperations.massiveClearRegion(min, max);
    }

    public static FlexSchematic newMassive(@NotNull File file, @NotNull IAllocate space, @NotNull Location origin) {
        return new FlexSchematic(file, space, origin, Schematics::massiveLoader, Schematics::massiveCleaner);
    }
}
