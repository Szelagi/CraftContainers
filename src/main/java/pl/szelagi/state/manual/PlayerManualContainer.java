/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.state.manual;

import org.bukkit.entity.Player;
import pl.szelagi.state.ManualContainer;
import pl.szelagi.state.PlayerState;

public class PlayerManualContainer<T extends PlayerState> extends ManualContainer<Player, T> {
}