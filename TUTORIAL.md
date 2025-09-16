# SessionAPI

<h3>First Project</h3>

We are creating the main container class.

```java
public class MyContainer extends Session {
	public MyContainer(JavaPlugin plugin) {
		super(plugin);
	}

	@NotNull
	@Override
	protected Board getDefaultStartBoard() {
		return new MyBoard(this);
	}

	@Override
	public void componentConstructor(ComponentConstructorEvent event) {
		super.componentConstructor(event);
		// To activate the controller, we need to add it to the container. 
		// To do this, we must provide the parent element of the tree in the constructor and call the start method.
		new MyController(this).start();
	}
}
```

We are creating a class responsible for the map within the container.

```java
public class MyBoard extends Board {
	public MyBoard(Session container) {
		super(container);
	}
}
```

We are creating a controller that will display *Hello, world* to players upon joining the container.

```java
public class MyController extends Controller {
	public MyController(ISessionComponent sessionComponent) {
		super(sessionComponent);
	}

	@Override
	public void playerConstructor(PlayerConstructorEvent event) {
		super.playerConstructor(event);
		var player = event.getPlayer();
		player.sendMessage("Hello world!");
	}
}
```

To run the container, you need to create a new instance and call the start method.

```
var containter = new MyContainer(plugin);
containter.start();
containter.addPlayer(player);
```

![first.png](img%2Ffirst.png)

🎉 **Congratulations!** You've created your first container!

> [!TIP]
> To separate the player's inventory state, you need to start the `OtherEquipment` controller
>
>```java
> new OtherEquipment(this).start();
>```

<h3>Bukkit listeners and controlled tasks</h3>
The example demonstrates how to create bukkit listeners and start controlled tasks.

```java
public class SneakController extends Controller {
	public SneakController(ISessionComponent sessionComponent) {
		super(sessionComponent);
	}

	private static final int DIAMOND_COUNT = 9;
	private int gameToggleSneakCounter = 0;

	// must return an instance of the bukkit listener
	@Override
	public @Nullable Listener getListener() {
		return new SneakListener();
	}

	public void toggleSneak() {
		gameToggleSneakCounter++;
		// broadcast message for container
		getSession().getPlayers().forEach(player -> player.sendMessage("someone changed sneak state"));
		if (gameToggleSneakCounter == 4) {
			startFreeDiamondsTask();
		}
	}

	public void startFreeDiamondsTask() {
		// Running a controlled recurring task that ends with this controller
		getProcess().runControlledTaskTimer(() -> {
			for (var containerPlayer : getSession().getPlayers()) {
				var eq = containerPlayer.getInventory();
				eq.addItem(new ItemStack(Material.DIAMOND, DIAMOND_COUNT));
			}
		}, Time.Zero(), Time.Ticks(20));
	}
}
```

```java
public class SneakListener implements Listener {
	@EventHandler(ignoreCancelled = true)
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
		var player = event.getPlayer();
		var container = SessionManager.getSession(player);
		var sneakControllers = ControllerManager.getControllers(container, SneakController.class);
		for (var controller : sneakControllers)
			controller.toggleSneak();
	}
}
```

![sneak.gif](img%2Fsneak.gif)

<h3>Component Hierarchy</h3>
In this example, we will create nested controllers and pause the "GroupController" while the container is running.
Destructors are called in the reverse order of component constructors.

**Structure:**

- MyContainer _(root)_
    - MyBoard
    - MyGroupController
        - MyHasteController
        - MySpeedController
            - MyCookieController

```java
public class MyGroupController extends Controller {
	// ...
	@Override
	public void componentConstructor(ComponentConstructorEvent event) {
		super.componentConstructor(event);
		new MyHasteController(this).start();
		new MySpeedController(this).start();
	}
}
```

```java
public class MyHasteController extends Controller {
	// ...
	@Override
	public void playerConstructor(PlayerConstructorEvent event) {
		super.playerConstructor(event);
		event.getPlayer()
		     .addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, PotionEffect.INFINITE_DURATION, 9));
	}

	@Override
	public void playerDestructor(PlayerDestructorEvent event) {
		super.playerDestructor(event);
		event.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
	}
}
```

```java
public class MySpeedController extends Controller {
	// ...
	@Override
	public void componentConstructor(ComponentConstructorEvent event) {
		super.componentConstructor(event);
		new MyCookieController(this).start();
	}

	@Override
	public void playerConstructor(PlayerConstructorEvent event) {
		super.playerConstructor(event);
		event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 9));
	}

	@Override
	public void playerDestructor(PlayerDestructorEvent event) {
		super.playerDestructor(event);
		event.getPlayer().removePotionEffect(PotionEffectType.SPEED);
	}
}
```

```java
public class MyCookieController extends Controller {
	// ...
	@Override
	public void componentConstructor(ComponentConstructorEvent event) {
		super.componentConstructor(event);
		getProcess().runControlledTaskTimer(() -> {
			for (var containerPlayer : getSession().getPlayers()) {
				var eq = containerPlayer.getInventory();
				eq.addItem(new ItemStack(Material.COOKIE));
			}
		}, Time.Zero(), Time.Ticks(20));
	}
}
```

```java
public class MyContainer extends Session {
	// ...
	@Override
	public void componentConstructor(ComponentConstructorEvent event) {
		super.componentConstructor(event);
		var groupController = new MyGroupController(this);
		groupController.start();
		// End the 'MyGroupController' after 3 seconds
		getProcess().runControlledTaskLater(groupController::stop, Time.Seconds(3));
	}
}
```

It demonstrates that removing the "groupController" triggers cascading removal of subordinate controllers and threads.
![hierarchy.gif](img%2Fhierarchy.gif)