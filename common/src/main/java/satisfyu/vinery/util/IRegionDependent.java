package satisfyu.vinery.util;

public interface IRegionDependent {
    void setDecorativeName(FlavorTextType type, String name);
    public String getDecorativeName(FlavorTextType type);
}