package vicente.rocka.events.shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONObject;
import vicente.rocka.command.shop.CommandBuy;
import vicente.rocka.events.custom.PlayerCloseMerchantEvent;
import vicente.rocka.events.custom.PlayerSellItem;
import vicente.rocka.events.custom.VillagerSellEvent;
import vicente.rocka.villaregion.VillaRegion;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ShopEvents implements Listener {

    private static HashMap<Merchant, List<MerchantRecipe>> global_merchants = new HashMap<>();

    private static List<Player> playerInShop = new ArrayList();
    private static HashSet<Merchant> merchantsTrading = new HashSet<>();
    private static boolean go = false;

    public static HashMap<Merchant, List<MerchantRecipe>> getGlobalMerchants(){
        return global_merchants;
    }
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
                        VillagerSellEvent villagerSellEvent = new VillagerSellEvent((Player) merchant.getTrader(), merchant, recipe);
                        Bukkit.getServer().getPluginManager().callEvent(villagerSellEvent);
                    }

                    index.addAndGet(1);

                });
            }

            if(go){
                go = false;
                global_merchants.clear();

                for(Player p : playerInShop){
                    p.closeInventory();
                    p.chat("/compro");
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


    @EventHandler
    public void VillagerSellEvent(VillagerSellEvent villagerSellEvent){

        ItemStack itemSell = villagerSellEvent.getMerchantRecipe().getResult();

        if(!itemExitsInJSON(itemSell)){
            villagerSellEvent.getPlayer().getInventory().remove(itemSell);
        }

        JSONObject itemJSON = this.removeItem(itemSell);

        giveMoney(itemJSON);

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
            p.chat("/compro");
        }

        playersInShop.clear();
    }

    private void addItemShop(Player player, ItemStack itemSell, Integer price){
        JSONObject jsonObject = new JSONObject();

        JSONObject itemJSON = new JSONObject(itemSell.serialize());
        itemJSON.put("meta", itemSell.getItemMeta().serialize());

        jsonObject.put("item", itemJSON);
        jsonObject.put("price", price);
        jsonObject.put("player_UUID", player.getUniqueId().toString());

        VillaRegion.SHOP.getJson().put(jsonObject);

        VillaRegion.SHOP.saveJson();
    }

    private static boolean equals(JSONObject o, JSONObject b){
        JSONObject obj = (JSONObject) o;


        if(!obj.keySet().equals(b.keySet())) return false;

        for(String key : obj.keySet()){

            if(!obj.get(key).toString().equals(b.get(key).toString())) return false;
        }

        return true;
    }

    private boolean itemExitsInJSON(ItemStack itemSell){

        JSONObject itemJSON = new JSONObject(itemSell.serialize());
        itemJSON.put("meta", itemSell.getItemMeta().serialize());

        for(int i = 0; i < VillaRegion.SHOP.getJson().length(); i++){
            JSONObject jsonObject = ((JSONObject) VillaRegion.SHOP.getJson().get(i)).getJSONObject("item");

            if(equals(jsonObject, itemJSON)) return true;
        }

        return false;
    }

    private JSONObject removeItem(ItemStack itemSell) {
        JSONObject itemJSON = new JSONObject(itemSell.serialize());
        itemJSON.put("meta", itemSell.getItemMeta().serialize());

        for (int i = 0; i < VillaRegion.SHOP.getJson().length(); i++) {
            JSONObject jsonObject = ((JSONObject) VillaRegion.SHOP.getJson().get(i)).getJSONObject("item");

            if (equals(jsonObject, itemJSON)){
                jsonObject = VillaRegion.SHOP.getJson().getJSONObject(i);
                VillaRegion.SHOP.getJson().remove(i);
                VillaRegion.SHOP.saveJson();
                return jsonObject;
            }
        }
        return null;
    }

    private void giveMoney(JSONObject itemJSON){
        if(Bukkit.getPlayer(UUID.fromString(itemJSON.getString("player_UUID"))) == null){
            JSONObject bank = new JSONObject();
            bank.put("UUID", itemJSON.getString("player_UUID"));
            bank.put("price", itemJSON.getInt("price"));
            VillaRegion.BANK.getJson().put(bank);
            VillaRegion.BANK.saveJson();
        }else {
            ItemStack price_value = new ItemStack(Material.GOLD_NUGGET, itemJSON.getInt("price"));

            ItemMeta itemMeta = price_value.getItemMeta();
            itemMeta.setCustomModelData(7007447);
            price_value.setItemMeta(itemMeta);

            Player player = Bukkit.getPlayer(UUID.fromString(itemJSON.getString("player_UUID")));

            player.getWorld().dropItem(player.getLocation(),price_value);


        }

        VillaRegion.SHOP.saveJson();
    }


}