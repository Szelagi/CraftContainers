# Project Migration to Version 2.3

## Internal Events

::: tip v2.3
```java
@Override
public void onComponentInit(ComponentConstructor event) {
    // when the component is started
}
    
@Override
public void onComponentDestroy(ComponentDestructor event) {
    // when the component is shut down
}

@Override
public void onPlayerInit(PlayerConstructor event) {
    // when a player joins the session or the component is started (for every player already in the session)
}

@Override
public void onPlayerDestroy(PlayerDestructor event) {
    // when a player leaves the session or the component is shut down (for every player in the session)
}

@Override
public void onPlayerJoinRequest(PlayerJoinRequest event) {
    // when a player attempts to join the session (used to block joining, e.g., full lobby)
}

@Override
public void onComponentRecovery(ComponentRecovery event) {
    // used to assign emergency events that execute after a server restart if the server crashed
    // serves to remove persistent resources, e.g., deleting an NPC from another plugin that was created for the session
    // this is persistent, so the recovery system ensures it gets removed even if the server shuts down abruptly
}

@Override
public void onPlayerRecovery(PlayerRecovery event) {
    // used to assign emergency events that execute when a player rejoins the server after a crash
    // serves to restore player state (e.g., removing session items, which is built into the OtherEquipment controller)
    // to maintain state consistency
}
```
:::

::: danger Old
```java
@Override
public void componentConstructor(ComponentConstructorEvent event) {}

@Override
public void componentDestructor(ComponentDestructorEvent event) {}

@Override
public void playerConstructor(PlayerConstructorEvent event) {}

@Override
public void playerDestructor(PlayerDestructorEvent event) {}

@Override
public void playerCanJoin(PlayerCanJoinEvent event) {}

@Override
public void playerCanQuit(PlayerQuitEvent event) {}

@Override
public void playerDestructorRecovery(PlayerRecoveryEvent event) {}
```
:::

## Bukkit Listener and Event Filtering

::: tip v2.3
New methods have been introduced with constant computational complexity when searching for components related to a listener.
```java
// MyComponent.class
@Override
public Listeners defineListeners() {
    return super.defineListeners().add(MyListener.class);
}

private static class MyListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        var player = event.getPlayer();
        var session = SessionManager.session(player);

        // Warning! Never use this for component lookup.md.
        // These methods are solely for filtering events from Bukkit listeners.
        // They allow finding components of a given type that have a listener of a given type.

        // getClass() -- returns MyListener.class (we provide the listener class)
        
        // Method 1
        ListenerManager.each(session, getClass(), MyComponent.class, myComponent -> {
            // events for each component of this type in the session
        });
        
        // Method 2
        ListenerManager.first(session, getClass(), MyComponent.class, myComponent -> {
            // events for the first component of this type in the session
        });
        
        // Method 3
        if (session == null) return;
        var myComponents = ListenerManager.components(session, getClass(), MyComponent.class);
        for (var myComponent : myComponents) {
            // events for each component of this type in the session
        }
        
        // Method 4
        if (session == null) return;
        var myComponent = ListenerManager.component(session, getClass(), MyComponent.class);
        if (myComponent == null) return;
        // events for the first component of this type in the session
    }
}
```
:::

::: danger Old
```java
// MyController.class
@Override
public @Nullable Listener getListener() {
    return new MyListener();
}

private static class MyListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        var player = event.getPlayer();
        var session = SessionManger.getSession(player);
        if (session == null) return;
        var controllers = ControllerManager.getControllers(session, MyController.class);
        for (var controller : controllers) {
            // events for each component of this type in the session
        }
    }
}
```
:::

## Searching for Components in the Session Tree
::: tip v2.3
```java
// MyComponent.class
var components = ComponentManager.components(session, MyComponent.class);
// components that are instances of MyComponent.class and are active in the session tree

var component = ComponentManager.firstComponent(session, MyComponent.class);
// the first component that is an instance of MyComponent.class and is active in the session tree
```
:::

::: danger Old
```java
// MyComponent.class
var controllers = ControllerManager.getControllers(session, MyComponent.class);
// controllers that are instances of MyComponent.class and are active in the session tree

var controller = ControllerManager.getFirstController(session, MyComponent.class);
// the first controller that is an instance of MyComponent.class and is active in the session tree

```
:::

## Tasks and Threads
::: tip v2.3
The **getProcess()** method has been removed. Now, controlled tasks are run directly from the component.
```java
// MyController.class
@Override
public void onComponentInit(ComponentConstructor event) {
    // Starts a task (after zero delay) that repeats every second
    runTaskTimer(() -> {
        players().forEach(player -> player.sendMessage("Hello!"));
    }, Time.zero(), Time.seconds(1));

    // Available task variants:
    //runTask
    //runTaskAsync
    //runTaskLater
    //runTaskLaterAsync
    //runTaskTimer
    //runTaskTimerAsync
}
```
:::

::: danger Old
```java
@Override
public void componentConstructor(ComponentConstructorEvent event) {
    // Starts a task (after zero delay) that repeats every second
    getProcess().runControlledTaskTimer(() -> {
        getSession().getPlayers().forEach(player -> player.sendMessage("Hello!"));
    }, Time.Zero(), Time.Seconds(1));
}
```
:::

## Player State Containers
::: tip v2.3
An example of a built-in component that uses **PlayerContainer** to manage individual player states. This way, it can associate a player with their initial GameMode.

```java
public class GameModeState extends PlayerState {
	private final GameMode gameMode;

	public GameModeState(Player player) {
		super(player);
		gameMode = player.getGameMode();
	}

	public GameMode getGameMode() {
		return gameMode;
	}
}

public class OtherGameMode extends Controller {
    private PlayerContainer<GameModeState> states;
    private final GameMode gameMode;

    public OtherGameMode(BaseComponent baseComponent, GameMode gameMode) {
        super(baseComponent);
        this.gameMode = gameMode;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        states = new PlayerContainer<>(GameModeState::new);
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        states.createOrThrow(event.getPlayer());
        event.getPlayer().setGameMode(gameMode);
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        var player = event.getPlayer();
        var state = states.removeOrThrow(player);
        player.setGameMode(state.getGameMode());
    }

    @Override
    public void onPlayerRecovery(PlayerRecovery event) {
        super.onPlayerRecovery(event);
        var state = states.getOrThrow(event.owner());
        final var gameMode = state.getGameMode();
        event.register(this, player -> {
            player.setGameMode(gameMode);
        });
    }
}
```
:::

::: danger Old
```java
public class GameModeState extends PlayerState {
	private final GameMode gameMode;

	public GameModeState(Player player) {
		super(player);
		gameMode = player.getGameMode();
	}

	public GameMode getGameMode() {
		return gameMode;
	}
}

public class OtherGameMode extends Controller {
    private PlayerContainer<GameModeState> states;
    private final GameMode gameMode;

    public OtherGameMode(ISessionComponent sessionComponent, GameMode gameMode) {
        super(sessionComponent);
        this.gameMode = gameMode;
    }

    @Override
    public void componentConstructor(ComponentConstructorEvent event) {
        super.componentConstructor(event);
        states = new PlayerContainer<>(GameModeState::new);
    }

    @Override
    public void playerConstructor(PlayerConstructorEvent event) {
        super.playerConstructor(event);
        states.get(event.getPlayer());
        event.getPlayer().setGameMode(gameMode);
    }

    @Override
    public void playerDestructor(PlayerDestructorEvent event) {
        super.playerDestructor(event);
        var player = event.getPlayer();
        var state = states.get(player);
        player.setGameMode(state.getGameMode());
        states.clearState(player);
    }

    @Override
    public void playerDestructorRecovery(PlayerRecoveryEvent event) {
        super.playerDestructorRecovery(event);
        var state = states.get(event.getForPlayer());
        final var gameMode = state.getGameMode();
        event.getLambdas().add(player -> {
            player.setGameMode(gameMode);
        });
    }
}
```
:::

## File system

::: tip v2.3
Defines which folder the **fileManager()** method points to. Schematics, tags, and other resources will be loaded from this directory.
```java
// MyBoard.class

@Override
public String defineDirectoryPath() {
    // Method 1 (static name)
    // Points to plugins/SessionAPI/board/myDirectory
    // Every component of this instance will use this directory
    // It’s better to use a dynamic name with name()
    return "board/myDirectory";

    // Method 2 (dynamic name)
    // name() returns: <className><C for controller | B for board | S for session>#<pluginName>
    return "board/" + name();
}
```
:::

::: danger Old
Previously, the file system for the map could be defined by overriding the **getName()** method.
:::

## Loading Schematics

::: tip v2.3
The default **generate()** method loads a map from a schematic in the map files.
```java
@Override
protected void generate() {
// ...
    // Loading a schematic from the "SessionAPI/<defineDirectoryPath()>" directory
    var fileManger = fileManager();
    if (fileManger.existSchematic(CONSTRUCTOR_FILE_NAME)) {
        fileManger.loadSchematic(CONSTRUCTOR_FILE_NAME, space(), center());
    }
    // ...
}
```
:::

::: danger Old
```java
// MyBoard.class
@Override
protected void generate() {
    // ...
    // Loading a schematic from the "SessionAPI/board/<getName()>" directory
    if (boardFileManager.existsSchematic(SCHEMATIC_CONSTRUCTOR_NAME)) {
        boardFileManager.loadSchematic(SCHEMATIC_CONSTRUCTOR_NAME);
    }
    // ...
}
```
:::

## Loading Tags
::: tip v2.3
The default **defineTags()** method loads Board tags.

Example operations for loading tags:
```java
// Used within a component
// Uses the "SessionAPI/<defineDirectoryPath()>" directory
var fileManger = fileManager();
// OR

// Custom folder
// Uses the "SessionAPI/board/MyBoard" directory
var fileManager = new FileManager("board/MyBoard");

TagResolve tagResolve = null;
// Reading tags
if (fileManger.existTag(TAG_FILE_NAME)) {
    tagResolve =  fileManger.loadTag(TAG_FILE_NAME, center());
}

// Saving tags
if (tagResolve != null) {
    fileManager.saveTag(TAG_FILE_NAME, tagResolve);
}
```
::: danger Old
```java
var boardFileManager = new BoardFileManager(getName(), getSpace());

TagResolve tagResolve = null;
if (boardFileManager.existsSignTagData(SIGN_TAG_DATA_NAME)) {
    tagResolve = boardFileManager.loadSignTagData(SIGN_TAG_DATA_NAME);
}

if (tagResolve != null) {
    boardFileManager.saveSignTagData(SIGN_TAG_DATA_NAME, tagResolve);
}
```
:::

## Safe Zone of the Map

::: tip v2.3
Defines the area that is destroyed by **degenerate()**. Only within the safe zone can blocks be placed and block operations performed.
```java
// MyBoard.class
@Override
public ISpatial defineSecureZone() {
    // Method 1 (default) -- based on the destructor's size
    return fileManager().loadSchematicToSpatial(DESTRUCTOR_FILE_NAME, center());

    // Method 2 -- on the entire assigned session map.
    // Ensure that degenerate() covers the entire assigned space.
    return ISpatial.clone(space());
}
```
:::

::: danger Old
```java
// MyBoard.class
@Override
protected void generate() {
    // ... (map generation code omitted)

    // Setting SecureZone
    if (boardFileManager.existsSchematic(SCHEMATIC_DESTRUCTOR_NAME)) {
        secureZone = boardFileManager.toSpatial(SCHEMATIC_DESTRUCTOR_NAME, getBase());
    }

}
```
:::