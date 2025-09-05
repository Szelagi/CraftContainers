/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.szelagi.util.timespigot.Time;
import pl.szelagi.util.timespigot.TimeUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for managing volatile player cooldowns.
 * <p>
 * Stores cooldowns in memory and provides methods to start, check, delete,
 * and retrieve remaining cooldown times. Automatically optimizes expired cooldowns.
 */
public class CooldownVolatile {
    private static final int OPTIMIZE_TIMER_TICKS = 60 * 20 * 2;
    private static JavaPlugin plugin;
    private static final Map<Player, Map<NamespacedKey, Long>> map = new HashMap<>();

    /** Initializes the cooldown system and schedules periodic cleanup. */
    public static void initialize(JavaPlugin p) {
        plugin = p;
        plugin.getServer().getScheduler()
                .runTaskTimer(plugin, CooldownVolatile::optimize, OPTIMIZE_TIMER_TICKS, OPTIMIZE_TIMER_TICKS);
    }

    /**
     * Starts a cooldown for a player with the given key and duration.
     *
     * @param player the player to apply the cooldown
     * @param key    the unique key identifying the cooldown
     * @param span   the duration of the cooldown
     */
    public static void startCooldown(Player player, NamespacedKey key, Time span) {
        var playerCooldownMap = map.computeIfAbsent(player, k -> new HashMap<>());
        var millisEnd = System.currentTimeMillis() + span.toMillis();
        playerCooldownMap.put(key, millisEnd);
    }

    @Deprecated
    public static void startCooldown(Player player, String name, Time span) {
        startCooldown(player, NamespacedKey.minecraft(name), span);
    }

    /**
     * Checks if a player can use a something identified by the key.
     *
     * @param player the player to check
     * @param key    the cooldown key
     * @return true if the player can use it, false if still on cooldown
     */
    public static boolean canUse(Player player, NamespacedKey key) {
        var playerCooldownMap = map.get(player);
        if (playerCooldownMap == null) return true;
        var cooldown = playerCooldownMap.get(key);
        if (cooldown == null) return true;
        return System.currentTimeMillis() >= cooldown;
    }

    @Deprecated
    public static boolean canUse(Player player, String name) {
        return canUse(player, NamespacedKey.minecraft(name));
    }

    /** Deletes a specific cooldown for a player. */
    public static void deleteCooldown(Player player, NamespacedKey key) {
        var playerCooldownMap = map.get(player);
        if (playerCooldownMap == null) return;
        playerCooldownMap.remove(key);
    }

    @Deprecated
    public static void deleteCooldown(Player player, String name) {
        deleteCooldown(player, NamespacedKey.minecraft(name));
    }

    /**
     * Checks if a player can use a something and starts the cooldown if possible.
     *
     * @param player the player to check
     * @param key    the cooldown key
     * @param span   the duration of the cooldown
     * @return true if the cooldown was successfully started, false if still on cooldown
     */
    public static boolean canUseAndStart(Player player, NamespacedKey key, Time span) {
        if (!canUse(player, key)) return false;
        startCooldown(player, key, span);
        return true;
    }

    @Deprecated
    public static boolean canUseAndStart(Player player, String name, Time span) {
        return canUseAndStart(player, NamespacedKey.minecraft(name), span);
    }

    /**
     * Returns the remaining time before a player can use something again.
     *
     * @param player the player to check
     * @param key    the cooldown key
     * @return the remaining {@link Time} span (0 if cooldown expired)
     */
    public static Time getTimeSpanToUse(Player player, NamespacedKey key) {
        var playerCooldownMap = map.get(player);
        if (playerCooldownMap == null)
            return Time.zero();

        var millisEnd = playerCooldownMap.get(key);
        if (millisEnd == null)
            return Time.zero();

        var deltaMillis = millisEnd - System.currentTimeMillis();
        if (deltaMillis <= 0)
            return Time.zero();

        return new Time(deltaMillis, TimeUnit.MILLIS);
    }

    @Deprecated
    public static Time getTimeSpanToUse(Player player, String name) {
        return getTimeSpanToUse(player, NamespacedKey.minecraft(name));
    }

    /** Cleans up expired cooldowns from memory. Called periodically. */
    private static void optimize() {
        Player player;
        Map<NamespacedKey, Long> playerCooldownMap;
        var currentMillis = System.currentTimeMillis();
        for (var entry : map.entrySet()) {
            player = entry.getKey();
            playerCooldownMap = entry.getValue();
            for (var cooldown : playerCooldownMap.entrySet()) {
                if (currentMillis >= cooldown.getValue()) {
                    playerCooldownMap.remove(cooldown.getKey());
                }
            }
            if (playerCooldownMap.isEmpty()) {
                map.remove(player);
            }
        }
    }
}
