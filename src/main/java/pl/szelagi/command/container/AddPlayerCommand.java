package pl.szelagi.command.container;

import org.bukkit.command.CommandSender;
import pl.szelagi.command.CommandHelper;
import pl.szelagi.command.SubCommand;

import java.util.List;

import static pl.szelagi.command.CommandHelper.*;

public class AddPlayerCommand implements SubCommand {
    @Override
    public String getName() {
        return "addplayer";
    }

    @Override
    public String getDescription() {
        return "Adds a player to the specified container.";
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
            container.addPlayer(player);
            sender.sendMessage(PREFIX + SUCCESS_COLOR + "Player " + player.getName() + " has been added to the container with ID " + container.identifier() + ".");
        } catch (Exception e) {
            sender.sendMessage(PREFIX + ERROR_COLOR + "An error occurred while adding the player: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return CommandHelper.containerAndPlayerTabComplete(sender, args);
    }
}
