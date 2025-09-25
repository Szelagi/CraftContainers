/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.test;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Method;
import java.util.List;

public class Tests {
    public static final String TEST_PLAYER_NICK = "TesterBot";
    public static Player getTestPlayer() {
        var player = Bukkit.getPlayer(TEST_PLAYER_NICK);
        if (player == null)
            throw new IllegalStateException("Test player not found.");
        return player;
    }

    private static List<Method> findTests(TestName testName) {
        var reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(Tests.class.getPackage().getName())
                .addScanners(Scanners.MethodsAnnotated));

        var methods = reflections.getMethodsAnnotatedWith(TestE2E.class);
        return methods
                .stream()
                .filter(method ->
                        method.getAnnotation(TestE2E.class).test().equals(testName)
                ).toList();
    }

    public static void perform(TestName testName) throws Exception {
        if (testName == null)
            throw new IllegalStateException("Test name not provided.");

        var methods = findTests(testName);

        if (methods.isEmpty())
            throw new IllegalStateException("No tests found. (" + testName + ")");

        if (methods.size() > 1)
            throw new IllegalStateException("Too many tests found. (" + testName + ")");

        var method = methods.getFirst();

        var constructor = method.getDeclaringClass().getDeclaredConstructor();
        constructor.setAccessible(true);
        var instance = constructor.newInstance();
        method.setAccessible(true);
        method.invoke(instance);
    }
}