package pl.szelagi.command.container;

import org.bukkit.command.CommandSender;
import pl.szelagi.command.CommandHelper;
import pl.szelagi.command.SubCommand;

import java.util.List;

import static pl.szelagi.command.CommandHelper.*;

public class RemovePlayerCommand implements SubCommand {
    @Override
    public String getName() {
        return "removeplayer";
    }

    @Override
    public String getDescription() {
        return "Removes a player from the specified container.";
    }

    @Override
    public String getUsage() {
        return "<container> <player>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        var containerAndPlayer = CommandHelper.extractContainerAndPlayer(sender, args);
        if (containerAndPlayer == null) return;

        var container = containerAndPlayer.left;
        var player = containerAndPlayer.right;

        try {
            container.removePlayer(player);
            sender.sendMessage(PREFIX + SUCCESS_COLOR + "Player " + player.getName() + " has been removed from the container with ID " + container.identifier() + ".");
        } catch (Exception e) {
            sender.sendMessage(PREFIX + ERROR_COLOR + "An error occurred while removing the player: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return CommandHelper.containerAndPlayerTabComplete(sender, args);
    }
}
