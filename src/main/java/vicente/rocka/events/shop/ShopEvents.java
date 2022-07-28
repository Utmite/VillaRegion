package vicente.rocka.events.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONObject;
import vicente.rocka.events.custom.PlayerCloseMerchantEvent;
import vicente.rocka.events.custom.PlayerSellItem;
import vicente.rocka.util.JSON.ItemJSON;
import vicente.rocka.util.shop.Shop;
import vicente.rocka.villaregion.VillaRegion;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ShopEvents implements Listener {

    public static HashMap<Merchant, List<MerchantRecipe>> global_merchants = new HashMap<>();

    private static List<Player> playerInShop = new ArrayList();
    private static HashSet<Merchant> merchantsTrading = new HashSet<>();
    private static boolean go = false;

    public static void generatedVillagerSellEvent(){
        if(global_merchants.isEmpty()) return;

        Set<Merchant> merchantSet = new HashSet<>();
        merchantSet.addAll(global_merchants.keySet());
        playerInShop.clear();

        for (Merchant merchant :  merchantSet) {
            if (!merchant.isTrading()) continue;
            if (merchant.getRecipes().isEmpty()) continue;
            if (!(merchant.getTrader() instanceof Player)) continue;

            playerInShop.add((Player) merchant.getTrader());
            AtomicInteger index = new AtomicInteger();

            global_merchants.get(merchant).forEach(auxrecipe -> {

                MerchantRecipe recipe = merchant.getRecipe(index.get());

                if (auxrecipe.getUses() != recipe.getUses() && !go) {
                    go = true;

                    VillagerSellEvent((Player) merchant.getTrader(), merchant, recipe);

                }

                index.addAndGet(1);

            });
        }

        if(go){
            go = false;
            global_merchants.clear();

            for(Player p : playerInShop){
                p.closeInventory();
                p.openMerchant(Shop.createMerchant(), true);
            }

            playerInShop.clear();
        }


    }

    public static void generatedVillagerCloseEvent(){
        Set<Merchant> merchantSet = new HashSet<>();
        merchantSet.addAll(global_merchants.keySet());

        for (Merchant merchant :  merchantSet) {
            if (!merchant.isTrading() && merchantsTrading.contains(merchant)) {
                PlayerCloseMerchantEvent playerCloseMerchantEvent = new PlayerCloseMerchantEvent(merchant);
                Bukkit.getServer().getPluginManager().callEvent(playerCloseMerchantEvent);
                merchantsTrading.remove(merchant);

            }
            if (merchant.isTrading()) {
                merchantsTrading.add(merchant);
            }
        }

    }

    private static void VillagerSellEvent(Player player, Merchant merchant, MerchantRecipe merchantRecipe){

        if(!go) return;

        ItemStack itemSell = merchantRecipe.getResult();

        if(!itemExitsInJSON(itemSell)){
            player.getInventory().remove(itemSell);
            return;
        }

        JSONObject itemJSON = removeItem(itemSell);

        if(itemJSON != null) giveMoney(itemJSON);
    }

    @EventHandler
    public void PlayerCloseMerchantEvent(PlayerCloseMerchantEvent playerCloseMerchantEvent){
        global_merchants.remove(playerCloseMerchantEvent.getMerchant());
    }

    @EventHandler
    public void PlayerSellItem(PlayerSellItem playerSellItem){
        Player player = playerSellItem.getPlayer();

        ItemStack itemSell = playerSellItem.getItemSell();
        Integer price = playerSellItem.getPrice();

        player.getInventory().setItemInMainHand(null);

        addItemShop(player, itemSell, price);

        reloadShop();
    }

    private void reloadShop(){

        if(global_merchants.isEmpty()) return;
        List<Player> playersInShop = new ArrayList<>();

        for (Merchant merchant : global_merchants.keySet()) {
            if(!merchant.isTrading()) continue;
            if(!(merchant.getTrader() instanceof Player)) continue;

            playersInShop.add((Player) merchant.getTrader());
        }

        global_merchants.clear();

        for(Player p : playersInShop){
            p.closeInventory();
            Shop.createMerchant();
            p.openMerchant(Shop.createMerchant(), true);

        }

        playersInShop.clear();
    }

    private void addItemShop(Player player, ItemStack itemSell, Integer price){
        JSONObject jsonObject = new JSONObject();

        JSONObject itemJSON = ItemJSON.itemStackToJSON(itemSell);

        jsonObject.put("item", itemJSON);
        jsonObject.put("price", price);
        jsonObject.put("player_UUID", player.getUniqueId().toString());

        VillaRegion.SHOP.getJson().put(jsonObject);

        VillaRegion.SHOP.saveJson();
    }

    private static boolean itemExitsInJSON(ItemStack itemSell){

        JSONObject itemJSON = ItemJSON.itemStackToJSON(itemSell);

        for(int i = 0; i < VillaRegion.SHOP.getJson().length(); i++){
            JSONObject jsonObject = ((JSONObject) VillaRegion.SHOP.getJson().get(i)).getJSONObject("item");

            if(jsonObject.similar(itemJSON)){
                return true;
            }
        }

        return false;
    }

    private static JSONObject removeItem(ItemStack itemSell) {
        JSONObject itemJSON = ItemJSON.itemStackToJSON(itemSell);

        for (int i = 0; i < VillaRegion.SHOP.getJson().length(); i++) {
            JSONObject jsonObject = ((JSONObject) VillaRegion.SHOP.getJson().get(i)).getJSONObject("item");

            if (itemJSON.similar(jsonObject)){
                jsonObject = VillaRegion.SHOP.getJson().getJSONObject(i);
                VillaRegion.SHOP.getJson().remove(i);
                VillaRegion.SHOP.saveJson();
                return jsonObject;
            }
        }
        return null;
    }

    private static void giveMoney(JSONObject itemJSON){
        if(Bukkit.getPlayer(UUID.fromString(itemJSON.getString("player_UUID"))) == null){
            JSONObject bank = new JSONObject();
            bank.put("UUID", itemJSON.getString("player_UUID"));
            bank.put("price", itemJSON.getInt("price"));
            VillaRegion.BANK.getJson().put(bank);
            VillaRegion.BANK.saveJson();
        }else {

            Player player = Bukkit.getPlayer(UUID.fromString(itemJSON.getString("player_UUID")));

            int price = itemJSON.getInt("price");

            int mainPrice = price / 64;
            int r = price % 64;

            if(mainPrice != 0) {
                ItemStack notebank = new ItemStack(Material.GOLD_NUGGET, mainPrice);

                ItemMeta itemMeta = notebank.getItemMeta();
                itemMeta.setCustomModelData(7007449);
                notebank.setItemMeta(itemMeta);

                player.getWorld().dropItem(player.getLocation(),notebank);
            }

            ItemStack coin = new ItemStack(Material.GOLD_NUGGET, r);

            ItemMeta coinMeta = coin.getItemMeta();
            coinMeta.setCustomModelData(7007447);
            coin.setItemMeta(coinMeta);


            player.getWorld().dropItem(player.getLocation(),coin);
        }

        VillaRegion.SHOP.saveJson();
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent playerJoinEvent){
        Player player = playerJoinEvent.getPlayer();
        int index = 0;
        JSONArray jsonArray = new JSONArray().putAll(VillaRegion.BANK.getJson());

        for(Object obj : jsonArray){
            if(!(obj instanceof JSONObject)) continue;

            JSONObject itemSell = (JSONObject) obj;
            UUID playerUUID = UUID.fromString(itemSell.getString("UUID"));

            if(!player.getUniqueId().equals(playerUUID)) continue;

            VillaRegion.BANK.getJson().remove(index);
            VillaRegion.BANK.saveJson();

            int price = itemSell.getInt("price");

            int mainPrice = price / 64;
            int r = price % 64;

            if(mainPrice != 0) {
                ItemStack notebank = new ItemStack(Material.GOLD_NUGGET, mainPrice);

                ItemMeta itemMeta = notebank.getItemMeta();
                itemMeta.setCustomModelData(7007449);
                notebank.setItemMeta(itemMeta);

                player.getWorld().dropItem(player.getLocation(),notebank);
            }

            ItemStack coin = new ItemStack(Material.GOLD_NUGGET, r);

            ItemMeta coinMeta = coin.getItemMeta();
            coinMeta.setCustomModelData(7007447);
            coin.setItemMeta(coinMeta);


            player.getWorld().dropItem(player.getLocation(),coin);

            index+=1;
        }
    }


}