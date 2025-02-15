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
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.Session;
import pl.szelagi.event.SAPIEventHandler;
import pl.szelagi.event.component.ComponentConstructorEvent;

// Drzewo
// tworzy drzewo zagnieżdzone na 2 poziomy
// sprawdza poprawną kolejność wielo warstwowe
// * czy kontollery w poprawnej kolejności się uruchomiły
// * czy rzucenie eventem z rodzica spowoduje popraną kolejność
// * czy rzuceniem eventem na sessje spowoduje poprawną kolejność
// * czy rzeceniem eventem na dzieci rodzica spowoduje poprawną kolejność

// sprawdza działanie playerConstructor
// sprawdza działanie playerDestructor
// sprawdza poprawną kolejność destruktorów drzewa
// sprawdza czy drzewo się wyłączyło

// sprawdza czy taski zostały zatrzymane
// sprawdza czy taski zostały przerwane

// Listener
// Działanie listenera
// Wiele klas z tym samym listenerem sprawdza czy nie ma duplikacji eventów
// Wyłącza jedną klasę i sprawdza czy listner dzialą
// wyłącza wszystkie klasy i sprawdza czy listener się wyłączył

public class SelfTest extends Session {
    private final Player player;
    public SelfTest(JavaPlugin plugin, Player player) {
        super(plugin);
        this.player = player;
    }

    @Override
    protected @NotNull Board getDefaultStartBoard() {
        return new STBoard(this);
    }

    @SAPIEventHandler
    public void init(ComponentConstructorEvent event) {
        var exceptedConstructorMessage = String.join(" ", TreeRoot.CONS_EXPECTED_RESULT);
        var exceptedDestructorMessage = String.join(" ", TreeRoot.DEST_EXPECTED_RESULT);

        {
            var treeResult1 = new TreeResult();
            var treeRoot1 = new TreeRoot(this, treeResult1);

            getSession().addPlayer(player);
            treeRoot1.start();
            treeRoot1.stop();

            getSession().removePlayer(player);

            var constructorMessage = String.join(" ",  treeResult1.constructorMessage);
            var destructorMessage = String.join(" ", treeResult1.destructorMessage);
            var playerConstructorAfterSession = String.join(" ", treeResult1.playerConstructorMessage);
            var playerDestructorAfterSession = String.join(" ", treeResult1.playerDestructorMessage);

            test(constructorMessage.equals(exceptedConstructorMessage), "Component constructor");

            broadcast("");
            broadcast("§c" + constructorMessage);
            broadcast("§a" + exceptedConstructorMessage);
            broadcast("");


            test(destructorMessage.equals(exceptedDestructorMessage), "Component destructor");

            broadcast("");
            broadcast("§c" + destructorMessage);
            broadcast("§a" + exceptedDestructorMessage);
            broadcast("");

            test(playerConstructorAfterSession.equals(exceptedConstructorMessage), "Player constructor (after session)");
            test(playerDestructorAfterSession.equals(exceptedDestructorMessage), "Player destructor (after session)");


        }

        {
            var treeResult2 = new TreeResult();
            var treeRoot2 = new TreeRoot(this, treeResult2);

            treeRoot2.start();
            getSession().addPlayer(player);
            getSession().removePlayer(player);
            treeRoot2.stop();

            var playerConstructorBeforeSession = String.join(" ", treeResult2.playerConstructorMessage);
            var playerDestructorBeforeSession = String.join(" ", treeResult2.playerDestructorMessage);

            test(playerConstructorBeforeSession.equals(exceptedConstructorMessage), "Player constructor (before session)");

            broadcast("");
            broadcast("§c" + playerConstructorBeforeSession);
            broadcast("§a" + exceptedConstructorMessage);
            broadcast("");

            test(playerDestructorBeforeSession.equals(exceptedDestructorMessage), "Player destructor (before session)");

            broadcast("");
            broadcast("§c" + playerDestructorBeforeSession);
            broadcast("§a" + exceptedDestructorMessage);
            broadcast("");

        }
    }

    private void test(boolean isSuccess, String message) {
        if (isSuccess) {
            pass(message);
        } else {
            fail(message);
        }
    }

    private void pass(String message) {
        broadcast("§2[P] §a" + message);
    }

    private void fail(String message) {
        broadcast("§4[F] §c" + message);
    }

    public void broadcast(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

}
