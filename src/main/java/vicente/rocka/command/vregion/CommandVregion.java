package vicente.rocka.command.vregion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import vicente.rocka.command.vregion.subcommand.VregionCreate;
import vicente.rocka.command.vregion.subcommand.VregionData;
import vicente.rocka.command.vregion.subcommand.VregionDelete;
import vicente.rocka.command.vregion.subcommand.VregionReload;
import vicente.rocka.region.Region;
import vicente.rocka.util.command.MainCommand;
import vicente.rocka.util.command.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandVregion implements TabExecutor, MainCommand {

    private ArrayList<SubCommand> subCommand = new ArrayList<>();

    public CommandVregion(){
        subCommand.add(new VregionCreate());
        subCommand.add(new VregionData());
        subCommand.add(new VregionDelete());
        subCommand.add(new VregionReload());
    }

    private ArrayList<SubCommand> getSubCommand(){
        return subCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        String permission_cmd = Region.plugin.getConfig().getString("commands.vrg.permission");

        if(!sender.hasPermission(permission_cmd)) {
            sender.sendMessage("You dont have permission!");
            if(sender instanceof Player){
                Player player = (Player) sender;
                player.kickPlayer(ChatColor.RED+"You dont have permission!");
            }
            return false;
        }

        if(args.length == 0) {
            this.getUsageCommand(sender, args);
            return true;
        }

        if(args.length > 0) {
            this.usageCommand(sender, args);
            return true;
        }


        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

        ArrayList<String> subcommandsArguments = new ArrayList<>();
        if(!sender.hasPermission("VillaRegion.VRegion")) return subcommandsArguments;

        if(args.length == 1) {this.setSubCommands(subcommandsArguments);}

        if(args.length > 1) {
        return this.getSubCommandsArguments(sender, args);
        }

        return subcommandsArguments;
    }

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
        return new ArrayList<>();
    }

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
        sender.sendMessage(ChatColor.YELLOW+"This SubCommand no exist!");
    }
}
