/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.marker;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.szelagi.buildin.schematic.Marker;
import pl.szelagi.command.SubCommand;

import java.util.HashMap;
import java.util.List;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class MarkerList extends MarkerSubCommand implements SubCommand {
    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Displays all markers, grouped by name or all markers in group.";
    }

    @Override
    public String getUsage() {
        return "(name)";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        var board = getBlueprintBoard(player);
        if (board == null) return;

        if (args.length == 0) {
            printAllMarkers(board.getMarkers().getMarkers(), player);
            return;
        }

        var name = args[0];
        var markers = board.getMarkers().getByName(name);
        if (markers == null) {
            player.sendMessage(PREFIX + "§cNo markers found.");
            return;
        }

        for (var marker : markers) {
            var message = Component.text(marker.toString())
                    .clickEvent(ClickEvent.runCommand("/marker tp " + marker.getId()))
                    .hoverEvent(HoverEvent.showText(Component.text("§7Click to teleport")));
            player.sendMessage(message);
        }
    }

    public void printAllMarkers(List<Marker> markers, Player player) {
        var markerCounter = new HashMap<String, Integer>();

        if (markers.isEmpty()) {
            player.sendMessage(PREFIX + "§cNo markers found.");
            return;
        }

        for (var marker : markers) {
            var counter = markerCounter.getOrDefault(marker.getName(), 0);
            counter++;
            markerCounter.put(marker.getName(), counter);
        }

        for (var entry : markerCounter.entrySet()) {
            var template = "§fMarker group \"§e%s§f\" (count: %d)";
            var formatted = String.format(template, entry.getKey(), entry.getValue());
            var message = Component.text(formatted)
                    .clickEvent(ClickEvent.runCommand("/marker list " + entry.getKey()))
                    .hoverEvent(HoverEvent.showText(Component.text("§7Click to show more details")));
            player.sendMessage(message);
        }

    }
}
