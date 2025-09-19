/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import pl.szelagi.Scheduler;
import pl.szelagi.buildin.system.BoardWatchDog;
import pl.szelagi.component.base.Component;
import pl.szelagi.component.base.StartException;
import pl.szelagi.component.base.StopException;
import pl.szelagi.component.container.Container;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.event.bukkit.BoardStartEvent;
import pl.szelagi.event.bukkit.BoardStopEvent;
import pl.szelagi.allocator.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class GameMap extends Component {
    private static final Set<GameMap> GAME_MAPS = new HashSet<>();

    public static @Unmodifiable Set<GameMap> gameMaps() {
        return Collections.unmodifiableSet(GAME_MAPS);
    }

    public static @Nullable GameMap getForLocation(@Nullable Location location) {
        if (location == null) return null;

        return GAME_MAPS.stream()
                .filter(gameMap -> gameMap.space().isLocationInXZ(location))
                .findFirst()
                .orElse(null);
    }

    public static @Nullable Container getContainerForLocation(@Nullable Location location) {
        var gameMap = getForLocation(location);
        return gameMap != null ? gameMap.container() : null;
    }

    public static @Nullable GameMap getForEntity(@Nullable Entity entity) {
        if (entity == null) return null;
        return getForLocation(entity.getLocation());
    }

    public static @Nullable Container getContainerForEntity(@Nullable Entity entity) {
        var gameMap = getForEntity(entity);
        return gameMap != null ? gameMap.container() : null;
    }

    public static @Nullable GameMap getForBlock(@Nullable Block block) {
        if (block == null) return null;
        return getForLocation(block.getLocation());
    }

    public static @Nullable Container getContainerForBlock(@Nullable Block block) {
        var gameMap = getForBlock(block);
        return gameMap != null ? gameMap.container() : null;
    }

    private final Container container;
    private final ISpaceAllocator allocator;
    private final boolean isUsed;
    private IAllocate space;
    private BukkitTask generateTask;

    @Deprecated
    public GameMap(@NotNull Container container) {
        this(container, Allocators.productionAllocator());
    }

    public GameMap(@NotNull Container container, ISpaceAllocator allocator) {
        super(container);
        this.container = container;
        this.allocator = allocator;
        this.isUsed = false;
    }

    @Override
    public final void start() throws StartException {
        start(true, null);
    }

    public final void start(boolean isAsync, @Nullable Runnable thenGenerate) {
        // Zasada działania: mapa musi być załadowana przed eventem ComponentConstructor oraz PlayerConstructor

        // Sprawdzanie, czy mapa nie została wcześniej użyta
        if (isUsed) {
            throw new StartException("Board is already used");
        }

        // Ten kod jest wykonywany na samym końcu po wykonaniu generowania
        Runnable lastAction = () -> {
            // Then generate zostaje uruchomiony przed uruchomieniem komponentu
            if (thenGenerate != null) {
                thenGenerate.run();
            }

            // Uruchamiamy komponent
            super.start();


            GAME_MAPS.add(this);
            // Wywołaj event o uruchomieniu mapy
            var event = new BoardStartEvent(this);
            callBukkit(event);
        };

        // Przed uruchomieniem komponentu prosimy o przydzielenie przestrzeni
        space = allocator.allocate();

        // Tagi nie są domyślnie wspieranie przez mapę
        // Ładowanie tagów musi zostać wykonane przed eventem generate, ComponentConstructor oraz PlayerConstructor, ponieważ one mogą korzystać z tagów
        // tagResolve = defineTags();

        // Ustawiamy bezpieczną przestrzeń, gdzie można edytować teren.
        // Teren, który obejmuje degenerate()
//        try {
//            secureZone = defineSecureZone();
//        } catch (Exception e) {
//            throw new IllegalStateException("Failed to define a secure zone for board: " + name(), e);
//        }
//        if (secureZone == null) {
//            throw new IllegalStateException("Board " + name() + " does not define a secure zone.");
//        }

        // Generujemy mapę na przestrzeni
        if (isAsync) {
            generateTask = Scheduler.runTaskAsync(() -> {
                generate();
                Scheduler.runAndWait(lastAction);
            });
        } else {
            generate();
            lastAction.run();
        }
    }

    @Override
    public final void stop() throws StopException {
        stop(true);
    }

    public final void stop(boolean isAsync) {
        // Zasada działania: ComponentDestructor oraz PlayerDestructor musi być wykonane przed zniczeniem mapy

        // jeżeli istnieje generowanie mapy zakańczamy je
        if (generateTask != null) {
            generateTask.cancel();
            generateTask = null;
        }

        // Wyłączamy komponent
        super.stop();

        GAME_MAPS.remove(this);
        // Wykonujemy event o zakończeniu mapy
        var event = new BoardStopEvent(this);
        callBukkit(event);

        // Czyszczenie mapy

        // Wykonywane na końcu
        Runnable lastAction = () -> {
            // Niszczmy pozostałości mapy
            degenerate();
            // Zwalniamy przydzieloną przestrzeń
            allocator.deallocate(space);
        };

        if (isAsync) {
            // Nie możemy używać wewnętrzengo scheduler, ponieważ komponent jest wyłączony
            // Rejestrowanie zdarzeń, kiedy plugin się wyłącza, powoduje błąd Paper/Spigot
            Scheduler.runTaskAsync(lastAction);
        } else {
            lastAction.run();
        }
    }

    protected abstract void generate();
    protected abstract void degenerate();

    public final Location center() {
        var space = space();
        return space.getCenter();
    }

    public final IAllocate space() {
        if (space == null) throw new IllegalStateException("Space not set");
        return space;
    }

    protected @NotNull Location spawnLocation() {
        return space().getAbove(space().getCenter());
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new BoardWatchDog(this).start();
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        var player = event.player();
        player.teleport(spawnLocation());
    }

    @Override
    public final @NotNull List<Player> players() {
        return container.players();
    }

    @Override
    public @NotNull Container container() {
        return container;
    }

    @Override
    public @Nullable GameMap gameMap() {
        return container.gameMap();
    }
}
