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

class MarkerGiveMarkerBlock extends MarkerSubCommand implements SubCommand {
    private static final String SUCCESS_TEMPLATE = PREFIX + "You have received a marker block named %s.";

    @Override
    public String getName() {
        return "givemarkerblock";
    }

    @Override
    public String getDescription() {
        return "Gives the player a block that creates a marker at the placement location.";
    }

    @Override
    public String getUsage() {
        return "<name>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) return;
        var board = getBlueprintBoard(player);
        if (board == null) return;

        if (args.length != 1) {
            player.sendMessage(PREFIX + "§cYou must provide a name, which must be a single concatenated word.");
            return;
        }

        var name = args[0];

        var item = new ItemStack(MarkerBlockLogic.MARKER_MATERIAL);
        var meta = item.getItemMeta();

        if (meta != null) {
            var itemName = Component.text("§fMarker: §e" + name);
            meta.displayName(itemName);
            meta.addEnchant(Enchantment.MENDING, 1, true);

            var key = MarkerBlockLogic.CREATE_NBT_KEY;
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, name);
            item.setItemMeta(meta);
        }

        player.getInventory().addItem(item);
        var message = String.format(SUCCESS_TEMPLATE, name);
        player.sendMessage(message);
    }
}
