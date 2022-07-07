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

public class VillaAdd implements SubCommand {

    private String name_command = Region.plugin.getConfig().getString("commands.villa.add.name");

    private String description_command = Region.plugin.getConfig().getString("commands.villa.add.description");

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

        Player target = Bukkit.getPlayerExact(args[2]);
        List<Zone> villas = Zone.getZonePlayerFurlough(player.getUniqueId(), new RegionFurlough[]{RegionFurlough.Owner, RegionFurlough.Add});

        villas.removeIf(e -> !e.getName().equals(args[1]));

        if(villas.isEmpty()){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.add.error.not_found_villa"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }
        if(target == null){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.add.error.not_found_player"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        Zone villa = villas.get(0);

        if(villa.getResident().contains(target.getUniqueId())){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.add.error.player_already_in_zone"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        villa.getResident().add(target);
        villa.saveZone();

        BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                .getString("commands.villa.add.success.added"))
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
                Zone.getZonePlayerFurlough(player.getUniqueId(), new RegionFurlough[]{RegionFurlough.Owner, RegionFurlough.Add})
                        .stream()
                        .map(e -> e.getName())
                        .forEach(e -> subArguments.add(e));
                break;
            case 3:
                Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(e -> {
                    subArguments.add(e);
                    subArguments.remove(player.getName());
                });
        }
        return subArguments;    }
}
