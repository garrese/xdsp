package xis.xdsp.model;

import xis.xdsp.dto.Item;
import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.TransputMap;

import java.util.Map;

public class DataCalculator {


    /**
     * @param itemRecipe recipe
     * @return production cost per recipe output item
     */
    public static TransputMap calcOneItemCost(Recipe itemRecipe) {
        double outputs = calcTransputMapTotalItems(itemRecipe.getOutputs());

        TransputMap result = new TransputMap();
        for (Map.Entry<String, Double> ingredient : itemRecipe.getInputs().entrySet()) {
            double ingredientRatio = ingredient.getValue() / outputs;
            result.put(ingredient.getKey(), ingredientRatio);
        }

        return result;

    }

    private static double calcTransputMapTotalItems(TransputMap transputMap) {
        double total = 0;
        for (Double n : transputMap.values()) {
            total += n;
        }
        return total;
    }


}
