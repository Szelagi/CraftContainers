/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.minecraftVersion;

import org.bukkit.Bukkit;
import pl.szelagi.ConfigManager;

import java.util.regex.Pattern;

public class MinecraftVersion {
    private static final Pattern VALID_PATTERN = Pattern.compile("^\\d+\\.\\d+(\\.\\d+)?$");
    private static int major;
    private static int minor;
    private static int patch;

    private static boolean validateVersion(String version) {
        return (VALID_PATTERN.matcher(version).matches());
    }

    public static void initialize() throws IllegalArgumentException {
        var version = ConfigManager.config().minecraftVersion;
        if (version == null || version.equals("auto")) {
            version = Bukkit.getBukkitVersion().split("-")[0];
        }
        if (!validateVersion(version)) throw new IllegalArgumentException("Invalid version: " + version);
        var parts = version.split("\\.");
        major = Integer.parseInt(parts[0]);
        minor = Integer.parseInt(parts[1]);
        patch = parts.length == 3 ? Integer.parseInt(parts[2]) : 0;
    }

    public static boolean isGreaterOrEqual(int requiredMajor, int requiredMinor, int requiredPatch) {
        if (major > requiredMajor) return true;
        if (major < requiredMajor) return false;

        if (minor > requiredMinor) return true;
        if (minor < requiredMinor) return false;

        return (patch >= requiredPatch);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getVersion() {
        return major + "." + minor + "." + patch;
    }

}
