/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.component.base;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.component.GameMap;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.component.ComponentDestructor;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.event.internal.player.PlayerDestroyCause;
import pl.szelagi.event.internal.player.PlayerDestructor;
import pl.szelagi.event.internal.player.PlayerInitCause;
import pl.szelagi.event.internal.playerRequest.PlayerJoinRequest;
import pl.szelagi.component.container.Container;
import pl.szelagi.event.internal.InternalEvent;
import pl.szelagi.event.tree.TreeEvent;
import pl.szelagi.event.tree.TreeListener;
import pl.szelagi.manager.*;
import pl.szelagi.manager.listener.ImmutableListeners;
import pl.szelagi.manager.listener.ListenerManager;
import pl.szelagi.manager.listener.Listeners;
import pl.szelagi.recovery.internalEvent.ComponentRecovery;
import pl.szelagi.recovery.internalEvent.ComponentRecoveryCause;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;
import pl.szelagi.recovery.internalEvent.PlayerRecoveryCause;
import pl.szelagi.tree.IHierarchical;
import pl.szelagi.util.Debug;
import pl.szelagi.tree.DepthFirstSearch;
import pl.szelagi.util.IncrementalGenerator;
import pl.szelagi.tree.ReverseDepthFirstSearch;
import pl.szelagi.util.timespigot.Time;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public abstract class Component implements TreeListener, IHierarchical<Component> {
    // Unique ID generator shared by all components.
    private static final IncrementalGenerator incrementalGenerator = new IncrementalGenerator();

    // Internal event handlers mapping each event type to a Consumer.
    private final Map<Class<? extends InternalEvent>, Consumer<InternalEvent>> eventHandlers = new HashMap<>();

    private final @NotNull JavaPlugin plugin;
    private final UUID uuid;
    private final long id;
    private final String name;
    private final String identifier;

    private Listeners cachedListeners = null;

    /**
     * Tracks players whose initialization or destruction events have been processed.
     *
     * <p>This set is used by the watchdog methods (`watchdogPlayerInit` and
     * `watchdogPlayerDestroy`) to prevent multiple executions of the same event
     * for a given player.</p>
     */
    private final Set<Player> constructedPlayers = new HashSet<>();

    // Flag used to prevent "Recursive Flow Inversion" during player initialization.
    private boolean isInvokePlayersConstructor = false;

    private ComponentStatus status = ComponentStatus.NOT_INITIALIZED;

    private TaskSystem taskSystem = null;

    private final @Nullable Component parent;
    private final List<Component> children;

    public Component(@NotNull JavaPlugin plugin) {
        this(plugin, null);
    }

    public Component(@NotNull Component parent) {
        this(parent.plugin(), parent);
    }

    public Component(@NotNull JavaPlugin plugin, @Nullable Component parent) {
        this.plugin = plugin;
        registerInternalEventHandlers();

        this.parent = parent;
        this.children = new LinkedList<>();

        // Generate unique identity
        uuid = UUID.randomUUID();
        id = incrementalGenerator.next();
        name = ComponentManager.componentName(getClass());
        // requires defined: name & id
        identifier = ComponentManager.componentIdentifier(this);
    }

    /**
     * Registers internal event handlers to avoid the overhead of reflection.
     *
     * <p>This method maps specific internal event classes to their corresponding
     * handler methods using a HashMap. When an event of a given type is triggered,
     * the associated handler is executed.</p>
     */
    private void registerInternalEventHandlers() {
        eventHandlers.put(PlayerConstructor.class, e -> watchdogPlayerInit((PlayerConstructor) e));
        eventHandlers.put(PlayerDestructor.class, e -> watchdogPlayerDestroy((PlayerDestructor) e));

        eventHandlers.put(ComponentConstructor.class, e -> {
            Debug.send(this, "init");
            onComponentInit((ComponentConstructor) e);
        });

        eventHandlers.put(ComponentDestructor.class, e -> {
            Debug.send(this, "destroy");
            onComponentDestroy((ComponentDestructor) e);
        });

        eventHandlers.put(PlayerJoinRequest.class, e -> {
            var event = (PlayerJoinRequest) e;
            Debug.send(this, "player join request: (" + event.getPlayer().getName() + ")");
            onPlayerJoinRequest(event);
        });

        eventHandlers.put(ComponentRecovery.class, e -> {
            Debug.send(this, "recovery");
            onComponentRecovery((ComponentRecovery) e);
        });
        eventHandlers.put(PlayerRecovery.class, e -> {
            var event = (PlayerRecovery) e;
            Debug.send(this, "player recovery: (" + event.owner().getName() + ")");
            onPlayerRecovery(event);
        });

    }

    /**
     * Handles the PlayerConstructor internal event with a safeguard to prevent
     * multiple executions for the same player.
     */
    private void watchdogPlayerInit(PlayerConstructor event) {
        var player = event.player();
        // odrzuć, jeżeli ten event został wykonany już dla tego gracza
        if (constructedPlayers.contains(player)) return;

        constructedPlayers.add(player);

        Debug.send(this, "player init: (" + event.player().getName() + ")");
        onPlayerInit(event);
    }


    /**
     * Handles the PlayerDestructor internal event with a safeguard to ensure
     * the player is only destroyed if previously initialized.
     */
    private void watchdogPlayerDestroy(PlayerDestructor event) {
        var player = event.player();
        if (!constructedPlayers.contains(player)) return;

        constructedPlayers.remove(player);

        Debug.send(this, "player destroy: (" + event.player().getName() + ")");
        onPlayerDestroy(event);
    }

    /**
     * Performs internal startup routines for the component.
     */
    private void internalOnStart() {
        CardinalityManager.baseComponentStart(this);

        // Requires CardinalityManager.baseComponentStart to run first
        SingletonManager.check(this);
        // Requires CardinalityManager.baseComponentStart to run first
        DependencyManager.componentStart(this);

        container().onComponentStart(this);
    }

    /**
     * Performs internal shutdown routines for the component.
     */
    private void internalOnStop() {
        CardinalityManager.baseComponentStop(this);
        DependencyManager.componentStop(this);
        container().onComponentStop(this);
    }

    // LIFE CYCLES
    @MustBeInvokedByOverriders
    public void start() {
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
        ListenerManager.onComponentStart(this);

        // ustaw flagę, że nie został wykonany na nim PlayerConstructor
        isInvokePlayersConstructor = false;

        internalOnStart();

        // wywołaj event o konstruktorze komponentu
        callSelf(new ComponentConstructor(this));

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
            component.backupComponentOnFailure(ComponentRecoveryCause.COMPONENT_INIT);

            var playersClone = new ArrayList<>(players());
            for (var player : playersClone) {
                component.isInvokePlayersConstructor = true; // Nakładamy flagę że event został użyty, aby algorytm unikał tego komponentu
                component.callSelf(new PlayerConstructor(player, players(), PlayerInitCause.COMPONENT_INIT, null));

                // recovery player
                component.backupPlayerOnFailure(player, PlayerRecoveryCause.COMPONENT_INIT);
            }

        }
    }

    @MustBeInvokedByOverriders
    public void stop() {
        // wyłącz najpierw dzieci
        var childrenIterator = new ReverseDepthFirstSearch<>(this, false);
        childrenIterator.forEachRemaining(Component::stop);

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

        internalOnStop();

        // Wywołaj event o destruktorze gracza dla każdego gracza w sesji.
        // Klonujemy listę, aby zapobiec błędu wynikającego z modyfikacji graczy w trakcie przechodzenia przez listę.
        // InvokeType wynosi SELF, ponieważ event jest wywoływane bez zmiany ilości graczy w sesji.
        var playersClone = new ArrayList<>(players());
        for (var player : playersClone) {
            callSelf(new PlayerDestructor(player, players(), PlayerDestroyCause.COMPONENT_DESTROY, null));
        }

        // wywołaj event o destruktorze komponentu
        callSelf(new ComponentDestructor(this));

        // wyrejestruj komponent z recovery
        container().recovery().destroyComponent(this);

        // wyrejestruj komponent z ListenerManger
        ListenerManager.onComponentStop(this);

        // usuń jako dziecko rodzica
        if (parent != null) {
            parent.children.remove(this);
        }
        // ustaw status komponentu
        status = ComponentStatus.SHUTDOWN;
    }

    // GETTERS
    @Override
    public final @Nullable Component parent() {
        return parent;
    }

    @Override
    public final List<Component> children() {
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

    public abstract @NotNull Container container();

    public abstract @Nullable GameMap gameMap();

    protected final void callBukkit(Event event) {
        plugin().getServer().getScheduler()
                .runTask(plugin(), () -> plugin()
                        .getServer()
                        .getPluginManager()
                        .callEvent(event));
    }

    // SAPI EVENT CALL
    private void call(Iterator<Component> iterator, TreeEvent event) {
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

    public final void callSelf(TreeEvent event) {
        call(List.of(this).iterator(), event);
    }

    public final void callSpecialization(TreeEvent event) {
        var iterator = new DepthFirstSearch<>(this, true);
        call(iterator, event);
    }

    public final void callGeneralization(TreeEvent event) {
        var iterator = new ReverseDepthFirstSearch<>(this, true);
        call(iterator, event);
    }

    // INTERNAL EVENT (method)
    private void call(Iterator<Component> iterator, InternalEvent event) {
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


    public final void callSelf(InternalEvent event) {
        call(List.of(this).iterator(), event);
    }

    public final void callSpecialization(InternalEvent event) {
        var iterator = new DepthFirstSearch<>(this, true);
        call(iterator, event);
    }

    public final void callGeneralization(InternalEvent event) {
        var iterator = new ReverseDepthFirstSearch<>(this, true);
        call(iterator, event);
    }

    // INTERNAL EVENTS (methods)
    public void onComponentInit(ComponentConstructor event) {}
    public void onComponentDestroy(ComponentDestructor event) {}
    public void onPlayerInit(PlayerConstructor event) {}
    public void onPlayerDestroy(PlayerDestructor event) {}
    public void onPlayerJoinRequest(PlayerJoinRequest event) {}
    public void onComponentRecovery(ComponentRecovery event) {}
    public void onPlayerRecovery(PlayerRecovery event) {}

    // TASK SYSTEM
    public final @NotNull ComponentTask runTask(@NotNull Runnable runnable) {
        return taskSystem.runTask(runnable);
    }

    public final @NotNull ComponentTask runTaskAsync(@NotNull Runnable runnable) {
        return taskSystem.runTaskAsync(runnable);
    }

    public final @NotNull ComponentTask runTaskLater(@NotNull Runnable runnable, @NotNull Time laterTime) {
        return taskSystem.runTaskLater(runnable, laterTime);
    }

    public final @NotNull ComponentTask runTaskLaterAsync(@NotNull Runnable runnable, @NotNull Time laterTime) {
        return taskSystem.runTaskLaterAsync(runnable, laterTime);
    }

    public final @NotNull ComponentTask runTaskTimer(@NotNull Runnable runnable, @NotNull Time laterTime, @NotNull Time repeatTime) {
        return taskSystem.runTaskTimer(runnable, laterTime, repeatTime);
    }

    public final @NotNull ComponentTask runTaskTimerAsync(@NotNull Runnable runnable, @NotNull Time laterTime, @NotNull Time repeatTime) {
        return taskSystem.runTaskTimerAsync(runnable, laterTime, repeatTime);
    }

    // EQUALS & HASH CODE & TO STRING
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Component that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
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
        if (cachedListeners == null) {
            cachedListeners = defineListeners();
        }
        return cachedListeners;
    }

    public Listeners defineListeners() {
        return new Listeners();
    }

    private List<Component> findParentWithoutPlayerConstructor() {
        Deque<Component> queue = new ArrayDeque<>();

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
        backupComponentOnFailure(ComponentRecoveryCause.FORCE_REFRESH);
    }

    private void backupComponentOnFailure(ComponentRecoveryCause cause) {
        var recovery = container().recovery();
        var event = new ComponentRecovery(cause);
        callSelf(event);
        recovery.updateComponent(event);
    }

    public final void backupPlayersOnFailure() {
        var playersClone = new ArrayList<>(players());
        for (var player : playersClone) {
            backupPlayerOnFailure(player);
        }
    }

    public final void backupPlayerOnFailure(Player player) {
        backupPlayerOnFailure(player, PlayerRecoveryCause.FORCE_REFRESH);
    }

    private void backupPlayerOnFailure(Player player, PlayerRecoveryCause cause) {
        var recovery = container().recovery();
        var event = new PlayerRecovery(player, cause);
        callSelf(event);
        recovery.updatePlayer(event);
    }

    protected boolean hasDependency(Class<? extends Component> dependencyClass) {
        return DependencyManager.getDependencies(getClass()).contains(dependencyClass);
    }

    public <T extends Component> @NotNull T useComponent(Class<T> component) {
        checkDependency(component);
        checkSingletonDependency(component);
        var res = container().getComponent(component);
        return Objects.requireNonNull(res);
    }

    public <T extends Component> @NotNull Set<T> useComponents(Class<T> component) {
        checkDependency(component);
        var res = container().getComponents(component);
        return Objects.requireNonNull(res);
    }

    private void checkDependency(Class<? extends Component> controllerClass) {
        if (hasDependency(controllerClass)) return;
        throw new UseComponentException(
                "Component " + controllerClass.getSimpleName() +
                        " is not a declared dependency of " + this.name()
        );
    }

    private void checkSingletonDependency(Class<? extends Component> controllerClass) {
        if (SingletonManager.isSingleton(controllerClass)) return;
        throw new UseComponentException(
                "Cannot use a method that returns a single instance for dependency " +
                        controllerClass.getSimpleName() +
                        ", because it is not marked with @Singleton (used in " + this.name() + ")"
        );
    }

    // Zawsze przeszukują drzewo
    public @NotNull <T> List<T> findComponentsSpecOrd(@NotNull Class<T> clazz) {
        var iterator = new DepthFirstSearch<>(this, true);
        return findComponents(iterator, clazz);
    }

    public @Nullable <T> T findComponentSpecOrd(@NotNull Class<T> clazz) {
        var iterator = new DepthFirstSearch<>(this, true);
        return findFirstComponent(iterator, clazz);
    }

    public @NotNull <T> List<T> findComponentsGeneOrd(@NotNull Class<T> clazz) {
        var iterator = new DepthFirstSearch<>(this, true);
        return findComponents(iterator, clazz);
    }

    public @Nullable <T> T findComponentGeneOrd(@NotNull Class<T> clazz) {
        var iterator = new DepthFirstSearch<>(this, true);
        return findFirstComponent(iterator, clazz);
    }

    private @NotNull <T, U> List<U> findComponents(Iterator<T> iterator, Class<U> clazz) {
        var stream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false
        );
        return stream.filter(clazz::isInstance).map(clazz::cast).toList();
    }

    private @Nullable <T, U> U findFirstComponent(Iterator<T> iterator, Class<U> clazz) {
        while (iterator.hasNext()) {
            var component = iterator.next();
            if (!clazz.isInstance(component)) continue;
            return clazz.cast(component);
        }
        return null;
    }

    public void broadcast(net.kyori.adventure.text.Component message) {
        players().forEach(player -> player.sendMessage(message));
    }

    public void broadcast(String message) {
        broadcast(net.kyori.adventure.text.Component.text(message));
    }

}
