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
import java.util.List;

/*
The algorithm is used to group players based on the defined number of groups.

Algorithm behavior:
1. We define how many groups we want, and the algorithm automatically determines the number of players in each group.
2. The number of groups may be smaller than the defined one if there are too few players.
3. The last groups may have fewer players if the total number of players is not exactly divisible by the number of groups.
4. It is not possible to create an empty group.
5. The number of groups will not exceed the value defined by the user.
*/

public class GrouperNTeams<T extends Group> extends Grouper<T> {
    public GrouperNTeams(int groups, Collection<Player> players, GroupCreator<T> creator) {
        var playersInGroup = players.size() / groups;
        var rest = players.size() % groups;
        var playerIterator = players.iterator();

        if (playersInGroup == 0) {
            for (int i = 0; i < rest; i++) {
                var group = creator.create(i, List.of(playerIterator.next()));
                add(group);
            }
            return;
        }

        var allGroups = new ArrayList<List<Player>>();
        for (int i = 0; i < groups; i++) {
            if (!playerIterator.hasNext()) break;
            var groupPlayers = new ArrayList<Player>();
            for (int j = 0; j < playersInGroup; j++) {
                if (!playerIterator.hasNext()) break;
                groupPlayers.add(playerIterator.next());
            }
            allGroups.add(groupPlayers);
        }

        for (int i = 0; i < rest; i++) {
            allGroups.get(i).add(playerIterator.next());
        }

        for (int i = 0; i < allGroups.size(); i++) {
            var group = creator.create(i, allGroups.get(i));
            add(group);
        }
    }
}
