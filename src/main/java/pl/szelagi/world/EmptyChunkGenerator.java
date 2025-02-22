/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.world;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class EmptyChunkGenerator extends ChunkGenerator {
    @Override
    @NotNull
    public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
        var chunkData = createChunkData(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biome.setBiome(x, 0, z, Biome.MEADOW);
            }
        }
        return chunkData;
    }
}
