package vicente.rocka.events.custom;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import vicente.rocka.region.Zone;


public final class PlayerEnterZoneEvent extends Event{
    private static final HandlerList handlers = new HandlerList();

    final private Zone zone;
    final private Player player;
    final private Location location;


    public PlayerEnterZoneEvent(Zone region, Player player) {
        this.zone = region;
        this.player = player;
        this.location = player.getLocation();
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Zone getZone() {
        return zone;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

}
