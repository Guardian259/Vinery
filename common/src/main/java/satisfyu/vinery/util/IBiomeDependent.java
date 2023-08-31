package satisfyu.vinery.util;

import java.util.ArrayList;

public interface IBiomeDependent {

    void setBiomeTraits(Float x, Float y);
    ArrayList<Float> getBiomeTraits();

}