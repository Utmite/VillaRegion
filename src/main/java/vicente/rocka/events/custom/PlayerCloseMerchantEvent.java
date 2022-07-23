package vicente.rocka.events.custom;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Merchant;

public class PlayerCloseMerchantEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    final private Merchant merchant;

    public PlayerCloseMerchantEvent(Merchant merchant){
        this.merchant =  merchant;
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
