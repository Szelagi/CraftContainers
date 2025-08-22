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
import pl.szelagi.buildin.editor.BlueprintSession;
import pl.szelagi.command.SubCommand;
import pl.szelagi.component.baseComponent.StartException;
import pl.szelagi.component.session.PlayerJoinException;
import pl.szelagi.manager.SessionManager;

import java.io.File;

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
        return "<name>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;

        if (args.length != 1) {
            player.sendMessage(PREFIX + "§cYou must specify a name.");
            return;
        }

        if (SessionManager.session(player) != null) {
            player.sendMessage(PREFIX + "§cYou are already in a session.");
        }

        var directoryName = args[0];
        var sapi = SessionAPI.instance();
        var schematicFile = new File(sapi.getDataFolder(), directoryName + ".schem");
        var markersFile = new File(sapi.getDataFolder(), directoryName + ".mrks");

        var editor = new BlueprintSession(SessionAPI.instance(), schematicFile, markersFile);
        try {
            editor.start();
            editor.addPlayer(player);
            player.sendMessage(PREFIX + "§aEditor started successfully.");
        } catch (
                StartException | PlayerJoinException e) {
            player.sendMessage(PREFIX + "§cError starting editor: §f" + e.getMessage());
        }
    }
}
