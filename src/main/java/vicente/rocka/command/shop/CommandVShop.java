package vicente.rocka.command.shop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.json.JSONArray;
import org.json.JSONObject;
import vicente.rocka.util.Util;
import vicente.rocka.villaregion.VillaRegion;

import java.util.ArrayList;
import java.util.List;

public class CommandVShop implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length != 1) return false;

        String index_str = args[0];

        if(index_str.equals("all")){


            for(int i = 0; i < VillaRegion.SHOP.getJson().length(); i++){
                JSONObject jsonObject = VillaRegion.SHOP.getJson().getJSONObject(i);
                sender.sendMessage(i+ "->"+jsonObject.getJSONObject("item").getString("type"));
            }

            return true;
        }

        if(!Util.isInt(index_str)) return false;

        int index = Integer.valueOf(index_str);

        if(VillaRegion.SHOP.getJson().length() - 1 < index) return false;

        sender.sendMessage("deleting index "+index);
        VillaRegion.SHOP.getJson().remove(index);
        VillaRegion.SHOP.saveJson();

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> subCommand = new ArrayList<>();

        subCommand.add("<index>");
        subCommand.add("all");

        return subCommand;
    }
}
