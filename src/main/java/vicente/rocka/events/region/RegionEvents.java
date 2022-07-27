package vicente.rocka.events.region;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import vicente.rocka.events.custom.PlayerEnterZoneEvent;
import vicente.rocka.events.custom.PlayerExitZoneEvent;
import vicente.rocka.region.Flag;
import vicente.rocka.region.Region;
import vicente.rocka.region.Zone;
import vicente.rocka.util.enums.RegionFlag;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RegionEvents implements Listener {

    private static HashMap<Player, List<Zone>> LAST_ZONES = new HashMap<>();
    private int tick = 0;

    public RegionEvents(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Region.plugin, () -> {
                tick+=5;
                if(tick % 21 == 0) tick = 0;
        }, 0, 5);
    }

    @EventHandler
    public void PlayerLoginEvent(PlayerLoginEvent playerLoginEvent){
        Player player = playerLoginEvent.getPlayer();
        List<Zone> now_zones = Zone.getZoneByCords(player.getLocation());

        LAST_ZONES.put(player, now_zones);

    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent playerMoveEvent) {

        if (tick % 20 == 0) {

            Player player = playerMoveEvent.getPlayer();
            Location location = player.getLocation();

            List<Zone> now_zones = Zone.getZoneByCords(location);
            List<Zone> last_zones = LAST_ZONES.get(player);

            if (last_zones == null) {

                if (now_zones.size() > 0) {
                    PlayerEnterZoneEvent playerEnterZoneEvent = new PlayerEnterZoneEvent(now_zones.get(0), player);
                    Bukkit.getServer().getPluginManager().callEvent(playerEnterZoneEvent);
                }

                LAST_ZONES.put(player, now_zones);
                return;
            }

            if (now_zones.size() == last_zones.size()) {
                LAST_ZONES.put(player, now_zones);
                return;
            }

            if (now_zones.size() > last_zones.size()) {

                now_zones.removeAll(last_zones);

                for (Zone zone : now_zones) {
                    PlayerEnterZoneEvent playerEnterZoneEvent = new PlayerEnterZoneEvent(zone, player);
                    Bukkit.getServer().getPluginManager().callEvent(playerEnterZoneEvent);
                }

                now_zones.addAll(last_zones);
                LAST_ZONES.put(player, now_zones);
                return;
            }

            if (now_zones.size() < last_zones.size()) {
                last_zones.removeAll(now_zones);

                for (Zone zone : last_zones) {
                    PlayerExitZoneEvent playerExitZoneEvent = new PlayerExitZoneEvent(zone, player);
                    Bukkit.getServer().getPluginManager().callEvent(playerExitZoneEvent);
                }

                LAST_ZONES.put(player, now_zones);
                return;
            }

        }

    }

    @EventHandler
    public void PlayerEnterZoneEvent(PlayerEnterZoneEvent playerEnterZoneEvent){

        String title_to_enter = Region.plugin.getConfig().getString("villa_specification.title_to_enter");

        Player player = playerEnterZoneEvent.getPlayer();
        Zone zone = playerEnterZoneEvent.getZone();

        if(zone.getFlag().getFlag(RegionFlag.Title).equals("null")) return;
        if(zone.getFlag().getFlag(RegionFlag.Title).equals("true")){
            Flag flag = zone.getFlag();
            if(!flag.getFlag(RegionFlag.Title_Text).equals("null")){
                player.sendTitle(ChatColor.translateAlternateColorCodes(
                                '&', flag.getFlag(RegionFlag.Title_Text)),
                        ChatColor.translateAlternateColorCodes('&', zone.getName())
                        , 10, 30, 10);
                return;
            }
            player.sendTitle(ChatColor.translateAlternateColorCodes(
                            '&', title_to_enter),
                    ChatColor.translateAlternateColorCodes('&', zone.getName())
                    , 10, 30,10 );
        }
    }

    @EventHandler
    public void PlayerExitZoneEvent(PlayerExitZoneEvent playerExitZoneEvent){
        String title_to_exit = Region.plugin.getConfig().getString("villa_specification.title_to_exit");


        Player player = playerExitZoneEvent.getPlayer();
        Zone zone = playerExitZoneEvent.getZone();

        if(zone.getFlag().getFlag(RegionFlag.Title).equals("null")) return;
        if(zone.getFlag().getFlag(RegionFlag.Title).equals("true")) {
            Flag flag = zone.getFlag();

            if(!flag.getFlag(RegionFlag.Title_Text).equals("null")){
                player.sendTitle(ChatColor.translateAlternateColorCodes(
                                '&', flag.getFlag(RegionFlag.Title_Text)),
                        ChatColor.translateAlternateColorCodes('&', zone.getName())
                        , 10, 30, 10);
                return;
            }
            player.sendTitle(ChatColor.translateAlternateColorCodes(
                            '&', title_to_exit),
                    ChatColor.translateAlternateColorCodes('&', zone.getName())
                    , 10, 30, 10);
        }
    }

    @EventHandler
    public void AsyncPlayerChatEvent(AsyncPlayerChatEvent asyncPlayerChatEvent){
        Player player = asyncPlayerChatEvent.getPlayer();
        String message = asyncPlayerChatEvent.getMessage();
        List<Zone> zones = LAST_ZONES.get(player);

        if(zones == null) return;

        Set<Player> recipients = asyncPlayerChatEvent.getRecipients();

        if(zones.isEmpty()) return;
        if(!message.startsWith("w")) return;

        recipients.clear();
        Zone zone = zones.get(0);

        asyncPlayerChatEvent.setFormat(ChatColor.translateAlternateColorCodes('&',"&b["+ zone.getName()+"&b]"+ " &7<"+player.getName()+"> &f"+message));

        LAST_ZONES.forEach((target, target_last_zone) -> {
            if(target_last_zone.contains(zone)) recipients.add(target);
        });

    }

    public boolean getCancelled(Player player, Location location, RegionFlag regionFlag){

        if(player.isOp()) return false;

        List<Zone> zones = Zone.getZoneByCords(location);

        if(zones.isEmpty()) return false;

        Zone zone = zones.get(zones.size() - 1);
        if(!checkAllowProtection() && Boolean.parseBoolean(zone.getFlag().getFlag(RegionFlag.Is_Village_Zone))) return false;

        String value = zone.getFlag().getFlag(regionFlag);

        if (value.equals("true")) return false;
        if (value.equals("false")) {
            sendFlagError(player, regionFlag);
            return true;
        }

        if(!zone.getResident().contains(player.getUniqueId())) {
            sendFlagError(player, regionFlag);
            return true;
        }

        return false;
    }

    private void sendFlagError(Player player, RegionFlag regionFlag){

        String error = Region.plugin.getConfig().getString("flags.errors."+regionFlag.name());

        if(error == null) return;

        BaseComponent[] component = new ComponentBuilder(ChatColor.RED+error).create();
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    private boolean checkAllowProtection(){
        Boolean value = Region.plugin.getConfig().getBoolean("villa_specification.protection");
        if(value == null) return true;
        if(!value) return false;
        return true;
    }
    public boolean getGameRule(Location location, RegionFlag regionFlag){


        List<Zone> zones = Zone.getZoneByCords(location);

        if(zones.isEmpty()) return false;

        Zone zone = zones.get(zones.size() - 1);

        if(!checkAllowProtection() && Boolean.parseBoolean(zone.getFlag().getFlag(RegionFlag.Is_Village_Zone))) return false;

        String value = zone.getFlag().getFlag(regionFlag);

        if(value.equals("true")) return true;

        return false;
    }


    @EventHandler
    public void PlayerBedEnterEvent(PlayerBedEnterEvent playerBedEnterEvent){
        playerBedEnterEvent.setCancelled(getCancelled(playerBedEnterEvent.getPlayer(), playerBedEnterEvent.getBed().getLocation(), RegionFlag.Use_Bed));
    }

    @EventHandler
    public void PlayerBucketEntityEvent(PlayerBucketEntityEvent playerBucketEntityEvent){
        playerBucketEntityEvent.setCancelled(getCancelled(playerBucketEntityEvent.getPlayer(), playerBucketEntityEvent.getEntity().getLocation(), RegionFlag.Interact));
    }

    @EventHandler
    public void PlayerBucketEmptyEvent(PlayerBucketEmptyEvent playerBucketEmptyEvent){
        //buket: false --> true
        playerBucketEmptyEvent.setCancelled(getCancelled(playerBucketEmptyEvent.getPlayer(), playerBucketEmptyEvent.getBlock().getLocation(), RegionFlag.Bucket));
    }

    @EventHandler
    public void PlayerBucketFillEvent(PlayerBucketFillEvent playerBucketFillEvent){
        playerBucketFillEvent.setCancelled(getCancelled(playerBucketFillEvent.getPlayer(), playerBucketFillEvent.getBlock().getLocation(), RegionFlag.Bucket));
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent playerInteractEvent){
        if(playerInteractEvent.getClickedBlock() == null) return;

        playerInteractEvent.setCancelled(getCancelled(
                playerInteractEvent.getPlayer(),
                playerInteractEvent.getClickedBlock().getLocation(),
                RegionFlag.Interact
        ));
    }

    private void entityDamageByPlayer(EntityDamageByEntityEvent entityDamageByEntityEvent){
        Player player = (Player) entityDamageByEntityEvent.getDamager();
        Location location = entityDamageByEntityEvent.getEntity().getLocation();

        entityDamageByEntityEvent.setCancelled(getCancelled(
                player,
                location,
                RegionFlag.Damage
        ));
    }

    private void entityDamageByProjectile(EntityDamageByEntityEvent entityDamageByEntityEvent){
            Projectile projectile = (Projectile) entityDamageByEntityEvent.getDamager();

            if(!(projectile.getShooter() instanceof Player)) return;

            Player player = (Player) projectile.getShooter();
            Location location = entityDamageByEntityEvent.getEntity().getLocation();

            entityDamageByEntityEvent.setCancelled(getCancelled(
                    player,
                    location,
                    RegionFlag.Damage
            ));
    }

    @EventHandler
    public void EntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){
        if(!(entityDamageByEntityEvent.getDamager() instanceof Player || entityDamageByEntityEvent.getDamager() instanceof Projectile)) return;

        if(entityDamageByEntityEvent.getDamager() instanceof Player){
            entityDamageByPlayer(entityDamageByEntityEvent);
            return;
        }

        if(entityDamageByEntityEvent.getDamager() instanceof Projectile){
            entityDamageByProjectile(entityDamageByEntityEvent);
            return;
        }

    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent playerDeathEvent){

        boolean value = getGameRule(
                playerDeathEvent.getEntity().getLocation(),
                RegionFlag.Keep
        );

        playerDeathEvent.setKeepInventory(value);
        if(value){
            playerDeathEvent.getKeepLevel();
            playerDeathEvent.getDrops().clear();
        }
    }

    @EventHandler
    public void EntityExplodeEvent(EntityExplodeEvent entityExplodeEvent){
        List<Zone> zones = Zone.getZoneByCords(entityExplodeEvent.getLocation());

        if(zones.isEmpty()) return;

        Zone zone = zones.get(zones.size() - 1);
        String value = zone.getFlag().getFlag(RegionFlag.Not_Explosion);

        if(value != null){
            if(Boolean.parseBoolean(value)) entityExplodeEvent.blockList().clear();
        }
    }

    @EventHandler
    public void PlayerPortalEvent(PlayerPortalEvent playerPortalEvent){

        Player player = playerPortalEvent.getPlayer();

        boolean to = getCancelled(
                player, playerPortalEvent.getTo(), RegionFlag.Interact
        );

        playerPortalEvent.setCanCreatePortal(!to);
        playerPortalEvent.setCancelled(to);
    }


    @EventHandler
    public void BlockBurnEvent(BlockBurnEvent blockBurnEvent){
        //Not_burn --> true el evento no debe cancelarse
        //
        blockBurnEvent.setCancelled(getGameRule(blockBurnEvent.getBlock().getLocation(), RegionFlag.Not_Burn));
    }

    @EventHandler
    public void BlockIgniteEvent(BlockIgniteEvent blockIgniteEvent){
        //This is a game rule
        if(blockIgniteEvent.getPlayer() == null) blockIgniteEvent.setCancelled(getGameRule(blockIgniteEvent.getBlock().getLocation(), RegionFlag.Not_Natural_Ignite));

        //This is a rule for Residents
        if(blockIgniteEvent.getPlayer() instanceof Player) blockIgniteEvent.setCancelled(getCancelled(
                blockIgniteEvent.getPlayer(),
                blockIgniteEvent.getBlock().getLocation(),
                RegionFlag.Player_Ignite
        ));
    }

    @EventHandler
    public void BlockCanBuildEvent(BlockCanBuildEvent blockCanBuildEvent){
        // not build: true --> false

        boolean value = !getCancelled(
                blockCanBuildEvent.getPlayer(),
                blockCanBuildEvent.getBlock().getLocation(),
                RegionFlag.Build
        );
        if(!value) blockCanBuildEvent.setBuildable(false);
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent blockBreakEvent){
        blockBreakEvent.setCancelled(
                getCancelled(blockBreakEvent.getPlayer(), blockBreakEvent.getBlock().getLocation(), RegionFlag.Break)
        );
    }

    @EventHandler
    public void InventoryOpenEvent(InventoryOpenEvent inventoryOpenEvent){
        inventoryOpenEvent.setCancelled(
                getCancelled((Player) inventoryOpenEvent.getPlayer(), inventoryOpenEvent.getInventory().getLocation(), RegionFlag.Interact)
        );
    }

    @EventHandler
    public void PlayerCommandSendEvent(PlayerCommandSendEvent playerCommandSendEvent){
        if(playerCommandSendEvent.getPlayer().isOp()) return;
        if(Region.plugin.getConfig().getBoolean(
                "tab.allow_tab_commands"
        )) return;

        playerCommandSendEvent.getCommands().clear();
        playerCommandSendEvent.getCommands().addAll(
                Region.plugin.getConfig().getStringList("tab.allow_commands")
        );
    }

}
