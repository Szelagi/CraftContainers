/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.recovery;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.SessionAPI;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.container.Container;
import pl.szelagi.file.FileManager;
import pl.szelagi.recovery.internalEvent.ComponentRecovery;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static pl.szelagi.recovery.RecoveryManager.*;

public class Recovery {
    private static final Logger LOGGER = Logger.getLogger(Recovery.class.getName());

    private final Container container;
    private final FileManager fileManager = new FileManager(SessionAPI.RECOVERY_DIRNAME);

    // Przechowuje recepturę awaryjnego zakańczania komponentów,
    private final HashMap<Component, HashSet<ComponentRecoveryLambda>> componentDestroyRecoveries = new HashMap<>();
    // Przechowuje recepturę awaryjnego zakańczania graczy
    private final HashMap<Player, HashMap<Component, HashSet<PlayerRecoveryLambda>>> playerDestroyRecoveries = new HashMap<>();

    public Recovery(@NotNull Container container) {
        this.container = container;
    }

    public void updateComponent(ComponentRecovery componentRecovery) {
        var registry = componentRecovery.componentDestroyRecoveries();
        if (registry.isEmpty()) return;

        componentDestroyRecoveries.putAll(registry);

        trySaveComponentsRecovery();
    }

    public void updatePlayer(PlayerRecovery playerRecovery) {
        var owner = playerRecovery.owner();
        var registry = playerRecovery.playersDestroyRecoveries();
        if (registry.isEmpty()) return;

        var players = playerDestroyRecoveries.computeIfAbsent(owner, k -> new HashMap<>());
        players.putAll(registry);

        trySavePlayerRecovery(owner);
    }

    private void trySaveComponentsRecovery() {
        try {
            saveComponentsRecovery();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save components recovery", e);
        }
    }

    public void trySavePlayerRecovery(Player player) {
        try {
            savePlayerRecovery(player);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save players recovery", e);
        }
    }


    private void saveComponentsRecovery() throws IOException {
        var unix = System.currentTimeMillis();
        var uuid = container.uuid().toString().replace("-", "");
        var name = COMPONENT_RECOVERY_PREFIX + SEPARATOR + uuid + SEPARATOR + unix + EXTENSION;

        var object = new HashSet<ComponentRecoveryLambda>();
        componentDestroyRecoveries.values().forEach(object::addAll);

        var file = new File(fileManager.directory(), name);
        file.createNewFile();
        try (var oos = new ObjectOutputStream(new FileOutputStream(file, false))) {
            oos.writeObject(object);
            oos.flush();
        }


        // usuwa poprzednie recovery
        var allRecoveries = findRecoveries(COMPONENT_RECOVERY_PREFIX);
        for (var recovery : allRecoveries) {
            if (recovery.unix() == unix) continue;
            recovery.file().delete();
        }
    }

    private void savePlayerRecovery(Player player) throws IOException {
        var unix = System.currentTimeMillis();
        var uuid = player.getUniqueId().toString().replace("-", "");
        var name = PLAYER_RECOVERY_PREFIX + SEPARATOR + uuid + SEPARATOR + unix + EXTENSION;

        var lambdas = playerDestroyRecoveries.get(player);
        var object = new HashSet<PlayerRecoveryLambda>();
        lambdas.values().forEach(object::addAll);

        var file = new File(fileManager.directory(), name);
        file.createNewFile();
        try (var oos = new ObjectOutputStream(new FileOutputStream(file, false))) {
            oos.writeObject(object);
            oos.flush();
        }

        var playerRecoveries = findRecoveries(PLAYER_RECOVERY_PREFIX + SEPARATOR + uuid);
        playerRecoveries.stream().filter(r -> r.unix() != unix).forEach(r -> {
            r.file().delete();
        });

    }


    public void destroyComponent(Component component) {
        componentDestroyRecoveries.remove(component);
        if (componentDestroyRecoveries.isEmpty()) {
            var uuid = container.uuid().toString().replace("-", "");
            var prefix = COMPONENT_RECOVERY_PREFIX + SEPARATOR + uuid;
            RecoveryManager.findRecoveries(prefix).forEach(recovery -> {
                recovery.file().delete();
            });
        } else {
            trySaveComponentsRecovery();
        }

        var playersWhoHave = new LinkedList<Player>();
        for (var entry : playerDestroyRecoveries.entrySet()) {
            var player = entry.getKey();
            var value = entry.getValue();
            var state = value.remove(component);
            if (state != null) {
                playersWhoHave.add(player);
            }
        }

        for (var player : playersWhoHave) {
            var values = playerDestroyRecoveries.get(player);
            if (values.isEmpty()) destryPlayer(player);
            trySavePlayerRecovery(player);
        }
    }

    public void destryPlayer(Player player) {
        playerDestroyRecoveries.remove(player);
        var uuid = player.getUniqueId().toString().replace("-", "");
        var prefix = PLAYER_RECOVERY_PREFIX + SEPARATOR + uuid;
        RecoveryManager.findRecoveries(prefix).forEach(recovery -> {
            recovery.file().delete();
        });
    }


}
