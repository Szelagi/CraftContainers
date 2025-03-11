# Kontenery stanu graczy
Wbudowana struktura danych do przechowywania stanu graczy.
Pozwala na przechowywanie własnych informacji o każdym graczu z osobna.

![kontener](../../img/container.gif)

## Tworzenie własnej struktury stanu

```java
public class MyState extends PlayerState {
    private int points;

    public MyState(Player player, int points) {
        super(player);
        this.points = points;
    }

    public int points() {
        return points;
    }

    public void add(int points) {
        this.points += points;
    }
}
```

## Operacje na kontenerze

```java
// Controller.java
private static HashMap<Material, Integer> materialToPoints = new HashMap<>();
static {
    materialToPoints.put(Material.STONE, 0);
    materialToPoints.put(Material.DIAMOND_ORE, 3);
    materialToPoints.put(Material.EMERALD_ORE, 6);
}

private static final String INVALID_BLOCK_MESSAGE = "§cYou cannot destroy this block!";
private static final String LOCKED_BLOCK_MESSAGE = "§cTo unlock the destruction of this block, you need %s more points!";

private final PlayerContainer<MyState> playerContainer = new PlayerContainer<>(player -> new MyState(player, 0));

@Override
public void onPlayerInit(PlayerConstructor event) {
    super.onPlayerInit(event);
    var player = event.player();
    playerContainer.getOrCreate(player);

    var pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
    var inventory = player.getInventory();
    inventory.addItem(pickaxe);
}

@Override
public Listeners defineListeners() {
    return super.defineListeners().add(MyListener.class);
}

public static class MyListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        var player = event.getPlayer();
        var session = SessionManager.session(player);
        ListenerManager.first(session, MyListener.class, TestBoard.class, testBoard -> {
            var material = event.getBlock().getType();
            var cost = materialToPoints.get(material);
            if (cost == null) {
                event.setCancelled(true);
                player.sendMessage(INVALID_BLOCK_MESSAGE);
                return;
            }

            var playerState = testBoard.playerContainer.getOrThrow(player);
            var currentPoints = playerState.points();
            if (currentPoints < cost) {
                event.setCancelled(true);
                var difference = cost - currentPoints;
                var message = String.format(LOCKED_BLOCK_MESSAGE, difference);
                player.sendMessage(message);
                return;
            }

            playerState.add(1);
        });
    }
}
```