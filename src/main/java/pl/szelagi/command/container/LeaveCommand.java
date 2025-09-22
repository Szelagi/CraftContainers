package pl.szelagi.command.container;

import org.bukkit.command.CommandSender;
import pl.szelagi.command.CommandHelper;
import pl.szelagi.command.SubCommand;
import pl.szelagi.component.container.Container;
import pl.szelagi.manager.ContainerManager;

import java.util.List;

import static pl.szelagi.command.CommandHelper.*;

public class LeaveCommand implements SubCommand {
    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leaves the current container.";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        var player = CommandHelper.selectSenderPlayer(sender);
        if (player == null) return;

        var container = CommandHelper.selectPlayerContainer(player);
        if (container == null) return;

        try {
            container.removePlayer(player);
            sender.sendMessage(PREFIX + SUCCESS_COLOR + "You have successfully left the container with ID " + container.identifier() + ".");
        } catch (Exception e) {
            sender.sendMessage(PREFIX + ERROR_COLOR + "An error occurred while leaving the container: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
