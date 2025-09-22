package pl.szelagi.command.container;

import org.bukkit.command.CommandSender;
import pl.szelagi.command.CommandHelper;
import pl.szelagi.command.SubCommand;

import java.util.List;

import static pl.szelagi.command.CommandHelper.*;

public class StopCommand implements SubCommand {
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Ends the specified session.";
    }

    @Override
    public String getUsage() {
        return "<container>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        var container = CommandHelper.extractContainer(sender, args);
        if (container == null) return;

        try {
            container.stop();
            sender.sendMessage(PREFIX + SUCCESS_COLOR + "Container with ID " + container.identifier() + " has been successfully stopped.");
        } catch (Exception e) {
            sender.sendMessage(PREFIX + ERROR_COLOR + "An error occurred while stopping the container: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return CommandHelper.containerAndPlayerTabComplete(sender, args);
    }
}
