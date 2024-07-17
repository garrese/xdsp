package xis.xdsp.calculators;

import xis.xdsp.dto.*;
import xis.xdsp.dto.sub.Proliferator;
import xis.xdsp.memory.Memory;
import xis.xdsp.util.AppUtil;
import xis.xdsp.util.PrintUtil;

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
                if (!ingredient.getKey().equals(itemKey)) {
                    Double ingredientRatio = -1 * ingredient.getValue() / itemOutputs;
                    recipeItemCost.sumTransput(ingredient.getKey(), ingredientRatio);
                }
            }
        } catch (Exception e) {
            System.out.println(PrintUtil.printEx(e));
        }
        return recipeItemCost;
    }

    public static List<String> calcItemInputRecipes(String abb) {
        List<String> result = new ArrayList<>();
        for (Recipe recipe : Memory.getRecipes()) {
            for (String inputAbb : recipe.getInputs().keySet()) {
                if (inputAbb.equals(abb)) {
                    result.add(recipe.getKey());
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
                    result.add(recipe.getKey());
                }
            }
        }
        return result;
    }

    public static double calcRecipeSpraysNeeded(Recipe recipe) {
        return recipe.getInputs().values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public static void calcRecipeSpraysRawCost(Recipe recipe) {
        if (recipe.getRecipeSpraysNeeded() != null && recipe.getRecipeSpraysNeeded() != 0) {
            for (Proliferator pr : Memory.getProliferators()) {
                Item prItem = Memory.getItem(pr.getItemKey());

                Double proliferatorFraction = recipe.getRecipeSpraysNeeded() / pr.getSpraysAvailable();
                TransputMap recipeSpraysRawCost = new TransputMap(prItem.getItemRawCost().positiveCopy());
                recipeSpraysRawCost.multiply(proliferatorFraction);

                recipe.getRecipeSpraysRawCost().put(pr.getItemKey(), recipeSpraysRawCost);
            }
        }
    }

    public static void calcRecipeRawCost(Recipe recipe) {
            TransputMap recipeRawCost = new TransputMap();
            for (Map.Entry<String, Double> inputEntry : recipe.getInputs().entrySet()) {
                String itemKey = inputEntry.getKey();
                Double itemValue = inputEntry.getValue();
                Item item = Memory.getItem(itemKey);
                TransputMap itemsRawCost = item.getItemRawCost().positiveCopy();
                itemsRawCost.multiply(itemValue);
                recipeRawCost.sumTransputMap(itemsRawCost);
            }
            recipe.setRecipeRawCost(recipeRawCost);
    }

    public static void calcRecipeRawCostPrSpeed(Recipe recipe) {
        TransputMap recipeRawCost = recipe.getRecipeRawCost();
        if (!recipeRawCost.isEmpty()) {
            for (Proliferator pr : Memory.getProliferators()) {
                TransputMap recipeSpraysRawCost = recipe.getRecipeSpraysRawCost().get(pr.getItemKey());

                if (recipeSpraysRawCost != null && !recipeSpraysRawCost.isEmpty()) {
                    TransputMap recipeRawCostPrSpeed = new TransputMap();
                    recipeRawCostPrSpeed.sumTransputMap(recipeRawCost);
                    recipeRawCostPrSpeed.sumTransputMap(recipeSpraysRawCost);
                    recipe.getRecipeRawCostPrSpeed().put(pr.getItemKey(), recipeRawCostPrSpeed);
                }
            }
        }
    }


    public static void calcRecipeRawCostPrExtra(Recipe recipe) {
        TransputMap recipeRawCost = recipe.getRecipeRawCost();
        if (!recipeRawCost.isEmpty()) {
            for (Proliferator pr : Memory.getProliferators()) {
                TransputMap recipeSpraysRawCost = recipe.getRecipeSpraysRawCost().get(pr.getItemKey());

                if (recipeSpraysRawCost != null && !recipeSpraysRawCost.isEmpty()) {

                    TransputMap recipeRawCostPrExtra = new TransputMap(recipeRawCost);
                    recipeRawCostPrExtra.sumTransputMap(recipeSpraysRawCost);

                    Double extraProduction = pr.getExtraProducts() + 1;
                    recipeRawCostPrExtra.divideDenominator(extraProduction);

                    recipe.getRecipeRawCostPrExtra().put(pr.getItemKey(), recipeRawCostPrExtra);
                }
            }
        }
    }


}



