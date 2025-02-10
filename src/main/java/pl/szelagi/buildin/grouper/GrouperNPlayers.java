/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.buildin.grouper;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

/*
The algorithm groups players based on the defined number of players per group.

Algorithm behavior:
1. We define how many players should be in each group (base value).
2. The algorithm automatically determines the number of groups to accommodate all players.
3. The last group may contain fewer players than the base value, but it will not be empty.
4. No group can contain more players than the defined number.
*/

public class GrouperNPlayers<T extends Group> extends Grouper<T> {
    public GrouperNPlayers(int playersInGroup, Collection<Player> players, GroupCreator<T> creator) {
        var filledGroups = players.size() / playersInGroup;
        var rest = players.size() % playersInGroup;
        var groups = filledGroups + (rest > 0 ? 1 : 0);
        var playerIterator = players.iterator();

        for (int i = 0; i < groups; i++) {
            var groupPlayers = new ArrayList<Player>();
            for (int j = 0; j < playersInGroup; j++) {
                if (!playerIterator.hasNext()) break;
                groupPlayers.add(playerIterator.next());
            }
            var group = creator.create(i, groupPlayers);
            add(group);
        }
    }
}
