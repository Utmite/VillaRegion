package vicente.rocka.util.command;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public interface MainCommand {

    public abstract void setSubCommands(ArrayList<String> subCommandsArguments);

    public abstract List<String> getSubCommandsArguments(CommandSender sender, String[] args);

    public abstract void getUsageCommand(CommandSender sender, String[] args);

    public abstract void usageCommand(CommandSender sender, String[] args);

}
