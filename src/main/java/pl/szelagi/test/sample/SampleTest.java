/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test.sample;

import pl.szelagi.SessionAPI;
import pl.szelagi.test.Test;

public class SampleTest extends Test {

    @Override
    public void execute(String[] args) throws Exception {
        var session = new SampleSession(SessionAPI.instance());
        session.start();
        session.stop();
    }

}
