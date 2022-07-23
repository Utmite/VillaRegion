package vicente.rocka.command.villa.subcommand;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;
import vicente.rocka.region.Region;
import vicente.rocka.region.Zone;
import vicente.rocka.util.command.SubCommand;
import vicente.rocka.util.enums.RegionFlag;
import vicente.rocka.util.enums.RegionFurlough;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VillaConfig implements SubCommand {

    private String name_command = Region.plugin.getConfig().getString("commands.villa.config.name");

    private String description_command = Region.plugin.getConfig().getString("commands.villa.config.description");

    private String subCommnadPermission = Region.plugin.getConfig().getString("commands.villa.config.subcommands.permission");

    private String subCommandName = Region.plugin.getConfig().getString("commands.villa.config.subcommands.name");

    private String subCommandSpawn = Region.plugin.getConfig().getString("commands.villa.config.subcommands.spawn");

    @Override
    public String getName() {
        return name_command;
    }

    @Override
    public String getDes() {
        return description_command;
    }

    @Override
    public String getSyn() {
        return "/villa "+name_command+" <villa> <option> ...";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("The sender must be a Player");
            return;
        }


        Player player = (Player) sender;
        if(args.length <= 2){
            BaseComponent[] component = new ComponentBuilder(this.getSyn()).append("!").color(ChatColor.RED).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        Zone villa = Zone.getZoneByName(args[1]);

        if(villa == null){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.config.error.not_found_villa"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        if(!villa.getResident().contains(player.getUniqueId())){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.config.error.player_is_not_resident"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        if(args[2].equalsIgnoreCase(subCommandSpawn)) this.performSetSpawn(villa, player, args);

        if(args[2].equalsIgnoreCase(subCommandName)) this.performSetName(villa, player, args);

        if(args[2].equalsIgnoreCase(subCommnadPermission)) this.performSetPermission(villa, player, args);



    }

    private void performSetPermission(Zone villa, Player player, String[] args){
        if(
                !villa.getFurlough().getPlayerFurLough(player.getUniqueId(), RegionFurlough.Owner)
        ){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.config.error.player_doesnt_have_permission"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        if(args.length != 6){
            BaseComponent[] component = new ComponentBuilder(
                    "/villa "+name_command+" "+args[1]+" "+subCommnadPermission+" <key> <player> <value>"
            ).append("!").color(ChatColor.RED).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        try {
            RegionFurlough regionFurlough = RegionFurlough.valueOf(args[3]);
            Player target = Bukkit.getPlayerExact(args[4]);
            String value = args[5];

            if (!value.equalsIgnoreCase("false")
                && !value.equalsIgnoreCase("true")
                && !value.equalsIgnoreCase("remove"))
            {
                BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                        .getString("commands.villa.config.error.option_not_valid"))
                        .append("!")
                        .color(ChatColor.RED)
                        .create();

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
                return;
            }

            if(regionFurlough.equals(RegionFurlough.Owner) && value.equalsIgnoreCase("true")){

                int max_villa_per_player = Region.plugin.getConfig().getInt("villa_specification.max_villa_per_player");
                int count_villa_player = Zone.getNumberZoneFurloughPlayer(target.getUniqueId(), RegionFurlough.Owner);

                Set<String> specification_by_perm = Region.plugin.getConfig().getConfigurationSection("villa_specification.specification_by_perm").getKeys(false);

                for(String perm : specification_by_perm){
                    if(player.hasPermission(perm)){
                        max_villa_per_player = Region.plugin.getConfig().getInt("villa_specification.specification_by_perm."+perm+".max_villa_per_player");
                    }
                }

                if(count_villa_player >= max_villa_per_player){
                    BaseComponent[] component = new ComponentBuilder(
                            Region.plugin.getConfig().
                                    getString("commands.villa.config.error.max_villa_per_player")
                                    .replace("%max_villa_per_player%", max_villa_per_player+"")
                                    .replace("%count_villa_player%", count_villa_player+""))
                            .append("!")
                            .color(ChatColor.RED)
                            .create();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
                    return;
                }

                villa.getFurlough().removePlayerFurLough(player.getUniqueId(), RegionFurlough.Owner);
                villa.getFurlough().setPlayerFurLough(target.getUniqueId(), RegionFurlough.Owner, Boolean.valueOf(value));




            }else if(value.equalsIgnoreCase("remove") && !regionFurlough.equals(RegionFurlough.Owner)){
                villa.getFurlough().removePlayerFurLough(target.getUniqueId(), regionFurlough);
            }else if(!regionFurlough.equals(RegionFurlough.Owner)){
                villa.getFurlough().setPlayerFurLough(target.getUniqueId(), regionFurlough, Boolean.valueOf(value));
            }
            villa.saveZone();

            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.config.success.change_permission"))
                    .append("!")
                    .color(ChatColor.GREEN)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);

        }catch (IllegalArgumentException illegalArgumentException){
            if(args.length != 6){
                BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                        .getString("commands.villa.config.error.invalid_permission_name"))
                        .append("!")
                        .color(ChatColor.RED)
                        .create();

                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
                return;
            }
        }


    }
    private void performSetSpawn(Zone villa, Player player, String[] args){

        if(
                !villa.getFurlough().getPlayerFurLough(player.getUniqueId(), RegionFurlough.Set_spawn) &&
                !villa.getFurlough().getPlayerFurLough(player.getUniqueId(), RegionFurlough.Owner)
        ){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.config.error.player_doesnt_have_permission"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        villa.getFlag().setFlag(RegionFlag.Spawn, new JSONObject(player.getLocation().serialize()));
        villa.saveZone();

        BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                .getString("commands.villa.config.success.change_spawn"))
                .append("!")
                .color(ChatColor.GREEN)
                .create();

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);

    }

    private void performSetName(Zone villa, Player player, String[] args){

        if(
            !villa.getFurlough().getPlayerFurLough(player.getUniqueId(), RegionFurlough.Set_name) &&
            !villa.getFurlough().getPlayerFurLough(player.getUniqueId(), RegionFurlough.Owner)
        ){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.config.error.player_doesnt_have_permission"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }
        if(args.length != 4){
            BaseComponent[] component = new ComponentBuilder(
                    "/villa "+name_command+" "+args[1]+" "+subCommandName+" <"+subCommandName+">"
                    ).append("!").color(ChatColor.RED).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        villa.setName(args[3]);
        villa.saveZone();

        BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                .getString("commands.villa.config.success.change_name"))
                .append("!")
                .color(ChatColor.GREEN)
                .create();

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);

    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        List<String> subArguments = new ArrayList<>();
        if(!(sender instanceof Player)) return subArguments;

        Player player = (Player) sender;
        Zone villa = null;
        if(args.length > 2){
            villa = Zone.getZoneByName(args[1]);
            if(villa == null) return subArguments;
            if(
                    !villa.getFurlough().getPlayerFurLough(player.getUniqueId(), RegionFurlough.Set_spawn) &&
                    !villa.getFurlough().getPlayerFurLough(player.getUniqueId(), RegionFurlough.Owner) &&
                    !villa.getFurlough().getPlayerFurLough(player.getUniqueId(), RegionFurlough.Set_name)
            ){
                return subArguments;
            }
        }


        switch (args.length){
            case 2:
                Zone.getAllZonePlayerIsResident(player.getUniqueId()).stream()
                        .map(e -> e.getName())
                        .filter(e -> e.toUpperCase().startsWith(args[1].toUpperCase()))
                        .forEach(e -> subArguments.add(e));
                break;
            case 3:

                subArguments.add(subCommnadPermission);
                subArguments.add(subCommandSpawn);
                subArguments.add(subCommandName);

                break;
            case 4:


                if(args[2].equalsIgnoreCase(subCommandName)){
                    subArguments.add("<"+subCommandName+">");
                    return subArguments;
                }

                if(!args[2].equalsIgnoreCase(subCommnadPermission)) return subArguments;

                for(RegionFurlough regionFurlough : RegionFurlough.values()){
                    subArguments.add(regionFurlough.name());
                }


                break;
            case 5:
                if(villa == null) return subArguments;
                if(!args[2].equalsIgnoreCase(subCommnadPermission)) return subArguments;
                if(!villa.getResident().contains(player.getUniqueId())) return subArguments;


                for(UUID uuid : villa.getResident().getAll()){
                    OfflinePlayer offlinePlayer = Region.plugin.getServer().getOfflinePlayer(uuid);
                    subArguments.add(offlinePlayer.getName());
                }
                break;
            case 6:
                if(!args[2].equalsIgnoreCase(subCommnadPermission)) return subArguments;

                subArguments.add("true");
                subArguments.add("false");
                subArguments.add("remove");

                break;
        }


        return subArguments;
    }
}
