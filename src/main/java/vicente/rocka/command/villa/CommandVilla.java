package vicente.rocka.command.villa;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import vicente.rocka.command.villa.subcommand.*;
import vicente.rocka.util.command.MainCommand;
import vicente.rocka.util.command.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandVilla implements MainCommand, TabExecutor {

    final private ArrayList<SubCommand> subCommand = new ArrayList<>();

    private ArrayList<SubCommand> getSubCommand(){
        return subCommand;
    }

    public CommandVilla(){
        subCommand.add(new VillaCreate());
        subCommand.add(new VillaDelete());
        subCommand.add(new VillaAdd());
        subCommand.add(new VillaRemove());
        subCommand.add(new VillaGo());
        subCommand.add(new VillaConfig());
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            this.getUsageCommand(sender, args);
            return true;
        }

        if(args.length > 0) {
            this.usageCommand(sender, args);
            return true;
        }


        return false;    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        ArrayList<String> subcommandsArguments = new ArrayList<>();

        if(args.length == 1) {this.setSubCommands(subcommandsArguments);}

        if(args.length > 1) {
            return this.getSubCommandsArguments(sender, args);
        }

        return subcommandsArguments;    }

    @Override
    public void setSubCommands(ArrayList<String> subCommandsArguments) {
        subCommandsArguments.clear();
        for (int i = 0; i < getSubCommand().size(); i++){
            subCommandsArguments.add(this.getSubCommand().get(i).getName());
        }
    }

    @Override
    public List<String> getSubCommandsArguments(CommandSender sender, String[] args) {
        for (int i = 0; i < getSubCommand().size(); i++){
            if (args[0].equalsIgnoreCase(getSubCommand().get(i).getName())){
                return this.getSubCommand().get(i).getSubcommandArguments(sender, args);
            }
        }
        return new ArrayList<>();    }

    @Override
    public void getUsageCommand(CommandSender sender, String[] args) {
        for(int i = 0; i < getSubCommand().size(); i++) {
            if(!(sender instanceof Player)){
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+getSubCommand().get(i).getSyn() + " - " + getSubCommand().get(i).getDes());}
            if(sender instanceof Player) {Player player = (Player) sender; player.sendMessage(ChatColor.YELLOW+getSubCommand().get(i).getSyn() +ChatColor.RED+ " - " + ChatColor.GREEN+ getSubCommand().get(i).getDes());}
        }
    }

    @Override
    public void usageCommand(CommandSender sender, String[] args) {
        for(int i = 0; i < getSubCommand().size(); i++) {
            if(args[0].equalsIgnoreCase(getSubCommand().get(i).getName())) {
                getSubCommand().get(i).perform(sender, args);
                return;
            }
        }
        this.getUsageCommand(sender, args);
    }
}
