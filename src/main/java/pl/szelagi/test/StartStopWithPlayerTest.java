package pl.szelagi.test;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.CraftContainers;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.container.Container;

public class StartStopWithPlayerTest {
    @TestE2E(test = TestName.CONTAINER_START_STOP_WITH_PLAYER_TEST)
    public void startStopWithPlayerTest() {
        var s = new StartStopTest.C(CraftContainers.instance());
        try {
            s.start();
            s.addPlayer(Tests.getTestPlayer());
        } finally {
            s.stop();
        }
    }

    public static class C extends Container {
        public C(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        protected @NotNull GameMap defaultBoard() {
            return new G(this);
        }
    }

    public static class G extends GameMap {
        public G(@NotNull Container container) {
            super(container);
        }

        @Override
        protected void generate() {}

        @Override
        protected void degenerate() {}
    }
}
