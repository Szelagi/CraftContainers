/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.editor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.szelagi.SessionAPI;
import pl.szelagi.buildin.creator.Creator;
import pl.szelagi.component.baseComponent.StartException;
import pl.szelagi.component.session.PlayerJoinException;

import java.util.ArrayList;
import java.util.List;

import static pl.szelagi.command.CommandHelper.PREFIX;

public class EditBoardCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return false;

        if (strings.length != 1) {
            player.sendMessage(PREFIX + "§cInvalid usage. §7Pattern: §c/board-edit <board name>");
            return false;
        }

        var directoryName = strings[0];
        var creator = new Creator(SessionAPI.getInstance(), directoryName);
        try {
            creator.start();
            creator.addPlayer(player);
            player.sendMessage(PREFIX + "§aBoard editor started successfully.");
        } catch (
                StartException | PlayerJoinException e) {
            player.sendMessage(PREFIX + "§cError starting editor: §f" + e.getMessage());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        var files = SessionAPI.BOARD_DIRECTORY.listFiles();
        if (files == null || files.length == 0)
            return null;
        var list = new ArrayList<String>();
        for (var file : files) {
            if (!file.isDirectory())
                continue;
            list.add(file.getName());
        }
        return list;
    }
}
