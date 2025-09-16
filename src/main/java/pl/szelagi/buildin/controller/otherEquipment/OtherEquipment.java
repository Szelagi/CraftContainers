/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.otherEquipment;

import pl.szelagi.annotation.SingletonComponent;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.event.internal.player.PlayerDestructor;
import pl.szelagi.component.Controller;
import pl.szelagi.event.tree.TreeEventHandler;
import pl.szelagi.event.tree.tutorial.TeamEliminatedEvent;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;
import pl.szelagi.state.PlayerStorage;

public class OtherEquipment extends Controller {
    private final boolean isClearEquipment;
    PlayerStorage<PlayerEqState> eqStatePlayerContainer = new PlayerStorage<>(PlayerEqState::new);

    public OtherEquipment(Component component) {
        super(component);
        this.isClearEquipment = true;
    }

    public OtherEquipment(Component component, boolean cloneEquipment) {
        super(component);
        this.isClearEquipment = !cloneEquipment;
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);

        var player = event.player();
        eqStatePlayerContainer.getOrCreate(player).save();
        if (isClearEquipment) {
            player.getInventory().clear();
            player.clearActivePotionEffects();
            player.setHealthScale(20);
            player.setHealth(player.getHealthScale());
            player.setFoodLevel(20);
            player.setSaturation(0.6f);
            player.setTotalExperience(0);
            player.setLevel(0);
            player.setExp(0);
        }
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        var player = event.player();
        eqStatePlayerContainer.getOrThrow(player).load(player);
    }

    @Override
    public void onPlayerRecovery(PlayerRecovery event) {
        super.onPlayerRecovery(event);
        var owner = event.owner();
        var state = eqStatePlayerContainer.getOrThrow(owner);
        event.register(this, state::load);
    }
}
