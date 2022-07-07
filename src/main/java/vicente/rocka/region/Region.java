package vicente.rocka.region;

import java.awt.Rectangle;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import org.json.JSONObject;

import vicente.rocka.util.enums.JSONProperties;
import vicente.rocka.util.enums.RegionProperties;
import vicente.rocka.villaregion.VillaRegion;
public class Region {

    public static VillaRegion plugin;
    private int down, up;
    private World world;
    private Rectangle rectangle;
    private UUID _ID;
    private String name;

    private JSONObject jsonObject;

    public Region(Rectangle rectangle, int down, int up, World world, String name){
        this.jsonObject = new JSONObject();
        this.setRectangle(rectangle);
        this.setDown(down);
        this.setUp(up);
        this.setWorld(world);
        this.set_ID(UUID.randomUUID());
        this.setName(name);
    }

    public Region(JSONObject properties) {
        this.jsonObject = new JSONObject();
        this.setRectangle(new Rectangle(
                Double.valueOf(properties.get(RegionProperties.x.name()).toString()).intValue(),
                Double.valueOf(properties.get(RegionProperties.z.name()).toString()).intValue(),
                Double.valueOf(properties.get(RegionProperties.width.name()).toString()).intValue(),
                Double.valueOf(properties.get(RegionProperties.height.name()).toString()).intValue())
        );
        this.setDown(Integer.valueOf(properties.get(RegionProperties.down.name()).toString()));
        this.setUp(Integer.valueOf(properties.get(RegionProperties.up.name()).toString()));
        this.setWorld(plugin.getServer().getWorld(properties.get(RegionProperties.world.name()).toString()));
        this.set_ID(UUID.fromString(properties.get("_ID").toString()));
        this.setName(properties.get(RegionProperties.name.name()).toString());
    }


    public boolean intersects(Region b) {
        if(this.getRectangle().intersects(b.getRectangle()) && this.getWorld().equals(b.getWorld()) && (Region.between(b.getDown(),this.getUp(), this.getDown()) || Region.between(b.getUp(),this.getUp(), this.getDown()))) return true;
        return false;
    }

    private static boolean between(int variable, int minValueInclusive, int maxValueInclusive) {
        return (variable >= minValueInclusive && variable <= maxValueInclusive) || (variable >= maxValueInclusive && variable <= minValueInclusive);
    }

    public boolean contains(int x, int y, int z,World world){
        if(this.getRectangle().contains(x,z) && (y >= down && up >= y) && this.getWorld().equals(world)) return true;
        return false;
    }

    public boolean contains(Location location){
        return this.contains((int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getWorld());
    }

    public int getUp() {
        return up;
    }
    public void setUp(int up) {
        this.jsonObject.put(RegionProperties.up.name(), up);
        this.up = up;
    }
    public int getDown() {
        return down;
    }
    public void setDown(int down) {
        this.down = down;
        this.jsonObject.put(RegionProperties.down.name(), down);
    }
    public World getWorld() {
        return world;
    }
    public void setWorld(World world) {
        this.world = world;
        this.jsonObject.put(RegionProperties.world.name(),this.getWorld().getName());
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
        this.jsonObject.put(RegionProperties.x.name(), this.getRectangle().getX());
        this.jsonObject.put(RegionProperties.z.name(), this.getRectangle().getY());
        this.jsonObject.put(RegionProperties.width.name(), this.getRectangle().getWidth());
        this.jsonObject.put(RegionProperties.height.name(), this.getRectangle().getHeight());
    }

    public UUID get_ID() {
        return _ID;
    }

    public void set_ID(UUID _ID) {
        this._ID = _ID;
        this.jsonObject.put("_ID", this.get_ID().toString());
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.jsonObject.put(RegionProperties.name.name(), name);
    }

    public JSONObject getJsonObjectProperties(){
        return this.jsonObject;
    }

    public void setJsonObjectProperties(JSONObject jsonObject){
        this.jsonObject = jsonObject;
        this.setName((String) this.jsonObject.get(RegionProperties.name.name()));
        this.set_ID(UUID.fromString((String) this.jsonObject.get("_ID")));
        this.setDown((Integer) this.jsonObject.get(RegionProperties.down.name()));
        this.setUp((Integer) this.jsonObject.get(RegionProperties.up.name()));
        this.setWorld(Region.plugin.getServer().getWorld((String) this.jsonObject.get(RegionProperties.world.name())));
        this.setRectangle(
                new Rectangle(
                        Double.valueOf(this.jsonObject.get(RegionProperties.x.name()).toString()).intValue(),
                        Double.valueOf(this.jsonObject.get(RegionProperties.z.name()).toString()).intValue(),
                        Double.valueOf(this.jsonObject.get(RegionProperties.width.name()).toString()).intValue(),
                        Double.valueOf(this.jsonObject.get(RegionProperties.height.name()).toString()).intValue()
                )
        );
    }
}

