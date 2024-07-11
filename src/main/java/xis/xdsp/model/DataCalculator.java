package xis.xdsp.model;

import xis.xdsp.dto.*;
import xis.xdsp.Memory;
import xis.xdsp.util.AppUtil;

import java.util.*;

public class DataCalculator {


    /**
     * @param recipe recipe
     * @return production cost per recipe output item
     */
    public static TransputMap calcRecipeOutputCost(Recipe recipe, String itemKey) {
//        double outputs = calcTransputMapTotalItems(recipe.getOutputs());
        TransputMap recipeItemCost = new TransputMap();

        try {
            Double itemOutputs = recipe.getOutputs().get(itemKey);
            for (Map.Entry<String, Double> ingredient : recipe.getInputs().entrySet()) {
                Double ingredientRatio = ingredient.getValue() / itemOutputs;
                AppUtil.securePut(recipeItemCost, ingredient.getKey(), ingredientRatio);
            }
            for (Map.Entry<String, Double> ingredient : recipe.getOutputs().entrySet()) {
                if(!ingredient.getKey().equals(itemKey)) {
                    Double ingredientRatio = -1 * ingredient.getValue() / itemOutputs;
                    recipeItemCost.sumTransput(ingredient.getKey(), ingredientRatio);
                }
            }
        }catch (Exception e){
            System.out.println(AppUtil.printEx(e));
        }
        return recipeItemCost;
    }

    public static List<String> calcItemInputRecipes(String abb) {
        List<String> result = new ArrayList<>();
        for (Recipe recipe : Memory.getRecipes()) {
            for (String inputAbb : recipe.getInputs().keySet()) {
                if (inputAbb.equals(abb)) {
                    result.add(recipe.getCode());
                }
            }
        }
        return result;
    }

    public static List<String> calcItemOutputRecipes(String abb) {
        List<String> result = new ArrayList<>();
        for (Recipe recipe : Memory.getRecipes()) {
            for (String outputAbb : recipe.getOutputs().keySet()) {
                if (outputAbb.equals(abb)) {
                    result.add(recipe.getCode());
                }
            }
        }
        return result;
    }

    public static void calcProliferatorCosts() {
        Memory.getRecipes().forEach(recipe -> {
            try {
//                Memory.getRecipe()
            } catch (Exception e) {
                System.out.println("[DataCalculator.calcProliferatorCosts] ERROR " + recipe.getName() + ". " + e.getClass().getSimpleName() + ":" + e.getMessage());
            }
        });
    }

    public static double calcRecipeSpraysNeeded(Recipe recipe) {
        return recipe.getInputs().values().stream().mapToDouble(Double::doubleValue).sum();
    }


}



