/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.spatial;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;
import java.util.function.Consumer;

public class BlockMethods {
    public static final Set<Material> AIR_MATERIALS = new HashSet<>(Arrays.asList(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.LAVA, Material.WATER));

    public static boolean isAirMaterial(Material material) {
        return AIR_MATERIALS.contains(material);
    }

    public static List<Block> getBlocksIn(Location loc1, Location loc2) {
        var list = new LinkedList<Block>();
        BlockMethods.eachBlocks(loc1, loc2, list::add);
        return new ArrayList<>(list);
    }

    public static void eachBlocks(Location loc1, Location loc2, Consumer<Block> predicate) {
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        Block block;

        for (int x = minX; x <= maxX; x++)
            for (int y = minY; y <= maxY; y++)
                for (int z = minZ; z <= maxZ; z++) {
                    block = loc1.getWorld()
                            .getBlockAt(x, y, z);
                    predicate.accept(block);
                }
    }
}
