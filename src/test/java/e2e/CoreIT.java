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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
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
import java.util.regex.Pattern;


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

    private static Network sharedNetwork;
    private static GenericContainer<?> server;
    private static GenericContainer<?> bot;
    private static int port;
    private static String host;
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
        sharedNetwork = Network.newNetwork();


        rconPassword = generatePassword(16);

        var sapiPath = sapiPath();
        var fawePath = fawePath();
        var worldPath = worldPath();

        server = new GenericContainer<>("itzg/minecraft-server")
                .withCreateContainerCmdModifier(cmd -> cmd.withName("sessionapi-test-server"))
                .withEnv("EULA", "TRUE")
                .withEnv("TYPE", "PAPER")
                .withEnv("VERSION", "1.21.1")
                .withEnv("ENABLE_RCON", "true")
                .withEnv("RCON_PASSWORD", rconPassword)
                .withEnv("RCON_PORT", "25575")
                .withEnv("LEVEL_TYPE", "FLAT")
                .withEnv("ONLINE_MODE", "FALSE")
                .withEnv("DIFFICULTY", "peaceful")
                .withExposedPorts(25565, 25575)
                .withCopyFileToContainer(MountableFile.forHostPath(sapiPath.toString()), "/plugins/sapi.jar")
                .withCopyFileToContainer(MountableFile.forHostPath(fawePath.toString()), "/plugins/fawe.jar")
                .withNetwork(sharedNetwork)
                .withNetworkAliases("mc-server");
//                .withCopyFileToContainer(MountableFile.forHostPath(worldPath.toString()), "/world.zip")
//                .withCommand("unzip -o /world.zip -d /world")
//                .waitingFor(org.testcontainers.containers.wait.strategy.Wait.forListeningPort());

        System.out.println("Starting the container...");
        server.start();
        System.out.println("Waiting for the server to fully start...");

        Awaitility.await()
                .pollInterval(Duration.ofMillis(100))
                .until(() -> {
                    String logs = server.getLogs();
                    return logs.contains("Done (");
                });


        port = server.getMappedPort(25565);
        host = server.getHost();
        rconPort = server.getMappedPort(25575);
        System.out.println("Minecraft server started on port: " + port);
        System.out.println("RCON is running on port: " + rconPort);
        rcon = RconClient.open(HOST, rconPort, rconPassword);


        bot = new GenericContainer<>("szelagi/mc-test-player")
                .withCreateContainerCmdModifier(cmd -> cmd.withName("sessionapi-test-client"))
                .withEnv("SERVER_HOST", "mc-server")
                .withEnv("SERVER_PORT", "25565")
                .withEnv("USERNAME", "TesterBot")
                .withEnv("MC_VERSION", "1.21.1")
                .withNetwork(sharedNetwork);

        bot.start();

        Awaitility.await()
                .pollInterval(Duration.ofMillis(100))
                .until(() -> {
                    String logs = server.getLogs();
                    return logs.contains("joined the game");
                });
    }

    @AfterAll
    public static void teardown() {
        if (rcon != null) rcon.close();
        if (bot != null) bot.stop();
        if (server != null) server.stop();
    }

    @Test
    public void hasPlayer() {
        var res = rcon.sendCommand("list");
        System.out.println(res.toString());
        var pattern = Pattern.compile(" 0 ");
        var matches = pattern.matcher(res).find();
        Assertions.assertFalse(matches);
    }

    @Test
    public void hasPlugins() {
        var res = rcon.sendCommand("pl");
        if (!res.contains("SessionAPI"))
            throw new IllegalStateException("SessionAPI not present");

        if (!res.contains("FastAsyncWorldEdit"))
            throw new IllegalStateException("FastAsyncWorldEdit not present");
    }

    @Test
    public void sample() {
        internalTest(Tests.SAMPLE_TEST_NAME, null);
    }

    private void internalTest(@NotNull String name, @Nullable List<String> args) throws IntegrationTestFailed {
        var query = "session-integration-test " + name + (args == null || args.isEmpty() ? "" : String.join(" ", args));
        var res = rcon.sendCommand(query);
        if (!res.contains(IntegrationTestCommand.SUCCESS)) {
            throw new IntegrationTestFailed(res);
        } else {
            System.out.println("Successfully executed: " + name);
        }
    }

}
