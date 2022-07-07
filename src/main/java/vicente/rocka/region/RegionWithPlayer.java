package vicente.rocka.region;

import java.awt.Rectangle;

import org.bukkit.World;
import org.json.JSONObject;

public class RegionWithPlayer extends Region{
    private Flag flag;
    private Furlough furlough;

    public RegionWithPlayer(Rectangle rectangle, int down, int up, World world, String name, Flag flag, Furlough furlough) {
        super(rectangle, down, up, world, name);
        this.setFlag(flag);
        this.setFurlough(furlough);
    }

    public RegionWithPlayer(JSONObject properties, Flag flag, Furlough furlough) {
        super(properties);
        this.setFlag(flag);
        this.setFurlough(furlough);
    }


    public Flag getFlag() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag = flag;
    }

    public Furlough getFurlough() {
        return furlough;
    }

    public void setFurlough(Furlough furlough) {
        this.furlough = furlough;
    }

}
