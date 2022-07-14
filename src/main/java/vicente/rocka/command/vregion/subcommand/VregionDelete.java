package vicente.rocka.command.vregion.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import vicente.rocka.region.Zone;
import vicente.rocka.util.command.SubCommand;
import vicente.rocka.villaregion.VillaRegion;

import java.util.ArrayList;
import java.util.List;


public class VregionDelete implements SubCommand {
    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDes() {
        return "The command for create a VRegion!";
    }

    @Override
    public String getSyn() {
        return "/vregion delete <name> or /vregion delete all";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(args.length != 2){
            sender.sendMessage(ChatColor.YELLOW+this.getSyn());
            return;
        }

        if(args[1].equals("all")){
            Zone.getZoneList().clear();
            VillaRegion.REGIONS.getJson().clear();
            VillaRegion.REGIONS.saveJson();
            return;
        }

        Zone zone = Zone.getZoneByName(args[1]);

        if(zone == null){
            sender.sendMessage(ChatColor.RED+"This zone name not exits");
            return;
        }

        zone.removeZone();

    }
    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        List<String> subcommandArguments = new ArrayList<>();
        switch (args.length){
            case 2:
                subcommandArguments.add("all");

                Zone.getAllName()
                        .stream()
                        .filter(e -> e.toUpperCase().startsWith(args[1].toUpperCase()))
                        .forEach(e -> subcommandArguments.add(e));

                break;
        }

        return subcommandArguments;
    }

}
