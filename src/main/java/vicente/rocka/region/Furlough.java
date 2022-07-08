package vicente.rocka.region;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONObject;
import vicente.rocka.util.enums.RegionFurlough;

public class Furlough {

    final private JSONObject furLough;

    public Furlough(){
        this.furLough = new JSONObject();
    }

    public Furlough(JSONObject playerFurLough){
        this.furLough = playerFurLough;
    }

    /**
     *
     * @param uuid of player
     * @param value true -> allow, false --> not allow and null --> for remove
     */
    public void setPlayerFurLough(UUID uuid, RegionFurlough regionFurlough, boolean value){
        String uuidString = uuid.toString();

        if(!furLough.has(uuidString)) furLough.put(uuidString, new JSONObject());

        JSONObject playerPerm = (JSONObject) furLough.get(uuidString);
        playerPerm.put(String.valueOf(regionFurlough), value);
        furLough.put(uuidString, playerPerm);

    }
    public boolean removePlayerFurLough(UUID uuid, RegionFurlough regionFurlough){
        if(furLough.get(uuid.toString()) == null) return false;

        JSONObject playerPerm = (JSONObject) furLough.get(uuid.toString());
        playerPerm.remove(String.valueOf(regionFurlough));
        furLough.put(uuid.toString(), playerPerm);
        return true;
    }

    public boolean removePlayerFurLough(Player player, RegionFurlough regionFurlough){
        return this.removePlayerFurLough(player.getUniqueId(), regionFurlough);
    }

    public boolean removeAllPlayerFurLough(UUID uuid){
        if(!furLough.has(uuid.toString())) return false;
        furLough.remove(uuid.toString());
        return true;
    }

    public boolean removeAllPlayerFurLough(Player player){
        return this.removeAllPlayerFurLough(player.getUniqueId());
    }

    public void setPlayerFurLough(Player player, RegionFurlough regionFurlough, boolean value) {
        this.setPlayerFurLough(player.getUniqueId(), regionFurlough, value);
    }


    public boolean getPlayerFurLough(UUID uuid, RegionFurlough regionFurlough){
        String uuidString = uuid.toString();

        if(!furLough.has(uuidString)) return false;

        JSONObject playerFurlough = this.furLough.getJSONObject(uuidString);

        if(!playerFurlough.has(regionFurlough.name())) return false;

        return (boolean) Boolean.valueOf(playerFurlough.get(regionFurlough.name()).toString());
    }

    public boolean getPlayerFurLough(Player player, RegionFurlough regionFurlough){
        return this.getPlayerFurLough(player.getUniqueId(), regionFurlough);
    }
    public JSONObject getAll(){
        return  furLough;
    }



}
