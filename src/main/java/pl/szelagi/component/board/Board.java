/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.board;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.Scheduler;
import pl.szelagi.buildin.system.BoardWatchDog;
import pl.szelagi.buildin.system.SecureZone;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.StartException;
import pl.szelagi.component.baseComponent.StopException;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerDestructor;
import pl.szelagi.component.board.bukkitEvent.BoardStartEvent;
import pl.szelagi.component.board.bukkitEvent.BoardStopEvent;
import pl.szelagi.component.session.Session;
import pl.szelagi.space.Space;
import pl.szelagi.space.SpaceAllocator;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.tag.TagQuery;
import pl.szelagi.tag.TagResolve;
import pl.szelagi.world.SessionWorldManager;

import java.util.List;

public abstract class Board extends BaseComponent {
    public final static String CONSTRUCTOR_FILE_NAME = "constructor";
    public final static String DESTRUCTOR_FILE_NAME = "destructor";
    public final static String TAG_FILE_NAME = "tag";

    private final Session session;
    private final boolean isUsed;
    private Space space;
    private TagResolve tagResolve;
    private ISpatial secureZone;

    private BukkitTask generateTask;

    public Board(@NotNull Session session) {
        super(session);
        this.session = session;
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

            // Wywołaj event o uruchomieniu mapy
            var event = new BoardStartEvent(this);
            callBukkit(event);
        };

        // Przed uruchomieniem komponentu prosimy o przydzielenie przestrzeni
        space = SpaceAllocator.allocate(SessionWorldManager.getSessionWorld());

        // Ładowanie tagów musi zostać wykonane przed eventem generate, ComponentConstructor oraz PlayerConstructor, ponieważ one mogą korzystać z tagów
        tagResolve = defineTags();

        // Ustawiamy bezpieczną przestrzeń, gdzie można edytować teren.
        // Teren, który obejmuje degenerate()
        try {
            secureZone = defineSecureZone();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to define a secure zone for board: " + name(), e);
        }
        if (secureZone == null) {
            throw new IllegalStateException("Board " + name() + " does not define a secure zone.");
        }

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

    public TagResolve defineTags() {
        var fileManger = fileManager();
        if (fileManger.existTag(TAG_FILE_NAME)) {
            return fileManger.loadTag(TAG_FILE_NAME, center());
        }
        return new TagResolve();
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

        // Wykonujemy event o zakończeniu mapy
        var event = new BoardStopEvent(this);
        callBukkit(event);

        // Czyszczenie mapy

        // Wykonywane na końcu
        Runnable lastAction = () -> {
            // Niszczmy pozostałości mapy
            degenerate();
            // Zwalniamy przydzieloną przestrzeń
            SpaceAllocator.deallocate(space);
        };

        if (isAsync) {
            // Nie możemy używać wewnętrzengo scheduler, ponieważ komponent jest wyłączony
            // Rejestrowanie zdarzeń, kiedy plugin się wyłącza, powoduje błąd Paper/Spigot
            Scheduler.runTaskAsync(lastAction);
        } else {
            lastAction.run();
        }
    }

    protected void generate() {
        Scheduler.runAndWait(() -> {
            center().getBlock()
                    .setType(Material.BEDROCK);
        });

        var fileManger = fileManager();
        if (fileManger.existSchematic(CONSTRUCTOR_FILE_NAME)) {
            fileManger.loadSchematic(CONSTRUCTOR_FILE_NAME, space(), center());
        }

        if (tagResolve != null) {
            Scheduler.runAndWait(() -> {
                for (var l : tagResolve.toLocations())
                    l.getBlock()
                            .setType(Material.AIR);
            });
        }
    }

    protected void degenerate() {
        var fileManger = fileManager();
        if (fileManger.existSchematic(DESTRUCTOR_FILE_NAME)) {
            fileManger.loadSchematic(DESTRUCTOR_FILE_NAME, space(), center());
        }

        Scheduler.runAndWait(() -> {
            center().getBlock()
                    .setType(Material.AIR);

            space().getMobsIn().forEach(Entity::remove);
        });
    }

    public final Location center() {
        var space = space();
        return space.getCenter();
    }

    public final Space space() {
        if (space == null) throw new IllegalStateException("Space not set");
        return space;
    }

    public final @NotNull TagResolve tagResolve() {
        if (tagResolve == null) throw new IllegalStateException("TagResolve not set");
        return tagResolve;
    }

    @Deprecated
    public final @NotNull TagQuery tagQuery(@NotNull String tagName) {
        var tagResolve = tagResolve();
        return tagResolve.query(tagName);
    }

    public final @NotNull TagQuery tags(@NotNull String name) {
        var tagResolve = tagResolve();
        return tagResolve.query(name);
    }

    protected int defaultTime() {
        return 0;
    }

    protected @NotNull WeatherType defaultWeather() {
        return WeatherType.CLEAR;
    }

    protected @NotNull Location spawnLocation() {
        return space().getAbove(space().getCenter());
    }


    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        new BoardWatchDog(this).start();
        new SecureZone(this).start();
        //		// TODO: It may be necessary to ensure that
		// all session maps are checked by these controllers
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);

        var player = event.player();
        player.teleport(spawnLocation());
        player.setPlayerTime(defaultTime(), false);
        player.setPlayerWeather(defaultWeather());
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);

        var player = event.player();
        player.resetPlayerWeather();
        player.resetPlayerTime();
    }

    //

    @Override
    public final @NotNull List<Player> players() {
        return session.players();
    }

    @Override
    public final @NotNull Session session() {
        return session;
    }

    @Override
    public final @Nullable Board board() {
        return session.board();
    }

    @Override
    public String defineDirectoryPath() {
        return "board/" + name();
    }

    public ISpatial defineSecureZone() {
        return fileManager().loadSchematicToSpatial(DESTRUCTOR_FILE_NAME, center());
    }

    public final ISpatial secureZone() {
        return secureZone;
    }
}
