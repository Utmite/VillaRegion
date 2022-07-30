package vicente.rocka.command.vregion.subcommand;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vicente.rocka.region.Region;
import vicente.rocka.region.Zone;
import vicente.rocka.region.Flag;
import vicente.rocka.region.Furlough;
import vicente.rocka.region.Resident;
import vicente.rocka.util.Util;
import vicente.rocka.util.command.SubCommand;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class VregionCreate implements SubCommand {

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDes() {
        return "The command for create a VRegion!";
    }

    @Override
    public String getSyn() {
        return "/vregion create <r> <name> or /vregion create x1 y1 z1 x2 y2 z2 <world> <name>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        if(args.length == 3 && sender instanceof Player) this.performPlayer3arg((Player) sender, args);
        if(args.length == 8 && sender instanceof Player) this.performPlayer8arg((Player) sender, args);

        if(args.length == 9 && !(sender instanceof Player)) this.performConsole9arg(sender, args);

        if(args.length != 3 && args.length != 8 && sender instanceof Player){
            sender.sendMessage(ChatColor.RED+"/vregion create <radio> <name> or /vregion create x1 y1 z1 x2 y2 z2 <name>");
        }
        if(args.length != 9 && !(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED+"/vregion create x1 y1 z1 x2 y2 z2 <world> <name>");
        }

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        List<String> subArguments = new ArrayList<>();

        if(sender instanceof Player player){
            Location location = player.getLocation();
            switch (args.length) {
                case 2 -> {
                    subArguments.add("<radio>");
                    subArguments.add((int) location.getX() + "");
                }
                case 3 -> {
                    subArguments.add("<name>");
                    subArguments.add((int) location.getY() + "");
                }
                case 4, 7 -> subArguments.add((int) location.getZ() + "");
                case 5 -> subArguments.add((int) location.getX() + "");
                case 6 -> subArguments.add((int) location.getY() + "");

                case 8 -> subArguments.add("<name>");
            }
        }else{
            switch (args.length){
                case 2:
                case 5:
                    subArguments.add("x");
                    break;
                case 3:
                case 6:
                    subArguments.add("y");
                    break;
                case 4:
                case 7:
                    subArguments.add("z");
                    break;
                case 9:
                    subArguments.add("<name>");
                    break;
                case 8:
                    for (World world : Region.plugin.getServer().getWorlds()){
                        subArguments.add(world.getName());
                    }
            }
        }

        return subArguments;
    }

    private void performPlayer3arg(Player player, String[] args){

        if(!Util.isInt(args[1])) {player.sendMessage(ChatColor.YELLOW+"<r> must be a INT"); return;}

        int radio = Integer.parseInt(args[1]);
        String name = args[2];

        if(radio <= 0) {player.sendMessage(ChatColor.RED+"The radio must be positive"); return;}
        if(Zone.isUsedName(name)) {player.sendMessage(ChatColor.RED+"The name is already used"); return;}

        Resident resident = new Resident();
        resident.add(player);

        Zone zone = new Zone(new Rectangle((int) player.getLocation().getX() - (radio / 2),
                (int) player.getLocation().getZ() - (radio / 2),
                radio,
                radio),
                -1000, 1000, player.getLocation().getWorld(),
                name,
                new Flag(),
                new Furlough(),
                resident
        );

        zone.saveZone();

    }
    private void performPlayer8arg(Player player, String[] args){
        if(!Util.isInt(args[1])){player.sendMessage(ChatColor.YELLOW+"<x1> must be a INT"); return;}
        if(!Util.isInt(args[2])){player.sendMessage(ChatColor.YELLOW+"<y1> must be a INT"); return;}
        if(!Util.isInt(args[3])){player.sendMessage(ChatColor.YELLOW+"<z1> must be a INT"); return;}
        if(!Util.isInt(args[4])){player.sendMessage(ChatColor.YELLOW+"<x2> must be a INT"); return;}
        if(!Util.isInt(args[5])){player.sendMessage(ChatColor.YELLOW+"<y2> must be a INT"); return;}
        if(!Util.isInt(args[6])){player.sendMessage(ChatColor.YELLOW+"<z2> must be a INT"); return;}

        String name = args[7];

        if(Zone.isUsedName(name)) {player.sendMessage(ChatColor.RED+"The name is already used"); return;}

        Resident resident = new Resident();
        resident.add(player);

        Zone zone = new Zone(
                this.doRectangle(args),
                Integer.parseInt(args[2]),
                Integer.parseInt(args[5]),
                player.getWorld(),
                name,
                new Flag(),
                new Furlough(),
                resident
        );

        zone.saveZone();
    }

    private void performConsole9arg(CommandSender sender, String[] args){
        if(!Util.isInt(args[1])){sender.sendMessage(ChatColor.YELLOW+"<x1> must be a INT"); return;}
        if(!Util.isInt(args[2])){sender.sendMessage(ChatColor.YELLOW+"<y1> must be a INT"); return;}
        if(!Util.isInt(args[3])){sender.sendMessage(ChatColor.YELLOW+"<z1> must be a INT"); return;}
        if(!Util.isInt(args[4])){sender.sendMessage(ChatColor.YELLOW+"<x2> must be a INT"); return;}
        if(!Util.isInt(args[5])){sender.sendMessage(ChatColor.YELLOW+"<y2> must be a INT"); return;}
        if(!Util.isInt(args[6])){sender.sendMessage(ChatColor.YELLOW+"<z2> must be a INT"); return;}

        String name = args[8];
        World world = Region.plugin.getServer().getWorld(args[7]);

        if(Zone.isUsedName(name)) {sender.sendMessage(ChatColor.RED+"The name is already used"); return;}
        if(world == null) {sender.sendMessage(ChatColor.RED+"The world is not exit"); return;}

        Zone zone = new Zone(
                this.doRectangle(args),
                Integer.parseInt(args[2]),
                Integer.parseInt(args[5]),
                world,
                name,
                new Flag(),
                new Furlough(),
                new Resident()
        );

        zone.saveZone();
    }
    private Rectangle doRectangle(String[] args){
        return new Rectangle(
                Util.getPoints(
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]),
                        Integer.parseInt(args[4]),
                        Integer.parseInt(args[5]),
                        Integer.parseInt(args[6])
                )[0],
                Util.getPoints(
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]),
                        Integer.parseInt(args[4]),
                        Integer.parseInt(args[5]),
                        Integer.parseInt(args[6])
                )[2],

                (int) Util.distaciaEntreDosPuntos(
                        Util.getPoints(
                                Integer.parseInt(args[1]),
                                Integer.parseInt(args[2]),
                                Integer.parseInt(args[3]),
                                Integer.parseInt(args[4]),
                                Integer.parseInt(args[5]),
                                Integer.parseInt(args[6])
                        )[0], 1,
                        Util.getPoints(
                                Integer.parseInt(args[1]),
                                Integer.parseInt(args[2]),
                                Integer.parseInt(args[3]),
                                Integer.parseInt(args[4]),
                                Integer.parseInt(args[5]),
                                Integer.parseInt(args[6])
                        )[3], 1),

                (int) Util.distaciaEntreDosPuntos(
                        1,
                        Util.getPoints(
                                Integer.parseInt(args[1]),
                                Integer.parseInt(args[2]),
                                Integer.parseInt(args[3]),
                                Integer.parseInt(args[4]),
                                Integer.parseInt(args[5]),
                                Integer.parseInt(args[6])
                        )[2],
                        1,
                        Util.getPoints(
                                Integer.parseInt(args[1]),
                                Integer.parseInt(args[2]),
                                Integer.parseInt(args[3]),
                                Integer.parseInt(args[4]),
                                Integer.parseInt(args[5]),
                                Integer.parseInt(args[6])
                        )[5]));
    }
}
