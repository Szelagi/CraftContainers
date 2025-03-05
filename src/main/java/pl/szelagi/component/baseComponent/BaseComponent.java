/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.baseComponent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentConstructor;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentDestructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerDestroyCause;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerDestructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerInitCause;
import pl.szelagi.component.baseComponent.internalEvent.playerRequest.PlayerJoinRequest;
import pl.szelagi.component.board.Board;
import pl.szelagi.component.session.Session;
import pl.szelagi.event.internal.InternalEvent;
import pl.szelagi.event.sapi.SAPIEvent;
import pl.szelagi.event.sapi.SAPIListener;
import pl.szelagi.file.FileManager;
import pl.szelagi.manager.ComponentManager;
import pl.szelagi.manager.listener.ImmutableListeners;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;
import pl.szelagi.recovery.internalEvent.ComponentRecovery;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;
import pl.szelagi.util.Debug;
import pl.szelagi.util.DepthFirstSearch;
import pl.szelagi.util.IncrementalGenerator;
import pl.szelagi.util.ReverseDepthFirstSearch;
import pl.szelagi.util.timespigot.Time;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

public abstract class BaseComponent implements SAPIListener {
    private static final IncrementalGenerator incrementalGenerator = new IncrementalGenerator();

    private final @NotNull JavaPlugin plugin;
    private final Map<Class<? extends InternalEvent>, Consumer<InternalEvent>> eventHandlers = new HashMap<>();

    private final UUID uuid;
    private final long id;
    private final String name;
    private final String identifier;

    private final FileManager fileManager;
    private final Listeners listeners;

    // testowy system śledzenie, jakie player init/destroy zostały wykonane, aby nie wykonywać wielokrotnie
    private final Set<Player> constructedPlayers = new HashSet<>();

    // potrzebna do rozwiązania problemu "Recursive Flow Inversion"
    private boolean isInvokePlayersConstructor = false;

    private ComponentStatus status = ComponentStatus.NOT_INITIALIZED;

    private TaskSystem taskSystem = null;

    private final @Nullable BaseComponent parent;
    private final List<BaseComponent> children;

    public BaseComponent(@NotNull JavaPlugin plugin) {
        this(plugin, null);
    }

    public BaseComponent(@NotNull BaseComponent parent) {
        this(parent.plugin(), parent);
    }

    public BaseComponent(@NotNull JavaPlugin plugin, @Nullable BaseComponent parent) {
        this.plugin = plugin;

        // Internal events
        eventHandlers.put(ComponentConstructor.class, e -> onComponentInit((ComponentConstructor) e));
        eventHandlers.put(ComponentDestructor.class, e -> onComponentDestroy((ComponentDestructor) e));
        eventHandlers.put(PlayerConstructor.class, e -> watchdogPlayerInit((PlayerConstructor) e));
        eventHandlers.put(PlayerDestructor.class, e -> watchdogPlayerDestroy((PlayerDestructor) e));
        eventHandlers.put(PlayerJoinRequest.class, e -> onPlayerJoinRequest((PlayerJoinRequest) e));
        eventHandlers.put(ComponentRecovery.class, e -> onComponentRecovery((ComponentRecovery) e));
        eventHandlers.put(PlayerRecovery.class, e -> onPlayerRecovery((PlayerRecovery) e));

        listeners = defineListeners();

        this.parent = parent;
        this.children = new LinkedList<>();

        uuid = UUID.randomUUID();
        id = incrementalGenerator.next();
        name = ComponentManager.componentName(this.getClass());
        // identifier wymaga zdefiniowanego: name & id
        identifier = ComponentManager.componentIdentifier(this);

        fileManager = new FileManager(defineDirectoryPath());
    }

    private void watchdogPlayerInit(PlayerConstructor event) {
        var player = event.player();
        // odrzuć, jeżeli ten event został wykonany już dla tego gracza
        if (constructedPlayers.contains(player)) return;

        constructedPlayers.add(player);
        onPlayerInit(event);
    }

    private void watchdogPlayerDestroy(PlayerDestructor event) {
        var player = event.player();
        if (!constructedPlayers.contains(player)) return;

        constructedPlayers.remove(player);
        onPlayerDestroy(event);
    }


    // LIFE CYCLES
    @MustBeInvokedByOverriders
    public void start() throws StartException {
        // Nie można włączyć komponentu, który nie jest w stanie NOT_INITIALIZED lub SHUTDOWN.
        if (status != ComponentStatus.NOT_INITIALIZED && status != ComponentStatus.SHUTDOWN) {
            throw new StartException("Component already started");
        }

        //log
        Debug.send(this, "start");

        // ustaw status na włączony
        status = ComponentStatus.RUNNING;
        // dodaj jako dziecko rodzica
        if (parent != null) {
            parent.children.add(this);
        }

        // tworzy task system który odpowiada za kontrolowane taski
        taskSystem = new TaskSystem(plugin);

        // zarejestruj komponent do ListenerManger
        // uruchamia listener oraz pozwala sprawdzać jakie komponenty go używają
        ListenerManager.controllerStart(this);

        // ustaw flagę, że nie został wykonany na nim PlayerConstructor
        isInvokePlayersConstructor = false;

        // wywołaj event o konstruktorze komponentu
        call(new ComponentConstructor(this));

        // Wywołaj event o konstruktorze gracza dla każdego gracza w sesji.
        // Klonujemy listę, aby zapobiec błędu wynikającego z modyfikacji graczy w trakcie przechodzenia przez listę.
        // InvokeType wynosi SELF, ponieważ event jest wywoływane bez zmiany ilości graczy w sesji.

        // Używamy tej metody, aby znaleźć wszystkich rodziców, którzy nie wywołali PlayerConstructorEvent
        // W ten sposób unikamy problemu "Recursive Flow Inversion", który występuje kiedy ComponentConstructor uruchamia nowe komponenty

        // Wykonujemy tylko na komponentach, które nie utworzyły nowych komponentów
        if (!children.isEmpty()) return;

        var components = findParentWithoutPlayerConstructor();
        for (var component : components) {

            // recovery component
            component.backupComponentOnFailure();

            var playersClone = new ArrayList<>(players());
            for (var player : playersClone) {
                component.isInvokePlayersConstructor = true; // Nakładamy flagę że event został użyty, aby algorytm unikał tego komponentu
                component.call(new PlayerConstructor(player, players(), PlayerInitCause.COMPONENT_INIT, null));

                // recovery player
                component.backupPlayerOnFailure(player);
            }

        }
    }

    @MustBeInvokedByOverriders
    public void stop() throws StopException {
        // wyłącz najpierw dzieci
        var childrenIterator = new ReverseDepthFirstSearch(this, false);
        childrenIterator.forEachRemaining(BaseComponent::stop);

        // nie można wyłączyć komponentu, który nie jest uruchomiony
        if (status != ComponentStatus.RUNNING) {
            throw new StopException("Component is not RUNNING");
        }

        // nie można wyłączyć komponentu, który ma dzieci
        if (!children.isEmpty()) {
            throw new StopException("There are still children");
        }

        //log
        Debug.send(this, "stop");

        // niszczy wszystkie kontrolowane taski
        taskSystem.destroy();
        taskSystem = null;

        // Wywołaj event o destruktorze gracza dla każdego gracza w sesji.
        // Klonujemy listę, aby zapobiec błędu wynikającego z modyfikacji graczy w trakcie przechodzenia przez listę.
        // InvokeType wynosi SELF, ponieważ event jest wywoływane bez zmiany ilości graczy w sesji.
        var playersClone = new ArrayList<>(players());
        for (var player : playersClone) {
            call(new PlayerDestructor(player, players(), PlayerDestroyCause.COMPONENT_DESTROY, null));
        }

        // wywołaj event o destruktorze komponentu
        call(new ComponentDestructor(this));

        // wyrejestruj komponent z recovery
        session().recovery().destroyComponent(this);

        // wyrejestruj komponent z ListenerManger
        ListenerManager.controllerStop(this);

        // usuń jako dziecko rodzica
        if (parent != null) {
            parent.children.remove(this);
        }
        // ustaw status komponentu
        status = ComponentStatus.SHUTDOWN;
    }

    // GETTERS

    public final @Nullable BaseComponent parent() {
        return parent;
    }

    public final List<BaseComponent> children() {
        return children;
    }

    public final @NotNull ComponentStatus status() {
        return status;
    }

    // ABSTRACT

    public abstract @NotNull List<Player> players();

    public final @NotNull JavaPlugin plugin() {
        return plugin;
    }

    public abstract @NotNull Session session();

    public abstract @Nullable Board board();

    // Domyślnym folderem, z którego są ładowane pliki mapy jest folder o nazwie mapy
    public String defineDirectoryPath() {
        return "component/" + name();
    }

    // Główny folder do plików komponentu
    public final @NotNull FileManager fileManager() {
        return fileManager;
    }

    protected final void callBukkit(Event event) {
        plugin().getServer().getScheduler()
                .runTask(plugin(), () -> plugin()
                        .getServer()
                        .getPluginManager()
                        .callEvent(event));
    }

    // SAPI EVENT CALL
    private void call(Iterator<BaseComponent> iterator, SAPIEvent event) {
        iterator.forEachRemaining(component -> {
            var methods = ComponentManager.listeners(component.getClass(), event.getClass());
            methods.forEach(method -> {
                try {
                    method.invoke(component, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Bukkit.getLogger().severe("Error invoking method " + method.getName() +
                            " in class " + component.getClass().getName());
                }
            });
        });
    }


    // SAPI EVENT (reflection)
    // wywołuje event tylko na tym elemencie
    public final void call(SAPIEvent event) {
        call(List.of(this).iterator(), event);
    }

    // wywołuje event na wskazanym liściu oraz na wszystkich dzieciach dzieci od najstarszego do najmłodszego
    public final void callOldToYoung(SAPIEvent event) {
        var iterator = new DepthFirstSearch(this, true);
        call(iterator, event);
    }

    // wywołuje event na wskazanym liściu oraz na wszystkich dzieciach dzieci od młodszego do najstarszego
    public final void callYoungToOld(SAPIEvent event) {
        var iterator = new ReverseDepthFirstSearch(this, true);
        call(iterator, event);
    }

    // INTERNAL EVENT (method)
    private void call(Iterator<BaseComponent> iterator, InternalEvent event) {
        while (iterator.hasNext()) {
            var component = iterator.next();
            if (component.status() != ComponentStatus.RUNNING) continue;
            var handler = component.eventHandlers.get(event.getClass());
            if (handler == null) {
                throw new IllegalStateException(
                        "No handler found in " + component.getClass().getName() +
                                " for event type: " + event.getClass().getName()
                );
            }
            handler.accept(event);
        }
    }

    public final void call(InternalEvent event) {
        call(List.of(this).iterator(), event);
    }

    public final void callOldToYoung(InternalEvent event) {
        var iterator = new DepthFirstSearch(this, true);
        call(iterator, event);
    }

    public final void callYoungToOld(InternalEvent event) {
        var iterator = new ReverseDepthFirstSearch(this, true);
        call(iterator, event);
    }

    // INTERNAL EVENTS (methods)
    @MustBeInvokedByOverriders
    public void onComponentInit(ComponentConstructor event) {
        Debug.send(this, "init");
    }

    @MustBeInvokedByOverriders
    public void onComponentDestroy(ComponentDestructor event) {
        Debug.send(this, "destroy");
    }

    @MustBeInvokedByOverriders
    public void onPlayerInit(PlayerConstructor event) {
        Debug.send(this, "player init: (" + event.player().getName() + ")");
    }

    @MustBeInvokedByOverriders
    public void onPlayerDestroy(PlayerDestructor event) {
        Debug.send(this, "player destroy: (" + event.player().getName() + ")");
    }

    @MustBeInvokedByOverriders
    public void onPlayerJoinRequest(PlayerJoinRequest event) {
        Debug.send(this, "player join request: (" + event.getPlayer().getName() + ")");
    }

    @MustBeInvokedByOverriders
    public void onComponentRecovery(ComponentRecovery event) {
        Debug.send(this, "recovery");
    }

    @MustBeInvokedByOverriders
    public void onPlayerRecovery(PlayerRecovery event) {
        Debug.send(this, "player recovery: (" + event.owner().getName() + ")");
    }

    // TASK SYSTEM
    public final @NotNull SAPITask runTask(@NotNull Runnable runnable) {
        return taskSystem.runTask(runnable);
    }

    public final @NotNull SAPITask runTaskAsync(@NotNull Runnable runnable) {
        return taskSystem.runTaskAsync(runnable);
    }

    public final @NotNull SAPITask runTaskLater(@NotNull Runnable runnable, @NotNull Time laterTime) {
        return taskSystem.runTaskLater(runnable, laterTime);
    }

    public final @NotNull SAPITask runTaskLaterAsync(@NotNull Runnable runnable, @NotNull Time laterTime) {
        return taskSystem.runTaskLaterAsync(runnable, laterTime);
    }

    public final @NotNull SAPITask runTaskTimer(@NotNull Runnable runnable, @NotNull Time laterTime, @NotNull Time repeatTime) {
        return taskSystem.runTaskTimer(runnable, laterTime, repeatTime);
    }

    public final @NotNull SAPITask runTaskTimerAsync(@NotNull Runnable runnable, @NotNull Time laterTime, @NotNull Time repeatTime) {
        return taskSystem.runTaskTimerAsync(runnable, laterTime, repeatTime);
    }

    // EQUALS & HASH CODE & TO STRING
    @Override
    public final boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseComponent that = (BaseComponent) o;
        return id == that.id;
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BaseComponent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    // IDENTIFICATION
    public final UUID uuid() {
        return uuid;
    }

    public final long id() {
        return id;
    }

    public final @NotNull String name() {
        return name;
    }

    public final @NotNull String identifier() {
        return identifier;
    }


    // LISTENERS
    public final ImmutableListeners listeners() {
        return listeners;
    }

    public Listeners defineListeners() {
        return new Listeners();
    }

    private List<BaseComponent> findParentWithoutPlayerConstructor() {
        Deque<BaseComponent> queue = new ArrayDeque<>();

        var processed = this;
        while (!processed.isInvokePlayersConstructor) {
            queue.push(processed);
            var next = processed.parent();
            if (next == null) break;
            processed = next;
        }

        return new ArrayList<>(queue);
    }

    // Recovery
    public final void backupComponentOnFailure() {
        var recovery = session().recovery();
        var event = new ComponentRecovery();
        call(event);
        recovery.updateComponent(event);
    }

    public final void backupPlayersOnFailure() {
        var playersClone = new ArrayList<>(players());
        for (var player : playersClone) {
            backupPlayerOnFailure(player);
        }
    }

    public final void backupPlayerOnFailure(Player player) {
        var recovery = session().recovery();
        var event = new PlayerRecovery(player);
        call(event);
        recovery.updatePlayer(event);
    }

}
