/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system.sessionsafecontrolplayers;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.ISessionComponent;
import pl.szelagi.component.controller.Controller;
import pl.szelagi.event.player.initialize.PlayerConstructorEvent;
import pl.szelagi.event.player.initialize.PlayerDestructorEvent;
import pl.szelagi.event.player.recovery.PlayerRecoveryEvent;
import pl.szelagi.state.PlayerContainer;

import java.util.ArrayList;
import java.util.List;

public class SessionSafeControlPlayers extends Controller {
	private static final ArrayList<PotionEffectType> NEGATIVE_POTION_TYPES = new ArrayList<>(List.of(PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.WEAKNESS, PotionEffectType.DARKNESS, PotionEffectType.BLINDNESS, PotionEffectType.SLOWNESS, PotionEffectType.MINING_FATIGUE, PotionEffectType.LEVITATION, PotionEffectType.NAUSEA, PotionEffectType.BAD_OMEN, PotionEffectType.HUNGER, PotionEffectType.GLOWING));
	private final PlayerContainer<SessionSafeControlPlayerState> stateContainer = new PlayerContainer<>(SessionSafeControlPlayerState::new);

	public SessionSafeControlPlayers(ISessionComponent sessionComponent) {
		super(sessionComponent);
	}

	@Override
	public void playerConstructor(PlayerConstructorEvent event) {
		super.playerConstructor(event);
		stateContainer.getOrCreate(event.getPlayer())
		              .save();
	}

	@Override
	public void playerDestructor(PlayerDestructorEvent event) {
		super.playerDestructor(event);
		var player = event.getPlayer();
		var state = stateContainer.getOrCreate(player);
		player.setFallDistance(0);
		player.setFireTicks(0);
		for (var potionType : NEGATIVE_POTION_TYPES)
			player.removePotionEffect(potionType);
		state.load(player);
	}

	@Override
	public void playerDestructorRecovery(PlayerRecoveryEvent event) {
		super.playerDestructorRecovery(event);
		var state = stateContainer.getOrCreate(event.getForPlayer());
		event.getLambdas().add(player -> {
			player.setFallDistance(0);
			player.setFireTicks(0);
			for (var potionType : NEGATIVE_POTION_TYPES)
				player.removePotionEffect(potionType);
			state.load(player);
		});
	}

	@Override
	public @NotNull String getName() {
		return "SystemSafeControlController";
	}
}
