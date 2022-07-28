package vicente.rocka.villaregion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import vicente.rocka.command.shop.CommandBuy;
import vicente.rocka.command.shop.CommandSell;
import vicente.rocka.command.shop.CommandVShop;
import vicente.rocka.command.villa.CommandVilla;
import vicente.rocka.command.vregion.CommandVregion;
import vicente.rocka.events.region.RegionEvents;
import vicente.rocka.events.shop.ShopEvents;
import vicente.rocka.region.Region;
import vicente.rocka.region.Zone;
import vicente.rocka.util.JSON.JSONFile;

import java.io.File;

public final class VillaRegion extends JavaPlugin {

    public static JSONFile REGIONS;
    public static JSONFile SHOP;
    public static JSONFile BANK;
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
        this.getCommand("vendo").setExecutor(new CommandSell());
        this.getCommand("compro").setExecutor(new CommandBuy());
        this.getCommand("vshop").setExecutor(new CommandVShop());
    }

    public void registerEvents(){
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new RegionEvents(), this);
        pluginManager.registerEvents(new ShopEvents(), this);
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
        VillaRegion.SHOP = new JSONFile("shop");
        VillaRegion.BANK = new JSONFile("bank");

        Zone.LOAD_ZONE_LIST();
    }
}
