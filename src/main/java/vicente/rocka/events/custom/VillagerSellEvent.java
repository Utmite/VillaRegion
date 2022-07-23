package vicente.rocka.events.custom;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

public class VillagerSellEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    final private Merchant merchant;
    final private Player player;
    final private MerchantRecipe merchantRecipe;

    public VillagerSellEvent(Player player, Merchant merchant, MerchantRecipe merchantRecipe){
        this.player = player;
        this.merchant =  merchant;
        this.merchantRecipe = merchantRecipe;

    }

    public Merchant getMerchant() {
        return merchant;
    }

    public Player getPlayer(){
        return player;
    }

    public MerchantRecipe getMerchantRecipe(){
        return merchantRecipe;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
