package vicente.rocka.util.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONObject;
import vicente.rocka.events.shop.ShopEvents;
import vicente.rocka.util.JSON.ItemJSON;
import vicente.rocka.villaregion.VillaRegion;

import java.util.ArrayList;
import java.util.List;

public class Shop {

    public static Merchant createMerchant() {
        List<MerchantRecipe> merchantRecipes = new ArrayList<>();
        List<MerchantRecipe> aux_merchantRecipes = new ArrayList<>();

        Merchant merchant = Bukkit.createMerchant("Comerciante proletario");

        for (Object object : VillaRegion.SHOP.getJson()) {
            if (!(object instanceof JSONObject)) continue;
            JSONObject itemSell = (JSONObject) object;

            ItemStack item = ItemJSON.JSONToItem(itemSell.getJSONObject("item"));

            merchantRecipes.add(getNewRecipe(item, itemSell.getInt("price")));
            aux_merchantRecipes.add(getNewRecipe(item, itemSell.getInt("price")));

        }

        merchant.setRecipes(merchantRecipes);
        ShopEvents.global_merchants.put(merchant, aux_merchantRecipes);

        return merchant;
    }

    private static MerchantRecipe getNewRecipe(ItemStack result, int price) {

        MerchantRecipe recipe = new MerchantRecipe(result, 1);


        return intgetPrice(price, recipe);
    }

    private static MerchantRecipe intgetPrice(int price, MerchantRecipe recipe){

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
}
