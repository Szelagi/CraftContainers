package pl.szelagi.buildin.system;

import org.bukkit.entity.Player;
import pl.szelagi.component.GameMap;
import pl.szelagi.util.ServerRuntimeException;

public class IllegalPlayerMapExitException extends ServerRuntimeException {
    public IllegalPlayerMapExitException(Player player, GameMap gameMap) {
        super("Player '" + player.getName() + "' left the game map '"
                + gameMap.identifier() + "' in container '"
                + gameMap.container().identifier() + "', "
                + "and could not be teleported to a valid position.");
    }
}