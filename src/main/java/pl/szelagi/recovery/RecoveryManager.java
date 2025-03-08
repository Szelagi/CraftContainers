/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.recovery;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.szelagi.SessionAPI;
import pl.szelagi.file.FileManager;
import pl.szelagi.util.Debug;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RecoveryManager implements Listener {
    public static final String COMPONENT_RECOVERY_PREFIX = "component";
    public static final String PLAYER_RECOVERY_PREFIX = "player";
    public static final String SEPARATOR = "-";
    public static final String EXTENSION = ".bin";
    private static final String PLAYER_RECOVERY_DEBUG = "Called %s different recoveries for player %s";
    private static final String SESSION_RECOVERY_DEBUG = "Called %s different recoveries for session %s";

    public static void initialize(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new RecoveryManager(), plugin);
        execute();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        execute(player);
    }

    private static void execute() {
        var recoveries = findRecoveries(COMPONENT_RECOVERY_PREFIX);

        var groupedByUuid = recoveries.stream()
                .collect(Collectors.groupingBy(RecoveryFile::uuid));

        var sortedGroupsByUnix = groupedByUuid.values().stream()
                .filter(g -> !g.isEmpty())
                .map(group -> {
                    return group.stream().sorted(Comparator.comparingLong(RecoveryFile::unix).reversed()).toList();
                }).toList();

        for (var group : sortedGroupsByUnix) {
            executeOnGroup(group);
        }
    }

    private static void executeOnGroup(List<RecoveryFile> recoveryFiles) {
        ObjectInputStream ois = null;
        for (var recovery : recoveryFiles) {
            var file = recovery.file();
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                var object = (HashSet<ComponentRecoveryLambda>) ois.readObject();

                object.forEach(ComponentRecoveryLambda::run);

                var message = String.format(SESSION_RECOVERY_DEBUG, object.size(), recovery.uuid());
                Debug.send(message);

                break;

            } catch (IOException | ClassNotFoundException ignore) {
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }

        for (var recovery : recoveryFiles) {
            var file = recovery.file();
            file.delete();
        }
    }


    private static void execute(Player player) {
        var uuid = player.getUniqueId().toString().replace("-", "");
        var recoveries = findRecoveries(PLAYER_RECOVERY_PREFIX + SEPARATOR + uuid);
        if (recoveries.isEmpty()) return;

        var sortedRecoveries = recoveries.stream()
                .sorted(Comparator.comparingLong(RecoveryFile::unix).reversed())
                .toList();

        ObjectInputStream ois = null;
        for (var recovery : sortedRecoveries) {
            var file = recovery.file();
            try {
                ois = new ObjectInputStream(new FileInputStream(file));
                var object = (HashSet<PlayerRecoveryLambda>) ois.readObject();

                object.forEach(c -> c.accept(player));

                var message = String.format(PLAYER_RECOVERY_DEBUG, object.size(), player.getName());
                Debug.send(message);
                break;

            } catch (IOException | ClassNotFoundException ignore) {
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }

        for (var recovery : sortedRecoveries) {
            var file = recovery.file();
            file.delete();
        }
    }


    public static Set<RecoveryFile> findRecoveries(String prefix) {
        var fileManager = new FileManager(SessionAPI.RECOVERY_DIRNAME);
        var hashSet = new HashSet<RecoveryFile>();
        var files = fileManager.directory().listFiles();
        if (files == null) return new HashSet<>();
        for (var file : files) {
            var name = file.getName();
            if (!name.endsWith(EXTENSION)) continue;
            if (!name.startsWith(prefix)) continue;
            var split = name.split(SEPARATOR);
            var uuid = split[1];
            var unix = Long.parseLong(split[2].replace(EXTENSION, ""));
            hashSet.add(new RecoveryFile(unix, uuid, file));
        }
        return hashSet;
    }

}
