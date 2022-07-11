package vicente.rocka.command.villa.subcommand;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.JSONObject;
import vicente.rocka.region.*;
import vicente.rocka.util.command.SubCommand;
import vicente.rocka.util.enums.RegionFlag;
import vicente.rocka.util.enums.RegionFurlough;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VillaCreate implements SubCommand {

    private String name_command = Region.plugin.getConfig().getString("commands.villa.create.name");
    private String description_command = Region.plugin.getConfig().getString("commands.villa.create.description");

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
        return "/villa "+name_command+" <name>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("The sender must be a Player");
            return;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();

        if(args.length != 2){
            BaseComponent[] component = new ComponentBuilder(this.getSyn()).append("!").color(ChatColor.RED).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        String villa_name = args[1];

        int size_villa = Region.plugin.getConfig().getInt("villa_specification.size_villa");
        int size_name = Region.plugin.getConfig().getInt("villa_specification.size_name");
        int max_villa_per_player = Region.plugin.getConfig().getInt("villa_specification.max_villa_per_player");

        Set<String> classifications = Region.plugin.getConfig().getConfigurationSection("villa_specification.specification_by_perm").getKeys(false);

        for(String classification : classifications){
            String permission = Region.plugin.getConfig().getString(
                    "villa_specification.specification_by_perm."+classification+".permission");
            if(player.hasPermission(permission)){
                size_villa = Region.plugin.getConfig().getInt("villa_specification.specification_by_perm."+classification+".size_villa");
                size_name = Region.plugin.getConfig().getInt("villa_specification.specification_by_perm."+classification+".size_name");
                max_villa_per_player = Region.plugin.getConfig().getInt("villa_specification.specification_by_perm."+classification+".max_villa_per_player");
            }
        }

        boolean overlapping_villa = Region.plugin.getConfig().getBoolean("villa_specification.overlapping_villa");

        if(Zone.isUsedName(villa_name)) {
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.create.error.name_usage")
                    .replace("%name_usage%", villa_name+""))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }
        if(villa_name.length() > size_name){
            BaseComponent[] component = new ComponentBuilder(
                    Region.plugin.getConfig()
                            .getString("commands.villa.create.error.size_name")
                            .replace("%size_name%", size_name+""))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        int count_villa_player = Zone.numberZoneFurloughPlayer(player.getUniqueId(), RegionFurlough.Owner);

        if(count_villa_player >= max_villa_per_player){
            BaseComponent[] component = new ComponentBuilder(
                    Region.plugin.getConfig().
                            getString("commands.villa.create.error.max_villa_per_player")
                            .replace("%max_villa_per_player%", max_villa_per_player+"")
                            .replace("%count_villa_player%", count_villa_player+""))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        Furlough furlough = new Furlough();
        furlough.setPlayerFurLough(player, RegionFurlough.Owner, true);

        Resident resident = new Resident();
        resident.add(player);



        Zone zone = new Villa(
                new Rectangle((int) location.getX() - (size_villa / 2),
                (int) location.getZ() - (size_villa / 2),
                size_villa,
                size_villa),
                -1000,
                1000,
                location.getWorld(),
                villa_name,
                furlough,
                resident
        );

        zone.getFlag().setFlag(RegionFlag.Spawn, player.getLocation());

        if(!overlapping_villa){
            Zone collision = Zone.getOverlapping(zone);
            if(collision != null){
                BaseComponent[] component = new ComponentBuilder(
                        Region.plugin.getConfig().
                                getString("commands.villa.create.error.overlapping_villa")
                                .replace("%overlapping_villa%", collision.getName()))
                        .append("!")
                        .color(ChatColor.RED)
                        .create();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
                return;
            }
        }

        zone.saveZone();

        BaseComponent[] component = new ComponentBuilder(
                Region.plugin.getConfig().
                        getString("commands.villa.create.success.create_region")
                        .replace("%create_region%", zone.getName()+""))
                .append("!")
                .color(ChatColor.GREEN)
                .create();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        List<String> subArguments = new ArrayList<>();

        switch (args.length){
            case 2:
                subArguments.add("<name>");
        }
        return subArguments;
    }
}
