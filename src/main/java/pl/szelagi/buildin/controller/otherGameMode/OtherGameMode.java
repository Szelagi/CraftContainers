/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.otherGameMode;

import org.bukkit.GameMode;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.event.internal.player.PlayerDestructor;
import pl.szelagi.component.Controller;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;
import pl.szelagi.state.PlayerStorage;

public class OtherGameMode extends Controller {
    private PlayerStorage<GameModeState> states;
    private final GameMode gameMode;

    public OtherGameMode(Component component, GameMode gameMode) {
        super(component);
        this.gameMode = gameMode;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        states = new PlayerStorage<>(GameModeState::new);
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        states.createOrThrow(event.player());
        event.player().setGameMode(gameMode);
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        var player = event.player();
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
