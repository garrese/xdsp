package xis.xdsp.readers;

import xis.xdsp.dto.sub.Factory;
import xis.xdsp.dto.sub.Proliferator;
import xis.xdsp.memory.Memory;
import xis.xdsp.util.ItemK;
import xis.xdsp.util.RecipeK;

import java.util.LinkedHashMap;
import java.util.Map;

public class HardcodedReader {

    public static LinkedHashMap<String, Proliferator> readProliferatorsMap() {
        LinkedHashMap<String, Proliferator> objects = new LinkedHashMap<>();
        objects.put(ItemK.Pr1, new Proliferator(ItemK.Pr1, RecipeK.Pr1_As, 12d, 0.125, 0.25, 0.3));
        objects.put(ItemK.Pr2, new Proliferator(ItemK.Pr2, RecipeK.Pr2_As, 24d, 0.20, 0.5, 0.7));
        objects.put(ItemK.Pr3, new Proliferator(ItemK.Pr3, RecipeK.Pr3_As, 60d, 0.25, 1d, 01.5));
        return objects;
    }

    public static Map<String, Map<String, Factory>> readFactoriesMap() {
        Map<String, Map<String, Factory>> factoriesMap = new LinkedHashMap<>();

        readFactory(1, ItemK.Mim, ItemK.Mim, 420d, 24d, 1d, factoriesMap);
        readFactory(2, ItemK.Mim, ItemK.AMim, 2940d, 168d, 2d, factoriesMap);

        readFactory(1, ItemK.Sm, ItemK.Sm, 360d, 12d, 1d, factoriesMap);
        readFactory(2, ItemK.Sm, ItemK.PlSm, 1440d, 48d, 2d, factoriesMap);
        readFactory(3, ItemK.Sm, ItemK.NegSm, 2880d, 96d, 3d, factoriesMap);

        readFactory(1, ItemK.As, ItemK.As, 270d, 12d, 0.75d, factoriesMap);
        readFactory(2, ItemK.As, ItemK.As2, 540d, 18d, 1d, factoriesMap);
        readFactory(3, ItemK.As, ItemK.As3, 1080d, 24d, 1.5d, factoriesMap);
        readFactory(4, ItemK.As, ItemK.RcAs, 2700d, 54d, 3d, factoriesMap);

        readFactory(1, ItemK.Lab, ItemK.Lab, 480d, 12d, 1d, factoriesMap);
        readFactory(2, ItemK.Lab, ItemK.SevLab, 1920d, 48d, 3d, factoriesMap);

        readFactory(1, ItemK.Pump, ItemK.Pump, 300d, 12d, 1d, factoriesMap);
        readFactory(1, ItemK.Extr, ItemK.Extr, 840d, 24d, 1d, factoriesMap);
        readFactory(1, ItemK.Refi, ItemK.Refi, 960d, 24d, 1d, factoriesMap);

        readFactory(1, ItemK.Chem, ItemK.Chem, 720d, 24d, 1d, factoriesMap);
        readFactory(2, ItemK.Chem, ItemK.QChem, 2160d, 36d, 2d, factoriesMap);

        readFactory(1, ItemK.Frtr, ItemK.Frtr, 720d, 18d, 1d, factoriesMap);
        readFactory(1, ItemK.Coll, ItemK.Coll, 12000d, 120d, 1d, factoriesMap);
        readFactory(1, ItemK.Orb, ItemK.Orb, 1d, 1d, 1d, factoriesMap);
        return factoriesMap;
    }

    public static void readFactory(
            Integer order,
            String typeKey,
            String itemKey,
            double workConsumption,
            double idleConsumption,
            double productionSpeed,
            Map<String, Map<String, Factory>> factoriesMap) {
        Map<String, Factory> typeMap = factoriesMap.computeIfAbsent(typeKey, k -> new LinkedHashMap<>());
        String name = Memory.getItem(itemKey).getName();
        typeMap.put(itemKey, new Factory(order, typeKey, itemKey, name, workConsumption, idleConsumption, productionSpeed));
    }


}
