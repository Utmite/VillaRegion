package vicente.rocka.util.command;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface SubCommand {
    public abstract String getName();

    public abstract String getDes();

    public abstract String getSyn();

    public abstract void perform(CommandSender sender, String[] args);

    public abstract List<String> getSubcommandArguments(CommandSender sender, String[] args);

    public default boolean hasArgs(String args[],int length) {
        if(args.length == length) return true;
        return false;
    }

}
