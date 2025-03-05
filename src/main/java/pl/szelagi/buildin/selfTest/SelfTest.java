/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.selfTest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.Session;

public class SelfTest extends Session {
    private final Player player;

    public SelfTest(JavaPlugin plugin, Player player) {
        super(plugin);
        this.player = player;
    }

    @Override
    protected @NotNull Board defaultBoard() {
        return new STBoard(this);
    }

    public void testInternalEvents() {
        var exceptedConstructorRecursiveMessage = String.join(" ", TreeRoot.CONS_EXPECTED_RECURSIVE_RESULT);
        var exceptedConstructorLayerMessage = String.join(" ", TreeRoot.CONS_EXCEPTED_LAYER_RESULT);
        var exceptedDestructorLayerMessage = String.join(" ", TreeRoot.DEST_EXPECTED_LAYER_RESULT);

        var result = new TreeResult();
        var root = new TreeRoot(this, result);

        addPlayer(player);
        root.start();
        root.stop();
        removePlayer(player);

        var constructorMessage = String.join(" ", result.constructorMessage);
        var destructorMessage = String.join(" ", result.destructorMessage);
        var playerConstructorAfterSession = String.join(" ", result.playerConstructorMessage);
        var playerDestructorAfterSession = String.join(" ", result.playerDestructorMessage);

        test(constructorMessage.equals(exceptedConstructorRecursiveMessage), "Component constructor", () -> {
            broadcast("§c" + constructorMessage);
            broadcast("§a" + exceptedConstructorRecursiveMessage);
            broadcast("");
        });

        test(destructorMessage.equals(exceptedDestructorLayerMessage), "Component destructor", () -> {
            broadcast("§c" + destructorMessage);
            broadcast("§a" + exceptedDestructorLayerMessage);
            broadcast("");
        });

        test(playerConstructorAfterSession.equals(exceptedConstructorRecursiveMessage), "Player constructor (after session)", () -> {
            broadcast("§c" + playerConstructorAfterSession);
            broadcast("§a" + exceptedDestructorLayerMessage);
            broadcast("");
        });

        test(playerDestructorAfterSession.equals(exceptedDestructorLayerMessage), "Player destructor (after session)", () -> {
            broadcast("§c" + playerDestructorAfterSession);
            broadcast("§a" + exceptedDestructorLayerMessage);
            broadcast("");
        });

        var result2 = new TreeResult();
        var root2 = new TreeRoot(this, result2);

        root2.start();
        addPlayer(player);
        removePlayer(player);
        root2.stop();

        var playerConstructorBeforeSession = String.join(" ", result2.playerConstructorMessage);
        var playerDestructorBeforeSession = String.join(" ", result2.playerDestructorMessage);

        test(playerConstructorBeforeSession.equals(exceptedConstructorLayerMessage), "Player constructor (before session)", () -> {
            broadcast("§c" + playerConstructorBeforeSession);
            broadcast("§a" + exceptedConstructorLayerMessage);
            broadcast("");
        });

        test(playerDestructorBeforeSession.equals(exceptedDestructorLayerMessage), "Player destructor (before session)", () -> {
            broadcast("§c" + playerDestructorBeforeSession);
            broadcast("§a" + exceptedDestructorLayerMessage);
            broadcast("");
        });

    }

    private void test(boolean isSuccess, String message, @Nullable Runnable failAction) {
        if (isSuccess) {
            pass(message);
        } else {
            fail(message);
            if (failAction != null)
                failAction.run();
        }
    }

    private void pass(String message) {
        broadcast("§2[passed] §a" + message);
    }

    private void fail(String message) {
        broadcast("§4[failed] §c" + message);
    }

    public void broadcast(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

}
