package vicente.rocka.command.vregion.subcommand;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;
import vicente.rocka.region.Region;
import vicente.rocka.region.Zone;
import vicente.rocka.util.Util;
import vicente.rocka.util.command.SubCommand;
import vicente.rocka.util.enums.JSONProperties;
import vicente.rocka.util.enums.RegionFlag;
import vicente.rocka.util.enums.RegionFurlough;
import vicente.rocka.util.enums.RegionProperties;

import java.util.*;

public class VregionData implements SubCommand {
    @Override
    public String getName() {
        return "data";
    }

    @Override
    public String getDes() {
        return "The command for get or modify data of a region";
    }

    @Override
    public String getSyn() {
        return "/vregion data get: Get Firts zone in the player, or " +
                "/vregion data get all x y z world : Return all Zone in cords, or" +
                "/vregion data get <name> <properties> : Return all data(JSON) of particular properties" +
                "/vregion data modify <name> flag <flag> <value>" +
                "/vregion data modify <name> furlough <furlough> <player> <value>" +
                "/vregion data modify <name> resident <add / remove> <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {

        if(args.length == 2 && !(sender instanceof Player)){
            sender.sendMessage(ChatColor.YELLOW+this.getSyn());
            return;
        }

        if (isPlayerAndGet(sender, args) && args.length == 2 && args[1].equalsIgnoreCase("get")) {
            this.performSubcommandGetFirstZonePlayer((Player) sender, args);
            return;
        }
        if (isPlayerAndGet(sender, args) && args.length >= 3 && args[2].equalsIgnoreCase("all") && args[1].equalsIgnoreCase("get")) {
            this.performSubcommandGetAllZonePlayer((Player) sender, args);
            return;
        }
        if (isPlayerAndGet(sender, args) && args.length >= 3 && args[1].equalsIgnoreCase("get")) {
            this.performSubcommandGetNameZonePlayer((Player) sender, args);
            return;
        }

        if(args.length >= 3 && args[2].equalsIgnoreCase("all") && args[1].equalsIgnoreCase("get")){
            this.performSubcommandGetZoneCmd(sender, args);
            return;
        }

        if(args.length >= 3 && args[1].equalsIgnoreCase("get")){
            this.performSubcommandGetNameZoneCmd(sender, args);
            return;
        }

        if(args.length <= 4){
            sender.sendMessage(ChatColor.YELLOW+this.getSyn());
            return;
        }

        if(args.length >= 3 && args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("flag")){
            this.performSubcommnadModifyZoneFlag(sender, args);
            return;
        }

        if(args.length >= 3 && args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("furlough")){
            this.performSubcommandModifyZoneFurlough(sender, args);
            return;
        }

        if(args.length >= 3 && args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("resident")){
            this.performSubcommandModifyZoneResident(sender, args);
            return;
        }

        if(args.length >= 3 && args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("properties")){
            this.performSubcommandModifyZoneProperties(sender, args);
            return;
        }

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {

        List<String> subcommandArguments = new ArrayList<>();
        try{
        switch (args.length) {
            case 2:
                subcommandArguments.clear();
                subcommandArguments.add("get");
                subcommandArguments.add("modify");
                break;
            case 3:
                subcommandArguments.clear();
                if (args[1].equalsIgnoreCase("get")) subcommandArguments.add("all");
                subcommandArguments.addAll(Zone.getAllName());
                break;
            case 4:
                subcommandArguments.clear();
                if (sender instanceof Player && args[2].equalsIgnoreCase("all")) {
                    Player player = (Player) sender;
                    subcommandArguments.add(String.valueOf((int) player.getLocation().getX()));
                }

                if (!args[2].equalsIgnoreCase("all")) {
                    subcommandArguments.add(String.valueOf(JSONProperties.properties));
                    subcommandArguments.add(String.valueOf(JSONProperties.flag));
                    subcommandArguments.add(String.valueOf(JSONProperties.furLough));
                    subcommandArguments.add(String.valueOf(JSONProperties.resident));
                }


                break;
            case 5:
                subcommandArguments.clear();
                if (sender instanceof Player && args[2].equalsIgnoreCase("all")) {
                    Player player = (Player) sender;
                    subcommandArguments.add(String.valueOf((int) player.getLocation().getY()));
                }
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("flag")) {
                    for (RegionFlag regionFlag : RegionFlag.values()) {
                        subcommandArguments.add(String.valueOf(regionFlag));
                    }
                }
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("furlough")) {
                    for (RegionFurlough regionFurlough : RegionFurlough.values()) {
                        subcommandArguments.add(String.valueOf(regionFurlough));
                    }
                }
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("resident")) {
                    subcommandArguments.add("add");
                    subcommandArguments.add("remove");
                }
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("properties")) {
                    Zone zone = Zone.getZoneByName(args[2]);
                    if(zone == null) return null;

                    JSONObject jsonObject = (JSONObject) zone.getJSON().get("properties");
                    subcommandArguments.addAll(jsonObject.keySet());
                    subcommandArguments.remove("_ID");
                }
                break;
            case 6:
                subcommandArguments.clear();
                if (sender instanceof Player && args[2].equalsIgnoreCase("all")) {
                    Player player = (Player) sender;
                    subcommandArguments.add(String.valueOf((int) player.getLocation().getZ()));
                }
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("flag")) {

                    RegionFlag regionFlag = RegionFlag.valueOf(args[4]);

                    if (regionFlag.getType() instanceof Boolean) {
                        subcommandArguments.add("true");
                        subcommandArguments.add("false");
                        subcommandArguments.add("remove");
                    } else if (regionFlag.getType() instanceof String) {
                        subcommandArguments.add("<text>");
                    } else if (regionFlag.getType() instanceof Location) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            subcommandArguments.add(String.valueOf((int) player.getLocation().getX()));
                        }
                        subcommandArguments.add("x");
                    }

                }
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("furlough")) {
                    for(UUID uuid : Zone.getZoneByName(args[2]).getResident().getAll()){
                        OfflinePlayer player = Region.plugin.getServer().getOfflinePlayer(uuid);
                        subcommandArguments.add(player.getName());
                    }
                }
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("resident")) {
                    if(args[4].equalsIgnoreCase("add")) {
                        for (Player player : Region.plugin.getServer().getOnlinePlayers()) {
                            subcommandArguments.add(player.getName());
                        }
                    }
                    if(args[4].equalsIgnoreCase("remove")) {
                        for(UUID uuid : Zone.getZoneByName(args[2]).getResident().getAll()){
                            OfflinePlayer player = Region.plugin.getServer().getOfflinePlayer(uuid);
                            subcommandArguments.add(player.getName());
                        }
                    }
                }
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("properties")){
                    RegionProperties regionProperties = RegionProperties.valueOf(args[4]);

                    if(regionProperties.equals(RegionProperties.world)){
                        for (World world : Region.plugin.getServer().getWorlds()) {
                            subcommandArguments.add(world.getName());
                        }
                    }else if(regionProperties.getType() instanceof String){
                        subcommandArguments.add("<text>");
                    }

                    if(regionProperties.getType() instanceof Integer){
                        subcommandArguments.add("<int>");
                    }
                }
                break;
            case 7:
                subcommandArguments.clear();
                if (args[2].equalsIgnoreCase("all")) {
                    for (World world : Region.plugin.getServer().getWorlds()) {
                        subcommandArguments.add(world.getName());
                    }
                }
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("flag")) {

                    RegionFlag regionFlag = RegionFlag.valueOf(args[4]);

                    if (regionFlag.getType() instanceof Location) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            subcommandArguments.add(String.valueOf((int) player.getLocation().getY()));
                        }
                        subcommandArguments.add("y");
                    }
                }
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("furlough")) {
                    subcommandArguments.add("true");
                    subcommandArguments.add("false");
                    subcommandArguments.add("remove");
                }
                break;
            case 8:
                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("flag")) {

                    RegionFlag regionFlag = RegionFlag.valueOf(args[4]);

                    if (regionFlag.getType() instanceof Location) {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            subcommandArguments.add(String.valueOf((int) player.getLocation().getZ()));
                        }
                        subcommandArguments.add("z");
                    }
                }
                break;
            case 9:

                if (args[1].equalsIgnoreCase("modify") && args[3].equalsIgnoreCase("flag")) {

                    RegionFlag regionFlag = RegionFlag.valueOf(args[4]);

                    if (regionFlag.getType() instanceof Location) {
                        for (World world : Region.plugin.getServer().getWorlds()) {
                            subcommandArguments.add(world.getName());
                        }
                    }
                }
                break;
        }

        }catch (IllegalArgumentException illegalArgumentException){
            if(args[3].equalsIgnoreCase("flag")){
                sender.sendMessage(ChatColor.RED+"This flag not exists");
            }
            if(args[3].equalsIgnoreCase("furlough")) {
                sender.sendMessage(ChatColor.RED+"This furlough not exits");
            }
        }
        return subcommandArguments;
    }

    private void performSubcommandGetFirstZonePlayer(Player player,String[] args){
        Location location = player.getLocation();
        List<Zone> Z = Zone.getZoneByCords(location);

        if(Z.isEmpty()){
            player.sendMessage(ChatColor.RED+"Don't any zone in this place");
            return;
        }

        Zone zone = Z.get(0);

        for(String key : zone.getJSON().keySet()){
            player.sendMessage(ChatColor.LIGHT_PURPLE+key+" : "+zone.getJSON().get(key).toString());
        }

    }
    private void performSubcommandGetAllZonePlayer(Player player, String[] args){

        if(args.length != 3 & args.length != 6 && args.length != 7){
            player.sendMessage(ChatColor.YELLOW+"/region data get all <x> <y> <z> <world> : Return all Zone in cords");
            return;
        }

        Location location = player.getLocation();
        List<Zone> Z = Zone.getZoneByCords(location);

        if(args.length == 3){
            Z = Zone.getZoneList();
        }

        if(args.length == 6){
            if(!Util.isInt(args[3])){player.sendMessage(ChatColor.YELLOW+"<x> must be a INT"); return;}
            if(!Util.isInt(args[4])){player.sendMessage(ChatColor.YELLOW+"<y> must be a INT"); return;}
            if(!Util.isInt(args[5])){player.sendMessage(ChatColor.YELLOW+"<z> must be a INT"); return;}

            location.setX(Double.parseDouble(args[3]));
            location.setY(Double.parseDouble(args[4]));
            location.setZ(Double.parseDouble(args[5]));
        }
        if(args.length == 7){
            if(!Util.isInt(args[3])){player.sendMessage(ChatColor.YELLOW+"<x> must be a INT"); return;}
            if(!Util.isInt(args[4])){player.sendMessage(ChatColor.YELLOW+"<y> must be a INT"); return;}
            if(!Util.isInt(args[5])){player.sendMessage(ChatColor.YELLOW+"<z> must be a INT"); return;}

            location.setX(Double.parseDouble(args[3]));
            location.setY(Double.parseDouble(args[4]));
            location.setZ(Double.parseDouble(args[5]));

            if(Region.plugin.getServer().getWorld(args[6]) == null){
                player.sendMessage(ChatColor.YELLOW+"This world not exits");
                return;
            }

            location.setWorld(Region.plugin.getServer().getWorld(args[6]));
        }

        if(Z.isEmpty()){
            player.sendMessage(ChatColor.RED+"Don't any zone in this place");
            return;
        }

        for(Zone zone : Z){
            player.sendMessage(ChatColor.AQUA+zone.getName()+" - "+zone.get_ID());
            for(String key : zone.getJSON().keySet()){
                player.sendMessage(ChatColor.LIGHT_PURPLE+key+" : "+zone.getJSON().get(key).toString());
            }
        }

    }

    private void performSubcommandGetNameZonePlayer(Player player, String[] args){
        Zone zone = Zone.getZoneByName(args[2]);

        Set<String> keys = zone.getJSON().keySet();

        if(args.length == 4 && keys.contains(args[3])){
            player.sendMessage(ChatColor.LIGHT_PURPLE+zone.getJSON().get(args[3]).toString());
            return;
        }

        for(String key : zone.getJSON().keySet()){
            player.sendMessage(ChatColor.LIGHT_PURPLE+key+" - "+zone.getJSON().get(key).toString());
        }

    }

    private void performSubcommandGetZoneCmd(CommandSender sender, String[] args){
        if(args.length != 3 && args.length != 7){
            sender.sendMessage(ChatColor.YELLOW+"/region data get all <x> <y> <z> <world> : Return all Zone in cords");
            return;
        }
        if(args.length == 3) {
            List<Zone> Z = Zone.getZoneList();

            if (Z.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Don't any zone in this place");
                return;
            }

            for (Zone zone : Z) {
                sender.sendMessage(ChatColor.AQUA + zone.getName() + " - " + zone.get_ID());
                for (String key : zone.getJSON().keySet()) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + key + " : " + zone.getJSON().get(key).toString());
                }
            }
            return;
        }
        if(args.length == 7) {

            if (!Util.isInt(args[3])) {
                sender.sendMessage(ChatColor.YELLOW + "<x> must be a INT");
                return;
            }
            if (!Util.isInt(args[4])) {
                sender.sendMessage(ChatColor.YELLOW + "<y> must be a INT");
                return;
            }
            if (!Util.isInt(args[5])) {
                sender.sendMessage(ChatColor.YELLOW + "<z> must be a INT");
                return;
            }

            if (Region.plugin.getServer().getWorld(args[6]) == null) {
                sender.sendMessage(ChatColor.YELLOW + "This world not exits");
                return;
            }

            Location location = new Location(
                    Region.plugin.getServer().getWorld(args[6]),
                    Double.parseDouble(args[3]),
                    Double.parseDouble(args[4]),
                    Double.parseDouble(args[5])
            );

            List<Zone> Z = Zone.getZoneByCords(location);

            if (Z.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Don't any zone in this place");
                return;
            }

            for (Zone zone : Z) {
                sender.sendMessage(ChatColor.AQUA + zone.getName() + " - " + zone.get_ID());
                for (String key : zone.getJSON().keySet()) {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + key + " : " + zone.getJSON().get(key).toString());
                }
            }
        }
    }

    private void performSubcommandGetNameZoneCmd(CommandSender sender, String[] args){
        Zone zone = Zone.getZoneByName(args[2]);

        Set<String> keys = zone.getJSON().keySet();

        if(args.length == 4 && keys.contains(args[3])){
            sender.sendMessage(ChatColor.LIGHT_PURPLE+zone.getJSON().get(args[3]).toString());
            return;
        }

        for(String key : zone.getJSON().keySet()){
            sender.sendMessage(ChatColor.LIGHT_PURPLE+key+" - "+zone.getJSON().get(key).toString());
        }
    }

    private void performSubcommnadModifyZoneFlag(CommandSender sender, String[] args){

        if(args.length <= 4) {
            sender.sendMessage(ChatColor.YELLOW+this.getSyn());
            return;
        }

        RegionFlag regionFlag = RegionFlag.valueOf(args[4]);
        Zone zone = Zone.getZoneByName(args[2]);

        if (regionFlag.getType() instanceof Boolean && args.length == 6) {
            // /vrg data modify <name> flag <flag> <value>
            // / -1  0   1      2      3    4      5
            if(!args[5].equals("true") && !args[5].equals("false") && !args[5].equals("remove")){
                sender.sendMessage(ChatColor.RED+"Must be true or false");
                return;
            }

            if(!args[5].equals("remove")) zone.getFlag().setFlag(regionFlag, Boolean.valueOf(args[5]));
            if(args[5].equals("remove")) zone.getFlag().removeFlag(regionFlag);

            zone.saveZone();
        } else if (regionFlag.getType() instanceof String && args.length == 6) {
            zone.getFlag().setFlag(regionFlag, args[5]);
            zone.saveZone();
        } else if (regionFlag.getType() instanceof Location && args.length == 9) {
            if (Region.plugin.getServer().getWorld(args[8]) == null) {
                //vrg data modify <name> flag location x y z world
                // -1 0    1      2      3    4        5 6 7 8
                sender.sendMessage(ChatColor.YELLOW + "This world not exits");
                return;
            }

            if(!Util.isInt(args[5])){sender.sendMessage(ChatColor.YELLOW+"<x> must be a INT"); return;}
            if(!Util.isInt(args[6])){sender.sendMessage(ChatColor.YELLOW+"<y> must be a INT"); return;}
            if(!Util.isInt(args[7])){sender.sendMessage(ChatColor.YELLOW+"<z> must be a INT"); return;}
            Location location = new Location(
                    Region.plugin.getServer().getWorld(args[8]),
                    (int) Double.parseDouble(args[5]),
                    (int) Double.parseDouble(args[6]),
                    (int) Double.parseDouble(args[7])
                    );
            JSONObject jsonObject = new JSONObject(location.serialize());

            zone.getFlag().setFlag(regionFlag, jsonObject);
            zone.saveZone();

        }else{
            sender.sendMessage(ChatColor.YELLOW+this.getSyn());
        }
    }

    private void performSubcommandModifyZoneFurlough(CommandSender sender, String[] args){
        // /region data modify <name> furlough <furlough> <player> <value>

        if(args.length != 7){
            sender.sendMessage(ChatColor.YELLOW+this.getSyn());
            return;
        }

        RegionFurlough regionFurlough = RegionFurlough.valueOf(args[4]);
        Zone zone = Zone.getZoneByName(args[2]);
        OfflinePlayer target = null;

        for(UUID uuid : zone.getResident().getAll()){
            target = Region.plugin.getServer().getOfflinePlayer(uuid);
            if(target.getName().equals(args[5])) break;
        }

        if(target == null){
            sender.sendMessage(ChatColor.RED+"This player is not added in the zone");
            return;
        }

        if(!args[6].equals("true") && !args[6].equals("false") && !args[6].equals("remove")){
            sender.sendMessage(ChatColor.RED+"Must be true or false");
            return;
        }

        if(!zone.getResident().contains(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "The player not exit in this zone");
            return;
        }

        zone.getFurlough().setPlayerFurLough(target.getUniqueId(), regionFurlough, Boolean.valueOf(args[6]));
        zone.saveZone();

    }

    private void performSubcommandModifyZoneResident(CommandSender sender, String[] args){
      //  /region data modify <name> resident <add / remove> <player>
        if(args.length != 6){
            sender.sendMessage(ChatColor.YELLOW+this.getSyn());
            return;
        }

        Zone zone = Zone.getZoneByName(args[2]);


        if(args[4].equalsIgnoreCase("add")){
            Player target = Region.plugin.getServer().getPlayerExact(args[5]);
            if(target == null){
                sender.sendMessage(ChatColor.RED+"The player is not connected");
                return;
            }
            if(zone.getResident().contains(target.getUniqueId())){
                sender.sendMessage(ChatColor.RED+"The player is resident");
                return;
            }

            zone.getResident().add(target.getUniqueId());
            zone.saveZone();
            return;
        }else if(args[4].equalsIgnoreCase("remove")){
            OfflinePlayer target = null;
            for(UUID uuid : zone.getResident().getAll()){
                target = Region.plugin.getServer().getOfflinePlayer(uuid);
                if(target.getName().equals(args[5])) break;
            }

            if(target == null){
                sender.sendMessage(ChatColor.RED+"The player is not in the region");
                return;
            }
            zone.removePlayer(target.getUniqueId());
            zone.saveZone();
            return;
        }else {
            sender.sendMessage(ChatColor.RED + "Use add or remove");
            zone.saveZone();
            return;
        }

    }

    private void performSubcommandModifyZoneProperties(CommandSender sender, String[] args){

        if(args.length != 6){
            sender.sendMessage(ChatColor.YELLOW+this.getSyn());
            return;
        }

        RegionProperties regionProperties = RegionProperties.valueOf(args[4]);
        Zone zone = Zone.getZoneByName(args[2]);
        JSONObject jsonObject = zone.getJsonObjectProperties();

        if(regionProperties.getType() instanceof Integer){
            if(!Util.isInt(args[5])) {
                sender.sendMessage(ChatColor.YELLOW + "This value must be a int");
                return;

            }
        }
        if(regionProperties.name().equals(RegionProperties.world.name())){
            if(Region.plugin.getServer().getWorld(args[5]) == null){
                sender.sendMessage(ChatColor.YELLOW + "This world not exits");
                return;
            }
        }

        if(regionProperties.getType() instanceof Integer){
            jsonObject.put(RegionProperties.valueOf(args[4]).name(), Integer.valueOf(args[5]));
        }else if(regionProperties.getType() instanceof String){
            jsonObject.put(RegionProperties.valueOf(args[4]).name(), args[5]);
        }

        zone.setJsonObjectProperties(jsonObject);
        zone.saveZone();
    }


    private boolean isPlayerAndGet(CommandSender sender, String[] args){
        return sender instanceof Player && args[1].equalsIgnoreCase("get");
    }
}
