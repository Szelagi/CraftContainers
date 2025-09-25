package pl.szelagi.test;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import pl.szelagi.CraftContainers;
import pl.szelagi.allocator.Allocators;
import pl.szelagi.component.Controller;
import pl.szelagi.component.GameMap;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.container.Container;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.test.TestE2E;
import pl.szelagi.test.TestName;
import pl.szelagi.test.Tests;

import java.util.ArrayList;
import java.util.List;

public class PlayerConstructorWrapTest {
    private List<String> expected = List.of("ca-i", "cb-i", "cc-i", "ca-pi", "cb-pi", "cc-pi");
    private List<String> actual = new ArrayList<>();


    @TestE2E(test = TestName.PLAYER_CONSTRUCTOR_WRAP_TEST)
    public void playerConstructorWrapTest() {
        C container = new C(CraftContainers.instance());
        try {
            container.start();
            container.addPlayer(Tests.getTestPlayer());
            container.run();
            Assertions.assertEquals(expected, actual);
        } finally {
            container.stop();
        }
    }

    public class C extends Container {
        public C(JavaPlugin plugin) {
            super(plugin);
        }

        @Override
        protected @NotNull GameMap defaultBoard() {
            return new G(this);
        }

        public void run() {
            new CA(this).start();
        }
    }

    public class G extends GameMap {
        public G(@NotNull Container container) {
            super(container, Allocators.defaultRecyclingAllocator());
        }

        @Override
        protected void generate() {}

        @Override
        protected void degenerate() {}
    }

    public class CA extends Controller {
        public CA(@NotNull Component parent) {
            super(parent);
        }

        @Override
        public void onComponentInit(ComponentConstructor event) {
            actual.add("ca-i");
            new CB(this).start();
        }

        @Override
        public void onPlayerInit(PlayerConstructor event) {
            actual.add("ca-pi");
        }
    }

    public class CB extends Controller {
        public CB(@NotNull Component parent) {
            super(parent);
        }

        @Override
        public void onComponentInit(ComponentConstructor event) {
            actual.add("cb-i");
            new CC(this).start();
        }

        @Override
        public void onPlayerInit(PlayerConstructor event) {
            actual.add("cb-pi");
        }
    }

    public class CC extends Controller {
        public CC(@NotNull Component parent) {
            super(parent);
        }

        @Override
        public void onComponentInit(ComponentConstructor event) {
            actual.add("cc-i");
        }

        @Override
        public void onPlayerInit(PlayerConstructor event) {
            actual.add("cc-pi");
        }
    }
}
