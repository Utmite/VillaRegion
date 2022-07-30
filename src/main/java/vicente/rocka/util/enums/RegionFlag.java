package vicente.rocka.util.enums;

import org.bukkit.Location;
import vicente.rocka.region.Region;


public enum RegionFlag {
    Not_Burn(true),
    Not_Natural_Ignite(true),
    Player_Ignite(true),
    Interact(true),
    Build(true),
    Break(true),
    Not_Explosion(true),
    Tpa_All(true),
    Tpa_Resident(true),
    Spawn(new Location(Region.plugin.getServer().getWorlds().get(0), 0, 0, 0)),
    Damage(true),
    Keep(true),
    Title(true),
    Bucket(true),
    Use_Bed(true),
    Is_Village_Zone(true),
    Use_villagers(true),
    Damage_items(true),
    Title_Text("");

    private Object type;
    RegionFlag(Object type) {
        this.type = type;
    }
    final public Object getType(){
        return this.type;
    }

}
