package vicente.rocka.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

public class PlayerSellItem extends Event {
    private static final HandlerList handlers = new HandlerList();

    final private Player player;

    final private ItemStack itemSell;

    final private Integer pirce;
    public PlayerSellItem(Player player,  ItemStack itemSell, Integer price){
        this.player = player;
        this.itemSell = itemSell;
        this.pirce = price;

    }

    public ItemStack getItemSell(){return itemSell;}

    public Integer getPrice(){return pirce;}

    public Player getPlayer(){
        return player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
