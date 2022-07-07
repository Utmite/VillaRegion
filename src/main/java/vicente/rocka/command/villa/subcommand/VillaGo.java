package vicente.rocka.command.villa.subcommand;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.json.JSONObject;
import vicente.rocka.region.Region;
import vicente.rocka.region.Zone;
import vicente.rocka.util.command.SubCommand;
import vicente.rocka.util.enums.RegionFlag;
import vicente.rocka.util.enums.RegionFurlough;

import java.util.ArrayList;
import java.util.List;

public class VillaGo implements SubCommand {

    private String name_command = Region.plugin.getConfig().getString("commands.villa.go.name");

    private String description_command = Region.plugin.getConfig().getString("commands.villa.go.description");

    private Permission permission = new Permission(Region.plugin.getConfig().getString("commands.villa.go.permission"));
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
        return "/villa "+name_command+" <villa>";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("The sender must be a Player");
            return;
        }

        Player player = (Player) sender;

        if(args.length != 2){
            BaseComponent[] component = new ComponentBuilder(this.getSyn()).append("!").color(ChatColor.RED).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        if(!Region.plugin.getConfig().getBoolean("commands.villa.go.all_can_use") && !player.hasPermission(permission)){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.go.error.not_permission"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        Zone villa = Zone.getZoneByName(args[1]);


        if(villa == null){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.go.error.not_found_villa"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        if(!villa.getResident().contains(player.getUniqueId()) && !player.isOp() && villa.getFlag().getFlag(RegionFlag.Tpa_All).equals("false")){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.go.error.player_is_not_resident"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        String location = villa.getFlag().getFlag(RegionFlag.Spawn);

        if(location == null){
            BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                    .getString("commands.villa.go.error.villa_dont_have_spawn"))
                    .append("!")
                    .color(ChatColor.RED)
                    .create();

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return;
        }

        Location spawn = Location.deserialize(new JSONObject(location).toMap());

        player.teleport(spawn);

        BaseComponent[] component = new ComponentBuilder(Region.plugin.getConfig()
                .getString("commands.villa.go.success.done"))
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
                if(player.isOp()){
                    Zone.getZoneList().forEach(e -> subArguments.add(e.getName()));
                    return subArguments;
                }
                Zone.getAllZonePlayerIsResident(player.getUniqueId()).forEach(e -> {
                    if(e.getFlag().getFlag(RegionFlag.Tpa_Resident).equals("true")) subArguments.add(e.getName());
                });
                Zone.getAllZoneByFlag(RegionFlag.Tpa_All, "true").forEach(e -> {
                    subArguments.add(e.getName());
                });
        }
        return subArguments;
    }
}
