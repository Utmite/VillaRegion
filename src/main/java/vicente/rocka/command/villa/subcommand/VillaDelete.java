package vicente.rocka.command.villa.subcommand;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vicente.rocka.region.Region;
import vicente.rocka.region.Zone;
import vicente.rocka.util.command.SubCommand;
import vicente.rocka.util.enums.RegionFurlough;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VillaDelete implements SubCommand {
    private String name_command = Region.plugin.getConfig().getString("commands.villa.delete.name");

    private String description_command = Region.plugin.getConfig().getString("commands.villa.delete.description");

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
        List<Zone> zones = Zone.getZonePlayerFurlough(player.getUniqueId(), RegionFurlough.Owner);

        if(args.length != 2){
            BaseComponent[] component = new ComponentBuilder(this.getSyn()).append("!").color(ChatColor.RED).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        zones.removeIf(e -> !e.getName().equals(args[1]));

        if(zones.isEmpty()){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.delete.error.not_found")
                    .replace("%name_villa%", args[1]+""))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        Zone zone = zones.get(0);
        zone.removeZone();

        BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                .getString("commands.villa.delete.success.deleted")
                .replace("%name_villa%", args[1]+""))
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
        switch (args.length){
            case 2:
                Zone.getZonePlayerFurlough(player.getUniqueId(), RegionFurlough.Owner)
                        .stream()
                        .map(e -> e.getName())
                        .forEach(e -> subArguments.add(e));

        }
        return subArguments;
    }
}
