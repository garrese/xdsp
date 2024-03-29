package xis.xdsp.model;

import xis.xdsp.dto.Item;
import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.TransputMap;
import xis.xdsp.system.Memory;
import xis.xdsp.util.AppUtil;

import java.util.Map;

public class DataIntegrityChecker {

    public static void checkRecipes() {
        for (Recipe recipe : Memory.RECIPES.values()) {
            try {
                checkTransputMapAbbs(recipe.getOutputs(), recipe);
                checkTransputMapAbbs(recipe.getInputs(), recipe);
            } catch (Exception e) {
                System.out.println("ERROR checking recipe = " + recipe);
            }
        }

    }

    private static void checkTransputMapAbbs(TransputMap transputMap, Recipe recipe) {
        for (Map.Entry<String, Double> entry : transputMap.entrySet()) {
            String abb = entry.getKey();
            Item item = Memory.ITEMS.get(abb);

            if (item == null) {
                error("Abb not found. Abb=" + abb + ", Recipe=" + recipe);
            }

            Double val = entry.getValue();
            if (val == null || val == 0) {
                error("Value null/0. Abb=" + abb + ", Recipe=" + recipe);
            }
        }
    }

    private static void error(String msg) {
        System.out.println("INTEGRITY CHECK ALERT!! " + msg);
    }

}
