/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package e2e;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class Watcher implements TestWatcher {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";

    private static boolean anyTestFailed = false;

    public static boolean anyTestFailed() {
        return anyTestFailed;
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        System.out.printf("%s[PASS]%s Test \"%s\" passed.%n", GREEN, RESET, context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        anyTestFailed = true;
    }
}
