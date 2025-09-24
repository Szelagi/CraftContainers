/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.treeProcessingTest;

import org.bukkit.Bukkit;
import pl.szelagi.CraftContainers;
import pl.szelagi.test.SAPITest;
import pl.szelagi.test.TestName;

class TreeTest {
    @SAPITest(test = TestName.CONSTRUCTOR_TREE_TEST)
    public void constructorTreeTest() {
        var s = new S(CraftContainers.instance());
        try {
            s.start();
            var p = Bukkit.getPlayer("TesterBot");
            assert p != null;
            s.addPlayer(p);

            var actual = String.join(" ", s.componentConstructors);
            test("C1 C2 C3 C4 C5 C6 C7 C8 C9 C10 C11", actual);
        } finally {
            s.stop();
        }
    }

    @SAPITest(test = TestName.DESTRUCTOR_TREE_TEST)
    public void destructorTreeTest() {
        var s = new S(CraftContainers.instance());
        try {
            s.start();
            var p = Bukkit.getPlayer("TesterBot");
            assert p != null;
            s.addPlayer(p);
        } finally {
            s.stop();
        }

        var actual = String.join(" ", s.componentDestructors);
        test("C11 C10 C9 C8 C7 C6 C5 C4 C3 C2 C1", actual);
    }

    @SAPITest(test = TestName.PLAYER_CONSTRUCTOR_AFTER_TREE_TEST)
    public void playerConstructorAfterTreeTest() {
        var s = new S(CraftContainers.instance());
        try {
            s.start();
            var p = Bukkit.getPlayer("TesterBot");
            assert p != null;
            s.addPlayer(p);

            var actual = String.join(" ", s.playerConstructors);
            test("C1 C2 C3 C4 C5 C6 C7 C8 C9 C10 C11", actual);
        } finally {
            s.stop();
        }
    }

    @SAPITest(test = TestName.PLAYER_DESTRUCTOR_BEFORE_TREE_TEST)
    public void playerDestructorBeforeTreeTest() {
        var s = new S(CraftContainers.instance());
        try {
            s.start();
            var p = Bukkit.getPlayer("TesterBot");
            assert p != null;
            s.addPlayer(p);

        } finally {
            s.stop();
        }

        var actual = String.join(" ", s.playerDestructors);
        test("C11 C10 C9 C8 C7 C6 C5 C4 C3 C2 C1", actual);
    }

    @SAPITest(test = TestName.PLAYER_DESTRUCTOR_AFTER_TREE_TEST)
    public void playerDestructorAfterTreeTest() {
        var s = new S(CraftContainers.instance());
        try {
            s.start();
            var p = Bukkit.getPlayer("TesterBot");
            assert p != null;
            s.addPlayer(p);
            s.removePlayer(p);

            var actual = String.join(" ", s.playerDestructors);
            test("C11 C10 C9 C8 C7 C6 C5 C4 C3 C2 C1", actual);
        } finally {
            s.stop();
        }
    }

    @SAPITest(test = TestName.PLAYER_JOIN_REQUEST_TREE_TEST)
    public void playerJoinRequestTreeTest() {
        var s = new S(CraftContainers.instance());
        try {
            s.start();
            var p = Bukkit.getPlayer("TesterBot");
            assert p != null;
            s.addPlayer(p);
            var actual = String.join(" ", s.playerJoinRequest);
            test("C1 C2 C3 C4 C5 C6 C7 C8 C9 C10 C11", actual);
        } finally {
            s.stop();
        }
    }

    private void test(String exceptedMessage, String actualMessage) {
        if (!actualMessage.equals(exceptedMessage)) {
            var template = "excepted = '%s', actual = '%s'";
            var message = String.format(template, exceptedMessage, actualMessage);
            throw new IllegalStateException(message);
        }
    }

}
