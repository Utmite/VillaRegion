package vicente.rocka.util.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

        final int[] finalPrice = Shop.getPrice(price);
        
        recipe = Shop.setPriceVillager(recipe, finalPrice[0], finalPrice[1]);
        
        return recipe;
    }
    
    private static int mainPrice(int price){
        return price / 64;
    }
    
    private static int secondaryPrice(int price){
        return price % 64;
    }
    
    public static MerchantRecipe setPriceVillager(MerchantRecipe recipe, int main, int secondary){
        if(main != 0){
            ItemStack notebank = new ItemStack(Material.GOLD_NUGGET, main);

            ItemMeta itemMeta = notebank.getItemMeta();
            itemMeta.setCustomModelData(7007449);
            notebank.setItemMeta(itemMeta);

            recipe.addIngredient(notebank);
        }

        ItemStack coin = new ItemStack(Material.GOLD_NUGGET, secondary);

        ItemMeta coinMeta = coin.getItemMeta();
        coinMeta.setCustomModelData(7007447);
        coin.setItemMeta(coinMeta);


        recipe.addIngredient(coin);
        
        return recipe;
    }
    
    public static int[] getPrice(int price){
        return new int[]{mainPrice(price), secondaryPrice(price)};
    }
    
    public static ItemStack getNotebank(int amount){
        ItemStack notebank = new ItemStack(Material.GOLD_NUGGET, amount);

        ItemMeta itemMeta = notebank.getItemMeta();
        itemMeta.setCustomModelData(7007449);
        notebank.setItemMeta(itemMeta);
        
        return notebank;
    }

    public static ItemStack getCoin(int amount){
        ItemStack coin = new ItemStack(Material.GOLD_NUGGET, amount);

        ItemMeta itemMeta = coin.getItemMeta();
        itemMeta.setCustomModelData(7007447);
        coin.setItemMeta(itemMeta);

        return coin;
    }
}
