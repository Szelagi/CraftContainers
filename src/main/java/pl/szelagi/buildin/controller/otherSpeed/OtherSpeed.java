/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.otherSpeed;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.internal.component.ComponentConstructor;
import pl.szelagi.event.internal.component.ComponentDestructor;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.event.internal.player.PlayerDestructor;
import pl.szelagi.component.Controller;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;
import pl.szelagi.state.PlayerContainer;

public class OtherSpeed extends Controller {
    private PlayerContainer<OtherSpeedState> playerContainer;
    private final float walkSpeed;
    private final float flySpeed;

    public OtherSpeed(@NotNull BaseComponent parent, float walkSpeed, float flySpeed) {
        super(parent);
        this.walkSpeed = walkSpeed;
        this.flySpeed = flySpeed;
    }

    @Override
    public void onComponentInit(ComponentConstructor event) {
        super.onComponentInit(event);
        playerContainer = new PlayerContainer<>(OtherSpeedState::new);
    }

    @Override
    public void onComponentDestroy(ComponentDestructor event) {
        super.onComponentDestroy(event);
        playerContainer = null;
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        var player = event.player();
        playerContainer.createOrThrow(player);
        player.setWalkSpeed(walkSpeed);
        player.setFlySpeed(flySpeed);
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        var player = event.player();
        var state = playerContainer.removeOrThrow(player);
        state.load(player);
    }

    @Override
    public void onPlayerRecovery(PlayerRecovery event) {
        super.onPlayerRecovery(event);
        var owner = event.owner();
        var state = playerContainer.getOrThrow(owner);
        event.register(this, state::load);
    }
}
