/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package e2e;

import com.github.t9t.minecraftrconclient.RconClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.MountableFile;
import pl.szelagi.command.test.IntegrationTestCommand;
import pl.szelagi.test.Tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class CoreIT {
    private static String generatePassword(int length) {
        var random = new SecureRandom();
        return random.ints(48, 122)
                .filter(i -> (i <= 57 || (i >= 65 && i <= 90) || (i >= 97 && i <= 122)))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private static final String HOST = "localhost";


    private static GenericContainer<?> server;
    private static int port;
    private static String rconPassword;
    private static int rconPort;
    private static RconClient rcon;

    private static Path sapiPath() {
        var plugin = Paths.get("target", "sessionapi-test.jar");
        if (!Files.exists(plugin)) {
            throw new RuntimeException("Could not find sessionapi-test.jar");
        }
        return plugin;
    }

    private static Path fawePath() throws URISyntaxException {
        var plugin = Paths.get(
                Objects.requireNonNull(CoreIT.class.getClassLoader().getResource("fawe.jar")).toURI()
        );
        if (!Files.exists(plugin)) {
            throw new RuntimeException("Could not find fawe.jar");
        }
        return plugin;
    }

    private static Path worldPath() throws URISyntaxException {
        var plugin = Paths.get(
                Objects.requireNonNull(CoreIT.class.getClassLoader().getResource("world.zip")).toURI()
        );
        if (!Files.exists(plugin)) {
            throw new RuntimeException("Could not find world.zip");
        }
        return plugin;
    }


    @BeforeAll
    public static void setup() throws IOException, InterruptedException, URISyntaxException {
        rconPassword = generatePassword(16);

        var sapiPath = sapiPath();
        var fawePath = fawePath();
        var worldPath = worldPath();

        server = new GenericContainer<>("itzg/minecraft-server")
                .withCreateContainerCmdModifier(cmd -> cmd.withName("sessionapi-test"))
                .withEnv("EULA", "TRUE")
                .withEnv("TYPE", "PAPER")
                .withEnv("VERSION", "1.21.1")
                .withEnv("ENABLE_RCON", "true")
                .withEnv("RCON_PASSWORD", rconPassword)
                .withEnv("RCON_PORT", "25575")
                .withEnv("LEVEL_TYPE", "FLAT")
                .withExposedPorts(25565, 25575)
                .withCopyFileToContainer(MountableFile.forHostPath(sapiPath.toString()), "/plugins/sapi.jar")
                .withCopyFileToContainer(MountableFile.forHostPath(fawePath.toString()), "/plugins/fawe.jar")
                .withCopyFileToContainer(MountableFile.forHostPath(worldPath.toString()), "/world.zip")
                .withCommand("unzip -o /world.zip -d /world")
                .waitingFor(org.testcontainers.containers.wait.strategy.Wait.forListeningPort());

        System.out.println("Starting the container...");
        server.start();
        System.out.println("Waiting for the server to fully start...");

        Awaitility.await()
                .atMost(Duration.ofMinutes(1))
                .pollInterval(Duration.ofMillis(500))
                .until(() -> {
                    String logs = server.getLogs();
                    return logs.contains("Done (");
                });


        port = server.getMappedPort(25565);
        rconPort = server.getMappedPort(25575);
        System.out.println("Minecraft server started on port: " + port);
        System.out.println("RCON is running on port: " + rconPort);

        rcon = RconClient.open(HOST, rconPort, rconPassword);
    }

    @AfterAll
    public static void teardown() {
        if (rcon != null) rcon.close();
        if (server != null) server.stop();
    }

    @Test
    public void hasPlugins() {
        var query = rcon.sendCommand("pl");
        if (!query.contains("SessionAPI"))
            throw new IllegalStateException("SessionAPI not present");

        if (!query.contains("FastAsyncWorldEdit"))
            throw new IllegalStateException("FastAsyncWorldEdit not present");
    }

    @Test
    public void sample() {
        internalTest(Tests.SAMPLE_TEST_NAME, null);
    }

    private void internalTest(@NotNull String name, @Nullable List<String> args) throws IntegrationTestFailed {
        var query = "session-integration-test " + name + (args == null || args.isEmpty() ? "" : String.join(" ", args));
        var response = rcon.sendCommand(query);
        if (!response.contains(IntegrationTestCommand.SUCCESS)) {
            throw new IntegrationTestFailed(response);
        } else {
            System.out.println("Successfully executed: " + name);
        }
    }

}
