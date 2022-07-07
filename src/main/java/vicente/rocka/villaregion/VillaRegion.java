package vicente.rocka.villaregion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.json.JSONObject;
import vicente.rocka.command.villa.CommandVilla;
import vicente.rocka.command.vregion.CommandVregion;
import vicente.rocka.events.region.RegionEvents;
import vicente.rocka.region.Region;
import vicente.rocka.region.Zone;
import vicente.rocka.util.JSONFile;

import java.io.File;

public final class VillaRegion extends JavaPlugin {

    public static JSONFile REGIONS;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"[VillageRegion]"+ChatColor.AQUA+" Plugin has started");

        this.createFile();
        this.registerCommands();
        this.registerEvents();

    }
    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"[VillageRegion]"+ChatColor.AQUA+" Plugin has turned off");
    }

    public void registerCommands(){
        this.getCommand("vregion").setExecutor(new CommandVregion());
        this.getCommand("villa").setExecutor(new CommandVilla());
    }

    public void registerEvents(){
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new RegionEvents(), this);
    }

    public void createFile() {
        File config = new File(this.getDataFolder(),"config.yml");

        if(!config.exists()) {
            this.getConfig().options().copyDefaults(true);
            this.saveDefaultConfig();
        }

        JSONFile.plugin = this;
        Region.plugin = this;

        VillaRegion.REGIONS = new JSONFile("regions");


        Zone.LOAD_ZONE_LIST();
    }
}
