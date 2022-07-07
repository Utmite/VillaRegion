package vicente.rocka.util.enums;

public enum RegionProperties {
    x,
    z,
    width,
    height,
    down,
    up,
    world,

    name;

    public Object getType(){
        return switch (this) {
            case x, z, width, height, down, up -> 0;
            case world, name -> "";
        };
    }
}
