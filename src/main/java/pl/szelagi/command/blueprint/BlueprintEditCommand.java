/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.blueprint;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.szelagi.SessionAPI;
import pl.szelagi.buildin.editor.BlueprintContainer;
import pl.szelagi.command.SubCommand;
import pl.szelagi.component.base.StartException;
import pl.szelagi.component.container.PlayerJoinException;
import pl.szelagi.fawe.ISchematic;
import pl.szelagi.manager.ContainerManager;
import pl.szelagi.marker.IMarkers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class BlueprintEditCommand implements SubCommand {
    @Override
    public String getName() {
        return "edit";
    }

    @Override
    public String getDescription() {
        return "Opens the editor using an existing schematic or creates a new file.";
    }

    @Override
    public String getUsage() {
        return "<plugin_name>#<project_name>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;

        if (args.length != 1) {
            player.sendMessage(PREFIX + "§cYou must specify a name.");
            return;
        }
        var name = args[0];

        if (!name.equals(name.toLowerCase())) {
            player.sendMessage(PREFIX + "§cThe name must be lowercase only.");
            return;
        }
        if (name.split("#").length != 2) {
            player.sendMessage(PREFIX + "§cThe name must be in the format <plugin_name>#<project_name>.");
            return;
        }

        if (ContainerManager.container(player) != null) {
            player.sendMessage(PREFIX + "§cYou are already in a container.");
            return;
        }

        var schematicFile = ISchematic.getFile(name);
        var markersFile = IMarkers.getFile(name);

        var editor = new BlueprintContainer(SessionAPI.instance(), schematicFile, markersFile);
        try {
            editor.start();
            editor.addPlayer(player);
            player.sendMessage(PREFIX + "§aEditor started successfully.");
        } catch (
                StartException | PlayerJoinException e) {
            player.sendMessage(PREFIX + "§cError starting editor: §f" + e.getMessage());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        var files = ISchematic.getDataFolder().listFiles();
        if (files == null)
            return Collections.emptyList();

        return Stream.of(files).filter(file -> {
            var name = file.getName();
            return name.endsWith(ISchematic.EXTENSION_NAME) || name.endsWith(IMarkers.EXTENSION_NAME);
        }).map(file -> {
            var name = file.getName();
            return name.replace("." + ISchematic.EXTENSION_NAME, "").replace("." + IMarkers.EXTENSION_NAME, "");
        }).collect(Collectors.toCollection(TreeSet::new)).stream().toList();
    }
}
