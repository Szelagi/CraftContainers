/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi;

public class Config {
    private static final String MINECRAFT_VERSION = "minecraft_version";
    public final String minecraftVersion;

    public static final String DEBUG_ON_START = "debug_on_start";
    public final boolean debugOnStart;

    public static final String MAX_REGION_SIZE = "max-region-size";
    public final int maxRegionSize;

    public static final String DISTANCE_BETWEEN_REGIONS = "distance-between-regions";
    public final int distanceBetweenRegions;

    public static final String LAZY_WORLD_MAX_ALLOCS = "lazy-world-max-allocs";
    public final int lazyWorldMaxAllocs;

    public static final String LAZY_BLUEPRINT_MAX_ALLOCS = "lazy-blueprint-max-allocs";

    public final int lazyBlueprintMaxAllocs;

    private void createConfig(CraftContainers cc) {
        var config = cc.getConfig();
        config.addDefault(MINECRAFT_VERSION, "auto");
        config.addDefault(DEBUG_ON_START, false);
        config.addDefault(MAX_REGION_SIZE, 500);
        config.addDefault(DISTANCE_BETWEEN_REGIONS, 500);
        config.addDefault(LAZY_WORLD_MAX_ALLOCS, 1000);
        config.addDefault(LAZY_BLUEPRINT_MAX_ALLOCS, 60);
        cc.saveConfig();
    }

    public Config(CraftContainers cc) {
        createConfig(cc);

        var config = cc.getConfig();
        minecraftVersion = config.getString(MINECRAFT_VERSION);
        debugOnStart = config.getBoolean(DEBUG_ON_START);
        maxRegionSize = config.getInt(MAX_REGION_SIZE);
        distanceBetweenRegions = config.getInt(DISTANCE_BETWEEN_REGIONS);
        lazyWorldMaxAllocs = config.getInt(LAZY_WORLD_MAX_ALLOCS);
        lazyBlueprintMaxAllocs = config.getInt(LAZY_BLUEPRINT_MAX_ALLOCS);
    }
}
