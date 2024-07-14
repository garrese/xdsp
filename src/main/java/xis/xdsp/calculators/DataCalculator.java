package xis.xdsp.calculators;

import xis.xdsp.dto.*;
import xis.xdsp.dto.sub.Proliferator;
import xis.xdsp.memory.Memory;
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
                if (!ingredient.getKey().equals(itemKey)) {
                    Double ingredientRatio = -1 * ingredient.getValue() / itemOutputs;
                    recipeItemCost.sumTransput(ingredient.getKey(), ingredientRatio);
                }
            }
        } catch (Exception e) {
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

    public static double calcRecipeSpraysNeeded(Recipe recipe) {
        return recipe.getInputs().values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public static void calcRecipeSpraysSourceCost(Recipe recipe) {
//        recipe.setRecipeSpraysSourceCost(new LinkedHashMap<>());

        if (recipe.getRecipeSpraysNeeded() != null && recipe.getRecipeSpraysNeeded() != 0) {
            for (Proliferator pr : Memory.getProliferators()) {
                Recipe prRecipe = Memory.getRecipe(pr.getRecipeKey());

                Double proliferatorFraction = recipe.getRecipeSpraysNeeded() / pr.getSpraysAvailable();
                TransputMap recipeSpraysSourceCost = new TransputMap(prRecipe.getRecipeSourcesCost());
                recipeSpraysSourceCost.multiply(proliferatorFraction);

                recipe.getRecipeSpraysSourceCost().put(pr.getItemKey(), recipeSpraysSourceCost);
            }
        }
    }

    public static void calcRecipeSourcesCostPrSpeed(Recipe recipe) {
        TransputMap recipeSourcesCost = recipe.getRecipeSourcesCost();
        if (!recipeSourcesCost.isEmpty()) {
            for (Proliferator pr : Memory.getProliferators()) {
                TransputMap recipeSpraysSourceCost = recipe.getRecipeSpraysSourceCost().get(pr.getItemKey());

                if (recipeSpraysSourceCost != null && !recipeSpraysSourceCost.isEmpty()) {
                    TransputMap recipeSourcesCostPrSpeed = new TransputMap();
                    recipeSourcesCostPrSpeed.sumTransputMap(recipeSourcesCost);
                    recipeSourcesCostPrSpeed.sumTransputMap(recipeSpraysSourceCost);
                    recipe.getRecipeSourcesCostPrSpeed().put(pr.getItemKey(), recipeSourcesCostPrSpeed);
                }
            }
        }
    }

    public static void calcRecipeSourcesCostPrExtra(Recipe recipe) {
        TransputMap recipeSourcesCost = recipe.getRecipeSourcesCost();
        if (!recipeSourcesCost.isEmpty()) {
            for (Proliferator pr : Memory.getProliferators()) {
                TransputMap recipeSpraysSourceCost = recipe.getRecipeSpraysSourceCost().get(pr.getItemKey());

                if (recipeSpraysSourceCost != null && !recipeSpraysSourceCost.isEmpty()) {
                    Double extraProduction = pr.getExtraProducts() + 1;
                    TransputMap recipeSourcesCostPrExtra = new TransputMap(recipeSourcesCost);
                    recipeSourcesCostPrExtra.divideDenominator(extraProduction);
                    recipeSourcesCostPrExtra.sumTransputMap(recipeSpraysSourceCost);

                    recipe.getRecipeSourcesCostPrExtra().put(pr.getItemKey(), recipeSourcesCostPrExtra);
                }
            }
        }
    }


}



