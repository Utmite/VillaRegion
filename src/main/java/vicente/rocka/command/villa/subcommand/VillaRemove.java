package vicente.rocka.command.villa.subcommand;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vicente.rocka.region.Region;
import vicente.rocka.region.Zone;
import vicente.rocka.util.command.SubCommand;
import vicente.rocka.util.enums.RegionFurlough;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VillaRemove implements SubCommand {

    private String name_command = Region.plugin.getConfig().getString("commands.villa.remove.name");

    private String description_command = Region.plugin.getConfig().getString("commands.villa.remove.description");

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
        return "/villa "+name_command+" <villa> <player>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("The sender must be a Player");
            return;
        }
        Player player = (Player) sender;

        if(args.length != 3){
            BaseComponent[] component = new ComponentBuilder(this.getSyn()).append("!").color(ChatColor.RED).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        List<Zone> villas = Zone.getZonePlayerFurlough(player.getUniqueId(), new RegionFurlough[]{RegionFurlough.Owner, RegionFurlough.Kick});

        villas.removeIf(e -> !e.getName().equals(args[1]));



        if(villas.isEmpty()){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.remove.error.not_found_villa"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        Zone villa = villas.get(0);
        OfflinePlayer target = null;
        for(UUID uuid : villa.getResident().getAll()){
            target = Region.plugin.getServer().getOfflinePlayer(uuid);
            if(target.getName().equals(args[2])) break;
        }

        if(target == null){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.remove.error.not_found_player"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }


        if(!villa.getResident().contains(target.getUniqueId())){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.remove.error.not_player_in_zone"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        if(villa.getFurlough().getPlayerFurLough(target.getUniqueId(), RegionFurlough.Owner)
                || villa.getFurlough().getPlayerFurLough(target.getUniqueId(), RegionFurlough.Kick)){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.remove.error.can_not_remove_player_with_same_permission"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        villa.removePlayer(target.getUniqueId());
        villa.saveZone();

        BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                .getString("commands.villa.remove.success.removed"))
                .append("!")
                .color(ChatColor.RED)
                .create();

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    @Override
    public List<String> getSubcommandArguments(CommandSender sender, String[] args) {
        List<String> subArguments = new ArrayList<>();
        if(!(sender instanceof Player)) return subArguments;

        Player player = (Player) sender;
        switch (args.length){
            case 2:
                Zone.getZonePlayerFurlough(player.getUniqueId(), new RegionFurlough[]{RegionFurlough.Owner, RegionFurlough.Kick})
                        .stream()
                        .map(e -> e.getName())
                        .filter(e -> e.toUpperCase().startsWith(args[1].toUpperCase()))
                        .forEach(e -> subArguments.add(e));
                break;
            case 3:
                List<Zone> villas = Zone.getZonePlayerFurlough(player.getUniqueId(), new RegionFurlough[]{RegionFurlough.Owner, RegionFurlough.Kick});

                villas.removeIf(e -> !e.getName().equals(args[1]));
                Zone villa = villas.get(0);

                if(villas.isEmpty()){
                    return subArguments;
                }

                for(UUID uuid : villa.getResident().getAll()){
                    OfflinePlayer target = Region.plugin.getServer().getOfflinePlayer(uuid);
                    subArguments.add(target.getName());
                }
        }
        return subArguments;    }
}
