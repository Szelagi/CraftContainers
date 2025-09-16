/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.sessionStartStopTest;

import pl.szelagi.SessionAPI;
import pl.szelagi.test.SAPITest;
import pl.szelagi.test.TestName;

class SessionStartStopTest {
    @SAPITest(test = TestName.SESSION_START_STOP_TEST)
    public void sessionStartStopTest() {
       var s = new MyContainer(SessionAPI.instance());
       try {
           s.start();
       } finally {
           s.stop();
       }
    }
}
