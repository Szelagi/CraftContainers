/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.util;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.szelagi.util.timespigot.Time;
import pl.szelagi.util.timespigot.TimeUnit;

import java.util.HashMap;

public class CooldownVolatile {
    private static final int OPTIMIZE_TIMER_TICKS = 60 * 20 * 2;
    private static JavaPlugin plugin;
    private static final HashMap<Player, HashMap<String, Long>> map = new HashMap<>();

    public static void initialize(JavaPlugin p) {
        plugin = p;
        plugin.getServer().getScheduler()
                .runTaskTimer(plugin, CooldownVolatile::optimize, OPTIMIZE_TIMER_TICKS, OPTIMIZE_TIMER_TICKS);
    }

    public static void startCooldown(Player player, String name, Time span) {
        HashMap<String, Long> playerCooldownMap;
        playerCooldownMap = map.computeIfAbsent(player, k -> new HashMap<>());
        long millisEnd = System.currentTimeMillis() + span.toMillis();
        playerCooldownMap.put(name, millisEnd);
    }

    public static boolean canUse(Player player, String name) {
        var playerCooldownMap = map.get(player);
        if (playerCooldownMap == null)
            return true;
        var cooldown = playerCooldownMap.get(name);
        if (cooldown == null)
            return true;
        return System.currentTimeMillis() >= cooldown;
    }

    public static void deleteCooldown(Player player, String name) {
        var playerCooldownMap = map.get(player);
        if (playerCooldownMap == null)
            return;
        playerCooldownMap.remove(name);
    }

    public static boolean canUseAndStart(Player player, String name, Time span) {
        if (!canUse(player, name))
            return false;
        startCooldown(player, name, span);
        return true;
    }

    public static Time getTimeSpanToUse(Player player, String name) {
        var dummyTimeSpan = new Time(0, TimeUnit.SECONDS);
        var playerCooldownMap = map.get(player);
        if (playerCooldownMap == null)
            return dummyTimeSpan;
        var millisEnd = playerCooldownMap.get(name);
        if (millisEnd == null)
            return dummyTimeSpan;
        var deltaMillis = millisEnd - System.currentTimeMillis();
        if (deltaMillis <= 0)
            return dummyTimeSpan;
        return new Time(deltaMillis, TimeUnit.MILLIS);
    }

    private static void optimize() {
        Player player;
        HashMap<String, Long> playerCooldownMap;
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
