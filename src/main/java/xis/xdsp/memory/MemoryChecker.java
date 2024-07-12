package xis.xdsp.memory;

import xis.xdsp.dto.Item;
import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.TransputMap;

import java.util.Map;

public class MemoryChecker {

    public static void checkRecipes() {
        for (Recipe recipe : Memory.getRecipes()) {
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
            Item item = Memory.getItem(abb);

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
