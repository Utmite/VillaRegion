package vicente.rocka.command.shop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Merchant;
import vicente.rocka.util.shop.Shop;


import java.util.ArrayList;
import java.util.List;


public class CommandBuy implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        Merchant merchant = Shop.createMerchant();

        player.openMerchant(merchant, true);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return new ArrayList<>();
    }

}
