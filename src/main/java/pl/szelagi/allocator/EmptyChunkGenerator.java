/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.allocator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class EmptyChunkGenerator extends ChunkGenerator {
    private static final BiomeProvider BIOME_PROVIDER = new BiomeProvider() {
        private final List<Biome> biomeList = List.of(Biome.PLAINS);

        @Override
        public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int i, int i1, int i2) {
            return Biome.PLAINS;
        }

        @Override
        public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
            return biomeList;
        }
    };

    @Override
    public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
        return BIOME_PROVIDER;
    }
}
