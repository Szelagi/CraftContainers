/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin;

import org.jetbrains.annotations.NotNull;
import pl.szelagi.component.base.Component;
import pl.szelagi.event.internal.player.PlayerDestroyCause;
import pl.szelagi.event.internal.player.PlayerDestructor;
import pl.szelagi.component.Controller;

public class ZeroPlayerClose extends Controller {
    public ZeroPlayerClose(@NotNull Component parent) {
        super(parent);
    }

    @Override
    public void onPlayerDestroy(PlayerDestructor event) {
        super.onPlayerDestroy(event);
        var cause = event.cause();
        if (cause != PlayerDestroyCause.PLAYER_QUIT) return;
        var playerChange = event.playerChange();
        assert playerChange != null;
        if (playerChange.newPlayers().isEmpty()) {
            container().stop();
        }
    }
}
