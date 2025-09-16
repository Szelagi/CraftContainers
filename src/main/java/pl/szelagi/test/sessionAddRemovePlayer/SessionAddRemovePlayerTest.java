/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.sessionAddRemovePlayer;

import org.bukkit.Bukkit;
import pl.szelagi.SessionAPI;
import pl.szelagi.test.SAPITest;
import pl.szelagi.test.TestName;

class SessionAddRemovePlayerTest {
    @SAPITest(test = TestName.ADD_REMOVE_PLAYER)
    public void sessionAddRemovePlayerTest() {
       var s = new MyContainer(SessionAPI.instance());
       try {
           s.start();
           var p = Bukkit.getPlayer("TesterBot");
           assert p != null;
           s.addPlayer(p);
       } finally {
           s.stop();
       }
    }
}
