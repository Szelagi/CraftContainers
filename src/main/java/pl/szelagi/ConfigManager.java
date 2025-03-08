/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi;

public class ConfigManager {
    public static final String MAX_BOARD_SIZE = "max-board-size";
    public static final String DISTANCE_BETWEEN_MAPS = "distance-between-maps";
    public static final String MINECRAFT_VERSION = "minecraft_version";
    public static final String DEBUG_ON_START = "debug_on_start";

    private static Config config;

    public static void init(SessionAPI sapi) {
        createConfig(sapi);
        loadConfig(sapi);
    }

    private static void createConfig(SessionAPI sapi) {
        var config = sapi.getConfig();
        config.addDefault(MAX_BOARD_SIZE, 300);
        config.addDefault(DISTANCE_BETWEEN_MAPS, 500);
        config.addDefault(MINECRAFT_VERSION, "auto");
        config.addDefault(DEBUG_ON_START, false);
        config.options().copyDefaults(true);
        sapi.saveConfig();
    }

    private static void loadConfig(SessionAPI sapi) {
        var fileConfiguration = sapi.getConfig();
        config = new Config(fileConfiguration);
    }

    public static Config config() {
        return config;
    }
}
