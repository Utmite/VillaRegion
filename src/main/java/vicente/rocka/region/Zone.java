package vicente.rocka.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import vicente.rocka.util.enums.JSONProperties;
import vicente.rocka.util.enums.RegionFlag;
import vicente.rocka.util.enums.RegionFurlough;
import vicente.rocka.util.enums.RegionProperties;
import vicente.rocka.villaregion.VillaRegion;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Zone extends RegionWithPlayer{

    private static List<Zone> ZONE_LIST = new ArrayList<>();
    private static Hashtable<Integer, List<Zone>> HASH_TABLE = new Hashtable<>();
    public static List<Zone> getZoneList() {
        return ZONE_LIST;
    }

    public static Hashtable<Integer, List<Zone>> getHashTable() {
        return Zone.HASH_TABLE;
    }
    public static int numberZoneFurloughPlayer(UUID uuid, RegionFurlough regionFurlough){
        AtomicInteger count = new AtomicInteger();
        Zone.ZONE_LIST.forEach(e -> {
            if(e.getFurlough().getPlayerFurLough(uuid, regionFurlough) == true) count.getAndIncrement();
        });
        return count.get();
    }

    public static Zone getOverlapping(Zone b){

        int index = Zone.getHash(b.getRectangle().getX());
        List<Zone> zones = Zone.HASH_TABLE.get(index);
        if(zones == null) return null;
        for(Zone zone : zones){
            if(b.intersects(zone)){
                return zone;
            }
        }
        return null;
    }

    public static List<Zone> getAllZonePlayerIsResident(UUID uuid){
        List<Zone> zones = new ArrayList<>();

        Zone.ZONE_LIST.forEach(e -> {
            if(e.getResident().contains(uuid)) zones.add(e);
        });

        return zones;
    }

    public static List<Zone> getAllZoneByFlag(RegionFlag regionFlag, String value){
        List<Zone> zones = new ArrayList<>();

        Zone.ZONE_LIST.forEach(e -> {
            if(e.getFlag().getFlag(regionFlag).equals(value)) zones.add(e);
        });

        return zones;
    }

    public static List<Zone> getAllZoneByFlag(RegionFlag regionFlag, String value, String value2){
        List<Zone> zones = new ArrayList<>();

        Zone.ZONE_LIST.forEach(e -> {
            if(e.getFlag().getFlag(regionFlag).equals(value)) zones.add(e);
            if(e.getFlag().getFlag(regionFlag).equals(value2)) zones.add(e);

        });

        return zones;
    }

    public static List<Zone> getZonePlayerFurlough(UUID uuid, RegionFurlough regionFurlough){
        List<Zone> zones = new ArrayList<>();

        Zone.ZONE_LIST.forEach(e -> {
            if(e.getFurlough().getPlayerFurLough(uuid, regionFurlough) == true) zones.add(e);
        });

        return zones;
    }

    public static List<Zone> getZonePlayerFurlough(UUID uuid, RegionFurlough[] regionFurloughs){
        List<Zone> zones = new ArrayList<>();

        Zone.ZONE_LIST.forEach(e -> {
            for(RegionFurlough regionFurlough : regionFurloughs) {
                if (e.getFurlough().getPlayerFurLough(uuid, regionFurlough) == true) zones.add(e);
            }
        });

        return zones;
    }

    public static Zone getZoneByUUID(UUID uuid){
        for(Zone zone : Zone.ZONE_LIST){
            if(zone.get_ID().equals(uuid)) return zone;
        }
        return null;
    }
    public static Zone getZoneByName(String name){
        for(Zone zone : Zone.ZONE_LIST){
            if(zone.getName().equals(name)) return zone;
        }
        return null;
    }

    public static Collection<? extends String> getAllName(){
        List<String> names = new ArrayList<>();
        for(Zone zone : ZONE_LIST){
            names.add(zone.getName());
        }
        return names;
    }
    public static void LOAD_ZONE_LIST(){
        Zone.ZONE_LIST.clear();
        Zone.HASH_TABLE.clear();

        for(Object object : VillaRegion.REGIONS.getJson()){
            if(object instanceof JSONObject){
                JSONObject jsonObject = (JSONObject) ((JSONObject) object).get(JSONProperties.properties.name());

                if(Region.plugin.getServer().getWorld(jsonObject.get(RegionProperties.world.name()).toString()) != null){
                    Zone zone = new Zone((JSONObject) object);

                    Zone.ZONE_LIST.add(zone);
                    Zone.LOAD_HASH_TABLE(zone);
                }
            }
        }
    }

    private static void LOAD_HASH_TABLE(Zone zone){
        int index = getHash(zone.getRectangle().getX());
        int index_w = getHash(zone.getRectangle().getX() + zone.getRectangle().getWidth());

        List<Zone> zones = Zone.HASH_TABLE.get(index);
        if(zones == null) zones = new ArrayList<>();

        if(index != index_w){
            List<Zone> zones_w = Zone.HASH_TABLE.get(index_w);
            if(zones_w == null) zones_w = new ArrayList<>();

            zones.add(zone);
            zones_w.add(zone);

            Zone.HASH_TABLE.put(index, zones);
            Zone.HASH_TABLE.put(index_w, zones_w);

            return;
        }

        zones.add(zone);
        Zone.HASH_TABLE.put(index, zones);
    }

    public static int getHash(double x){
        int index = ((int) (x / 383)) % 13 ;
        return index;
    }

    public static List<Zone> getZoneByCords(Location location){

        int index = Zone.getHash(location.getX());

        List<Zone> zones = new ArrayList<>();

        if(HASH_TABLE.get(index) == null) return zones;

        for(Zone zone : HASH_TABLE.get(index)){
            if(zone.contains(location)) zones.add(zone);
        }

        return zones;
    }

    public static Boolean isUsedName(String name){
        if(name.equals("all")) return true;
        for(Zone zone : ZONE_LIST) {
            if(zone.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    private Resident resident;

    public Zone(Rectangle rectangle, int down, int up, World world, String name, Flag flag, Furlough furlough, Resident resident) {
        super(rectangle, down, up, world, name, flag, furlough);
        this.resident = resident;
    }

    public Zone(JSONObject jsonObject){
        super((JSONObject) jsonObject.get(String.valueOf(JSONProperties.properties)),
                new Flag((JSONObject) jsonObject.get(String.valueOf(JSONProperties.flag))),
                new Furlough((JSONObject) jsonObject.get(String.valueOf(JSONProperties.furLough))));
        this.resident = new Resident((JSONArray) jsonObject.get(String.valueOf(JSONProperties.resident)));
    }
    public JSONObject getJSON(){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(String.valueOf(JSONProperties.properties),this.getJsonObjectProperties());
        jsonObject.put(String.valueOf(JSONProperties.furLough),this.getFurlough().getAll());
        jsonObject.put(String.valueOf(JSONProperties.flag),this.getFlag().getAll());
        jsonObject.put(String.valueOf(JSONProperties.resident), this.getResident().getResident());
        return jsonObject;
    }
    public void saveZone(){

        VillaRegion.REGIONS.getJson().clear();

        for(Zone zone : Zone.ZONE_LIST){
            VillaRegion.REGIONS.getJson().put(zone.getJSON());
        }
        if(!Zone.ZONE_LIST.contains(this)) VillaRegion.REGIONS.getJson().put(this.getJSON());

        VillaRegion.REGIONS.saveJson();
        Zone.LOAD_ZONE_LIST();
    }
    public void removeZone(){
        VillaRegion.REGIONS.getJson().clear();
        Zone.ZONE_LIST.remove(this);

        for(Zone zone : Zone.ZONE_LIST){
            VillaRegion.REGIONS.getJson().put(zone.getJSON());
        }

        VillaRegion.REGIONS.saveJson();
        Zone.LOAD_ZONE_LIST();
    }

    public void removePlayer(Player player){
        this.getFurlough().removeAllPlayerFurLough(player);
        this.getResident().remove(player);
    }
    public void removePlayer(UUID uuid){
        this.getFurlough().removeAllPlayerFurLough(uuid);

        this.getResident().remove(uuid);
    }
    public Resident getResident(){
        return this.resident;
    }



}
