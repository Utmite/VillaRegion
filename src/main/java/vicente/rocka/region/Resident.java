package vicente.rocka.region;

import java.util.*;

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

public class Resident {

    final private JSONArray list;
    final private HashSet<UUID> hashSet;
    public Resident() {
        this.list = new JSONArray();
        this.hashSet = new HashSet<>();
    }

    public Resident(JSONArray jsonArray){
        this.list = jsonArray;


        HashSet<UUID> tmp = new HashSet<>();

        for(Object s : list){
            tmp.add(UUID.fromString(s.toString()));
        }

        this.hashSet = tmp;

    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public void add(Player player) {
         this.hashSet.add(player.getUniqueId());
         this.list.clear();
         this.list.putAll(this.hashSet);
    }

    public void add(UUID uuid){
        this.hashSet.add(uuid);
        this.list.clear();
        this.list.putAll(this.hashSet);
    }

    protected void remove(Player player) {
        this.hashSet.remove(player.getUniqueId());
        this.list.clear();
        this.list.putAll(this.hashSet);

    }

    protected void remove(UUID uuid){
        this.hashSet.remove(uuid);
        this.list.clear();
        this.list.putAll(this.hashSet);
    }

    public boolean contains(UUID uuid){
        return this.hashSet.contains(uuid);
    }

    public Set<UUID> getAll(){
        return this.hashSet;
    }

    public JSONArray getResident(){
        return this.list;
    }




}
