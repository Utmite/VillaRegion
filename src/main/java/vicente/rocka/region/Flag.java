package vicente.rocka.region;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.json.JSONObject;
import vicente.rocka.util.enums.RegionFlag;

import java.util.HashMap;

public class Flag {

    final private JSONObject flag;

    public Flag(){
        this.flag = new JSONObject();
    }

    public Flag(JSONObject flag){
        this.flag = flag;
    }

    private  <T> void setFlagGod(RegionFlag regionFlag, T value){
        flag.put(String.valueOf(regionFlag), value);
    }

    public <T extends JSONObject> void setFlag(RegionFlag regionFlag, T value) {
        this.setFlagGod(regionFlag, value);
    }

    public <T extends String> void setFlag(RegionFlag regionFlag, T value){
        this.setFlagGod(regionFlag, value);
    }

    public <T extends Location> void setFlag(RegionFlag regionFlag, T value){
        JSONObject jsonObject = new JSONObject(value.serialize());
        this.setFlagGod(regionFlag, jsonObject);
    }

    public <T extends Boolean> void setFlag(RegionFlag regionFlag, T value){
        this.setFlagGod(regionFlag, value);
    }
    public void removeFlag(RegionFlag regionFlag){
        if(!this.flag.has(String.valueOf(regionFlag))) return;
        flag.remove(String.valueOf(regionFlag));
    }

    public String getFlag(RegionFlag regionFlag){
        if(!flag.has(String.valueOf(regionFlag))) return null;
        return flag.get(String.valueOf(regionFlag)).toString();
    }

    public JSONObject getAll(){
        return this.flag;
    }
}
