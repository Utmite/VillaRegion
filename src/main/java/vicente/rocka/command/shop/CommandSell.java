package vicente.rocka.command.shop;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.json.JSONArray;
import org.json.JSONObject;
import vicente.rocka.events.custom.PlayerCloseMerchantEvent;
import vicente.rocka.events.custom.PlayerSellItem;
import vicente.rocka.region.Region;
import vicente.rocka.util.Util;
import vicente.rocka.util.command.SubCommand;
import vicente.rocka.villaregion.VillaRegion;


import java.util.ArrayList;
import java.util.List;

public class CommandSell implements TabExecutor {

    final private ArrayList<SubCommand> subCommand = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        PlayerInventory inventory = player.getInventory();

        if(inventory.getItemInMainHand().getType().equals(Material.AIR)){
            String error = Region.plugin.getConfig().getString("shop.error.not_item_hand");

            if(error == null) return false;

            BaseComponent[] component = new ComponentBuilder(ChatColor.RED+error).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return false;
        }

        if(args.length != 1){
            BaseComponent[] component = new ComponentBuilder(ChatColor.RED+"/vendo <precio>").create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return false;
        }

        if(!Util.isInt(args[0])){
            String error = Region.plugin.getConfig().getString("shop.error.not_is_int");

            if(error == null) return false;

            BaseComponent[] component = new ComponentBuilder(ChatColor.RED+error).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return false;
        }
        int price = Integer.valueOf(args[0]);
        if(price >= 4160 || price == 0){
            String error = Region.plugin.getConfig().getString("shop.error.not_safe_number");

            BaseComponent[] component = new ComponentBuilder(ChatColor.RED+error).create();
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
            return false;
        }


        ItemStack itemStack = inventory.getItemInMainHand();

        if(itemStack.getType().equals(Material.GOLD_NUGGET) && (itemStack.getItemMeta().getCustomModelData() == 7007447 || itemStack.getItemMeta().getCustomModelData() == 7007449)){
            player.kickPlayer("Trabaja WEON!");
            return false;
        }

        PlayerSellItem playerSellItem = new PlayerSellItem(player, itemStack, Integer.valueOf(args[0]));
        Bukkit.getServer().getPluginManager().callEvent(playerSellItem);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> subArguments = new ArrayList<>();

        switch (args.length){
            case 1:
                subArguments.add("<Precio>");
                break;
        }

        return subArguments;
    }
}



