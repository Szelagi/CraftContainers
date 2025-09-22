package pl.szelagi.command.container;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.szelagi.command.CommandHelper;
import pl.szelagi.command.SubCommand;
import pl.szelagi.component.container.Container;
import pl.szelagi.manager.ContainerManager;

import java.util.List;

import static pl.szelagi.command.CommandHelper.*;

public class JoinCommand implements SubCommand {
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Joins the specified container.";
    }

    @Override
    public String getUsage() {
        return "<container>";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        var player = CommandHelper.selectSenderPlayer(sender);
        if (player == null) return;

        var container = CommandHelper.extractContainer(sender, args);
        if (container == null) return;

        try {
            container.addPlayer(player);
            sender.sendMessage(PREFIX + SUCCESS_COLOR + "You have successfully joined the container with ID " + container.identifier() + ".");
        } catch (Exception e) {
            sender.sendMessage(PREFIX + ERROR_COLOR + "An error occurred while joining the container: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return ContainerManager.containers().stream().map(Container::identifier).toList();
    }
}
