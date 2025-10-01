/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package e2e;

import com.github.t9t.minecraftrconclient.RconClient;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;
import pl.szelagi.command.test.TestCommand;
import pl.szelagi.test.TestName;
import pl.szelagi.test.Tests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Pattern;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@ExtendWith(Watcher.class)
public class CoreE2E {
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
        Properties props = new Properties();
        try (InputStream in = CoreE2E.class.getResourceAsStream("/build.properties")) {
            props.load(in);
            String finalName = props.getProperty("project.finalName");
            var fileName = finalName + ".jar";
            var plugin = Paths.get("target", fileName);
            if (!Files.exists(plugin)) {
                throw new RuntimeException("Could not find sessionapi-test.jar");
            }
            return plugin;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path fawePath() throws URISyntaxException {
        var plugin = Paths.get(
                Objects.requireNonNull(CoreE2E.class.getClassLoader().getResource("fawe.jar")).toURI()
        );
        if (!Files.exists(plugin)) {
            throw new RuntimeException("Could not find fawe.jar");
        }
        return plugin;
    }

    @BeforeAll
    public static void setup() throws IOException, InterruptedException, URISyntaxException {
        sharedNetwork = Network.newNetwork();


        rconPassword = generatePassword(16);

        var sapiPath = sapiPath();
        var fawePath = fawePath();

        server = new GenericContainer<>("itzg/minecraft-server")
                .withCreateContainerCmdModifier(cmd -> cmd.withName("craftcontainers-test-server"))
                .withEnv("EULA", "TRUE")
                .withEnv("TYPE", "PAPER")
                .withEnv("VERSION", "1.21.1")
                .withEnv("ENABLE_RCON", "true")
                .withEnv("RCON_PASSWORD", rconPassword)
                .withEnv("RCON_PORT", "25575")
                .withEnv("LEVEL_TYPE", "FLAT")
                .withEnv("ONLINE_MODE", "FALSE")
                .withEnv("DIFFICULTY", "peaceful")
                .withEnv("INIT_MEMORY", "1G")
                .withEnv("MAX_MEMORY", "2G")
                .withExposedPorts(25565, 25575)
                .withCopyFileToContainer(MountableFile.forHostPath(sapiPath.toString()), "/plugins/sapi.jar")
                .withCopyFileToContainer(MountableFile.forHostPath(fawePath.toString()), "/plugins/fawe.jar")
                .withNetwork(sharedNetwork)
                .withNetworkAliases("mc-server")
                .waitingFor(
                        Wait.forLogMessage(".*Done \\(.*\\)!.*", 1)
                                .withStartupTimeout(Duration.ofMinutes(8))
                );

        System.out.println("Starting the docker container...");
        server.start();
        System.out.println("Waiting for the server to fully start...");

        port = server.getMappedPort(25565);
        host = server.getHost();
        rconPort = server.getMappedPort(25575);
        System.out.println("Minecraft server started on port: " + port);
        System.out.println("RCON is running on port: " + rconPort);
        rcon = RconClient.open(HOST, rconPort, rconPassword);

        bot = new GenericContainer<>("szelagi/mc-test-player")
                .withCreateContainerCmdModifier(cmd -> cmd.withName("craftcontainers-test-client"))
                .withEnv("SERVER_HOST", "mc-server")
                .withEnv("SERVER_PORT", "25565")
                .withEnv("USERNAME", Tests.TEST_PLAYER_NICK)
                .withEnv("MC_VERSION", "1.21.1")
                .withNetwork(sharedNetwork);

        bot.start();

        await()
                .pollInterval(Duration.ofMillis(100))
                .until(() -> {
                    String logs = server.getLogs();
                    return logs.contains("joined the game");
                });
    }

    @AfterAll
    public static void teardown() {
        if (Watcher.anyTestFailed()) {
            assert server != null;
            System.err.println("Server logs:\n" + server.getLogs(OutputFrame.OutputType.STDOUT));
        }

        if (rcon != null) rcon.close();
        if (bot != null) bot.stop();
        if (server != null) server.stop();
    }

    @Test
    public void hasPlayer() {
        var res = rcon.sendCommand("list");
        var pattern = Pattern.compile(" 0 ");
        var matches = pattern.matcher(res).find();
        Assertions.assertFalse(matches);
    }

    @Test
    public void hasPlugins() {
        var res = rcon.sendCommand("pl");
        if (!res.contains("CraftContainers"))
            throw new IllegalStateException("CraftContainers not present");

        if (!res.contains("FastAsyncWorldEdit"))
            throw new IllegalStateException("FastAsyncWorldEdit not present");
    }

    @ParameterizedTest(name = "{0}")
    @EnumSource(TestName.class)
    public void internalTest(@NotNull TestName testName) throws RemoteTestFailed {
        var query = TestCommand.COMMAND_NAME + " " + testName;
        var res = rcon.sendCommand(query);
        if (!res.contains(TestCommand.SUCCESS)) {
            throw new RemoteTestFailed(res);
        }
    }

}
