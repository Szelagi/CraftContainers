/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.controller.otherEquipment;

import org.bukkit.Bukkit;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.component.baseComponent.internalEvent.component.ComponentDestructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerConstructor;
import pl.szelagi.component.baseComponent.internalEvent.player.PlayerDestructor;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;
import pl.szelagi.state.PlayerContainer;

public class OtherEquipment extends Controller {
	private final boolean isClearEquipment;
	PlayerContainer<PlayerEqState> eqStatePlayerContainer = new PlayerContainer<>(PlayerEqState::new);

	public OtherEquipment(BaseComponent baseComponent) {
		super(baseComponent);
		this.isClearEquipment = true;
	}

	public OtherEquipment(BaseComponent baseComponent, boolean cloneEquipment) {
		super(baseComponent);
		this.isClearEquipment = !cloneEquipment;
	}

	@Override
	public void onPlayerInit(PlayerConstructor event) {
		super.onPlayerInit(event);

		var player = event.getPlayer();
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
		var player = event.getPlayer();
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
