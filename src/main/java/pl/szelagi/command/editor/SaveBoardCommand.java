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
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.szelagi.buildin.creator.Creator;
import pl.szelagi.buildin.creator.CreatorGameMap;
import pl.szelagi.component.GameMap;
import pl.szelagi.manager.ContainerManager;
import pl.szelagi.spatial.ISpatial;
import pl.szelagi.tag.TagAnalyzer;

import static pl.szelagi.command.CommandHelper.PREFIX;

@Deprecated
public class SaveBoardCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player player)) return false;

        var session = ContainerManager.container(player);
        if (session == null) {
            player.sendMessage(PREFIX + "§cYou are not in a session.");
            return false;
        }
        if (!(session instanceof Creator creator)) {
            player.sendMessage(PREFIX + "§cYou are not in the editor.");
            return false;
        }
        if (!(creator.gameMap() instanceof CreatorGameMap creatorBoard)) {
            player.sendMessage(PREFIX + "§cYou are not in an editor board.");
            return false;
        }

        if (creator.isRecording()) {
            player.sendMessage(PREFIX + "§cYou can't start another record while the map is being saved.");
            return false;
        }

        creator.setRecording(true);
        player.sendMessage(PREFIX + "§7§oSaving...");
        long millis = System.currentTimeMillis();

        creatorBoard
                .space()
                .minimalizeAsync(optimized -> {
                    long deltaMinimalizeMillis = System.currentTimeMillis() - millis;
                    player.sendMessage(PREFIX + "§7Board minimalized! §f(" + deltaMinimalizeMillis + "ms)");

                    save(creatorBoard, optimized, player);

                    tag(creatorBoard, optimized, player, () -> {
                        long deltaTotalMillis = System.currentTimeMillis() - millis;
                        player.sendMessage(PREFIX + "§7Board size: §f" + optimized.volume() + "§7, size-x: §f" + optimized.sizeX() + "§7, size-y: §f" + optimized.sizeY() + "§7, size-z: §f" + optimized.sizeZ() + "§7!");
                        player.sendMessage(PREFIX + "§aBoard saved successfully! §f(" + deltaTotalMillis + "ms)");
                        creator.setRecording(false);
                    });
                });

        return true;
    }

    private void save(CreatorGameMap creator, ISpatial optimized, Player player) {
        long millis = System.currentTimeMillis();

        creator.creatorFileManager()
                .saveSchematic(GameMap.CONSTRUCTOR_FILE_NAME, optimized.getMin(), optimized.getMax(), optimized.getCenter());
        creator.creatorFileManager()
                .saveEmptySchematic(GameMap.DESTRUCTOR_FILE_NAME, optimized.getMin(), optimized.getMax(), optimized.getCenter());

        long delta = System.currentTimeMillis() - millis;
        player.sendMessage(PREFIX + "§7Schematics save! §f(" + delta + "ms)");
    }

    private void tag(CreatorGameMap creator, ISpatial optimized, Player player, Runnable next) {
        long millis = System.currentTimeMillis();
        TagAnalyzer.async(optimized, tagResolve -> {
            creator.creatorFileManager()
                    .saveTag(GameMap.TAG_FILE_NAME, tagResolve);
            long delta = System.currentTimeMillis() - millis;
            player.sendMessage(PREFIX + "§7Tag process! §f(" + delta + "ms)");
            next.run();
        });
    }
}
