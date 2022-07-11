package vicente.rocka.region;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import vicente.rocka.util.enums.RegionFlag;

import java.awt.*;

public class Villa extends Zone{

    public Villa(Rectangle rectangle, int down, int up, World world, String name, Furlough furlough, Resident resident) {
        super(rectangle, down, up, world, name, new Flag(), furlough, resident);
        this.getFlag().setFlag(RegionFlag.Is_Village_Zone, true);

        ConfigurationSection default_flags_villa = Region.plugin.getConfig().getConfigurationSection("villa_specification.default_flags_villa");

        for(String key : default_flags_villa.getKeys(false)){
            String value = Region.plugin.getConfig().getString("villa_specification.default_flags_villa."+key);
            try{
                RegionFlag regionFlag = RegionFlag.valueOf(key);
                this.getFlag().setFlag(regionFlag, value);
            }catch (IllegalArgumentException illegalArgumentException){
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"[VillageRegion] This flag in default_flags_villa (config) not exits");
            }
        }
    }
}
