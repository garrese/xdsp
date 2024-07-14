package xis.xdsp.readers;

import xis.xdsp.dto.sub.Proliferator;
import xis.xdsp.util.ItemK;
import xis.xdsp.util.RecipeK;

import java.util.LinkedHashMap;

public class HardcodedReader {

    public static LinkedHashMap<String, Proliferator> readProliferators(){
        LinkedHashMap<String, Proliferator> prs = new LinkedHashMap<>();
        prs.put(ItemK.Pr1, new Proliferator(ItemK.Pr1, RecipeK.Pr1_As,12d,0.125,0.25,0.3));
        prs.put(ItemK.Pr2, new Proliferator(ItemK.Pr2, RecipeK.Pr2_As,24d,0.20,0.5,0.7));
        prs.put(ItemK.Pr3, new Proliferator(ItemK.Pr3, RecipeK.Pr3_As,60d,0.25,1d,01.5));
        return prs;
    }
}
