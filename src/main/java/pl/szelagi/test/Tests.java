/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test;

import pl.szelagi.command.test.IntegrationTestCommand;
import pl.szelagi.test.sample.SampleTest;

public abstract class Tests {
    public static final String SAMPLE_TEST_NAME = "sample-test";

    public static void loadAll() {
        IntegrationTestCommand.register(SAMPLE_TEST_NAME, new SampleTest());
    }
}