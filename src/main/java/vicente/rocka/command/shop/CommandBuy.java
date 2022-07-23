package vicente.rocka.command.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONObject;
import vicente.rocka.events.shop.ShopEvents;
import vicente.rocka.villaregion.VillaRegion;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CommandBuy implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return false;

        List<MerchantRecipe> merchantRecipes = new ArrayList<>();
        List<MerchantRecipe> aux_merchantRecipes = new ArrayList<>();

        Merchant merchant = Bukkit.createMerchant("Comerciante proletario");
        Player player = (Player) sender;

        merchantRecipes.add(getBankNote());
        aux_merchantRecipes.add(getBankNote());

        for (Object object : VillaRegion.SHOP.getJson()) {
            if (!(object instanceof JSONObject)) continue;

            JSONObject jsonObject_price_item = (JSONObject) object;

            JSONObject jsonObject = jsonObject_price_item.getJSONObject("item");


            Map<String, Object> map_item = jsonObject.toMap();
            Map<String, Object> map_meta_item = jsonObject.getJSONObject("meta").toMap();

            map_meta_item.put("==", "ItemMeta");

            ItemMeta meta = (ItemMeta) ConfigurationSerialization.deserializeObject(map_meta_item);

            ItemStack item = ItemStack.deserialize(map_item);
            item.setItemMeta(meta);

            merchantRecipes.add(getNewRecipe(item, jsonObject_price_item.getInt("price")));
            aux_merchantRecipes.add(getNewRecipe(item, jsonObject_price_item.getInt("price")));
        }


        merchant.setRecipes(merchantRecipes);
        ShopEvents.global_merchants.put(merchant, aux_merchantRecipes);

        player.openMerchant(merchant, true);

        return true;
    }

    private MerchantRecipe getNewRecipe(ItemStack result, int price) {

        MerchantRecipe recipe = new MerchantRecipe(result, 1);


        return intgetPrice(price, recipe);
    }

    private MerchantRecipe getBankNote(){
        ItemStack bankNote = new ItemStack(Material.GOLD_NUGGET, 1);

        ItemMeta bankNoteMeta = bankNote.getItemMeta();
        bankNoteMeta.setCustomModelData(7007449);
        bankNote.setItemMeta(bankNoteMeta);

        MerchantRecipe bankNoteRecipe = new MerchantRecipe(bankNote, 99);

        ItemStack coin = new ItemStack(Material.GOLD_NUGGET, 64);

        ItemMeta coinMeta = coin.getItemMeta();
        coinMeta.setCustomModelData(7007447);
        coin.setItemMeta(coinMeta);

        bankNoteRecipe.addIngredient(coin);

        return bankNoteRecipe;
    }
    private MerchantRecipe intgetPrice(int price, MerchantRecipe recipe){

            int mainPrice = price / 64;
            int r = price % 64;

            if(mainPrice != 0) {
                ItemStack notebank = new ItemStack(Material.GOLD_NUGGET, mainPrice);

                ItemMeta itemMeta = notebank.getItemMeta();
                itemMeta.setCustomModelData(7007449);
                notebank.setItemMeta(itemMeta);

                recipe.addIngredient(notebank);
            }
            ItemStack coin = new ItemStack(Material.GOLD_NUGGET, r);

            ItemMeta coinMeta = coin.getItemMeta();
            coinMeta.setCustomModelData(7007447);
            coin.setItemMeta(coinMeta);


            recipe.addIngredient(coin);

            return recipe;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return new ArrayList<>();
    }

}
