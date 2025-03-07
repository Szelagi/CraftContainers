/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.recovery.internalEvent;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import pl.szelagi.component.baseComponent.BaseComponent;
import pl.szelagi.event.internal.InternalEvent;
import pl.szelagi.recovery.PlayerRecoveryLambda;

import java.util.HashMap;
import java.util.HashSet;

public class PlayerRecovery extends InternalEvent {
    private final @NotNull PlayerRecoveryCause cause;
    private final @NotNull Player owner;
    private final @NotNull HashMap<BaseComponent, HashSet<PlayerRecoveryLambda>> playersDestroyRecoveries;

    public PlayerRecovery(@NotNull Player owner, @NotNull PlayerRecoveryCause cause) {
        this.owner = owner;
        this.cause = cause;
        this.playersDestroyRecoveries = new HashMap<>();
    }

    public @NotNull Player owner() {
        return owner;
    }

    public void register(@NotNull BaseComponent component, @NotNull PlayerRecoveryLambda lambda) {
        var recoveries = playersDestroyRecoveries.computeIfAbsent(component, k -> new HashSet<>());
        recoveries.add(lambda);
    }

    @Unmodifiable
    @NotNull
    public HashMap<BaseComponent, HashSet<PlayerRecoveryLambda>> playersDestroyRecoveries() {
        return playersDestroyRecoveries;
    }

    public @NotNull PlayerRecoveryCause cause() {
        return cause;
    }
}
