package vicente.rocka.util.enums;

import org.bukkit.Location;
import vicente.rocka.region.Region;


public enum RegionFlag {
    Not_Burn,
    Not_Natural_Ignite,
    Not_Player_Ignite,
    Interact,
    Build,
    Break,
    Not_Explosion,
    Tpa_All,
    Tpa_Resident,
    Spawn,
    Damage,
    Keep,
    Title,
    Bucket,
    Use_Bed,
    Is_Village_Zone,

    Title_Text;

    final public Object getType(){
        return switch (this) {
            case Not_Burn, Not_Player_Ignite, Not_Natural_Ignite, Interact, Build, Break, Not_Explosion, Tpa_All, Tpa_Resident, Damage, Keep, Title, Bucket, Is_Village_Zone ->
                    true;
            case Spawn -> new Location(Region.plugin.getServer().getWorlds().get(0), 0, 0, 0);
            case Title_Text -> "";
            default -> null;
        };
    }

}
