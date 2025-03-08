/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi;

import org.bukkit.configuration.file.FileConfiguration;

import static pl.szelagi.ConfigManager.*;
import static pl.szelagi.ConfigManager.DEBUG_ON_START;

public class Config {
    public final FileConfiguration fileConfiguration;
    public final int maxBoardSize;
    public final int distanceBetweenMaps;
    public final String minecraftVersion;
    public final boolean debugOnStart;

    public Config(FileConfiguration config) {
        fileConfiguration = config;
        maxBoardSize = config.getInt(MAX_BOARD_SIZE);
        distanceBetweenMaps = config.getInt(DISTANCE_BETWEEN_MAPS);
        minecraftVersion = config.getString(MINECRAFT_VERSION);
        debugOnStart = config.getBoolean(DEBUG_ON_START);
    }
}
