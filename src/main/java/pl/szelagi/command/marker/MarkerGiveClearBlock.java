/*
 * SessionAPI - A framework for game containerization on Minecraft servers
 * Copyright (C) 2025 Szelagi (https://github.com/Szelagi/SessionAPI)
 * Licensed under the GNU General Public License v3.0.
 * For more details, visit <https://www.gnu.org/licenses/>.
 */

package pl.szelagi.command.marker;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import pl.szelagi.buildin.editor.MarkerBlockLogic;
import pl.szelagi.command.SubCommand;

import static pl.szelagi.command.CommandHelper.PREFIX;

class MarkerGiveClearBlock extends MarkerSubCommand implements SubCommand {
    private static final String SUCCESS = PREFIX + "You have received a marker clear block.";

    @Override
    public String getName() {
        return "giveclearblock";
    }

    @Override
    public String getDescription() {
        return "Gives a block that deletes markers at the placement location.";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        var board = getBlueprintBoard(player);
        if (board == null) return;

        var item = new ItemStack(MarkerBlockLogic.MARKER_CLEAR_MATERIAL);
        var meta = item.getItemMeta();

        if (meta != null) {
            var itemName = Component.text("§4Marker clear block");
            meta.displayName(itemName);
            meta.addEnchant(Enchantment.MENDING, 1, true);

            var key = MarkerBlockLogic.DELETE_NBT_KEY;
            meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
            item.setItemMeta(meta);
        }

        player.getInventory().addItem(item);
        player.sendMessage(SUCCESS);
    }
}
