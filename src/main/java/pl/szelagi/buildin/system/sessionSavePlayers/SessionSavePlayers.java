/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.system.sessionSavePlayers;

import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.internal.player.PlayerConstructor;
import pl.szelagi.event.internal.player.PlayerDestructor;
import pl.szelagi.component.Controller;
import pl.szelagi.recovery.internalEvent.PlayerRecovery;
import pl.szelagi.state.PlayerContainer;

import java.util.ArrayList;
import java.util.List;

public class SessionSavePlayers extends Controller {
    private static final ArrayList<PotionEffectType> NEGATIVE_POTION_TYPES = new ArrayList<>(List.of(PotionEffectType.POISON, PotionEffectType.WITHER, PotionEffectType.WEAKNESS, PotionEffectType.DARKNESS, PotionEffectType.BLINDNESS, PotionEffectType.SLOWNESS, PotionEffectType.MINING_FATIGUE, PotionEffectType.LEVITATION, PotionEffectType.NAUSEA, PotionEffectType.BAD_OMEN, PotionEffectType.HUNGER, PotionEffectType.GLOWING));
    private final PlayerContainer<SessionSavePlayerState> playerContainer = new PlayerContainer<>(SessionSavePlayerState::new);

    public SessionSavePlayers(@NotNull BaseComponent parent) {
        super(parent);
    }

    @Override
    public void onPlayerInit(PlayerConstructor event) {
        super.onPlayerInit(event);
        var player = event.player();
        playerContainer.createOrThrow(player);

        player.setFallDistance(0);
        player.setFireTicks(0);

        for (var potionEffectType : NEGATIVE_POTION_TYPES)
            player.removePotionEffect(potionEffectType);
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        var player = event.player();
        for (var potionEffectType : NEGATIVE_POTION_TYPES) {
            player.removePotionEffect(potionEffectType);
        }
        var state = playerContainer.removeOrThrow(player);
        state.load(player);

    }

    @Override
    public void onPlayerRecovery(PlayerRecovery event) {
        super.onPlayerRecovery(event);
        var state = playerContainer.getOrThrow(event.owner());
        event.register(this, player -> {
            for (var potionType : NEGATIVE_POTION_TYPES)
                player.removePotionEffect(potionType);

            state.load(player);
        });
    }
}
