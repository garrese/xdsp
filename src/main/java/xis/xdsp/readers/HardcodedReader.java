package xis.xdsp.readers;

import xis.xdsp.dto.sub.Proliferator;
import xis.xdsp.util.ItemK;

import java.util.LinkedHashMap;

public class HardcodedReader {

    public static LinkedHashMap<String, Proliferator> readProliferators(){
        LinkedHashMap<String, Proliferator> prs = new LinkedHashMap<>();
        prs.put(ItemK.Pr1, new Proliferator(12d,0.125,0.25,0.3));
        prs.put(ItemK.Pr2, new Proliferator(24d,0.20,0.5,0.7));
        prs.put(ItemK.Pr3, new Proliferator(60d,0.25,1d,01.5));
        return prs;
    }
}
