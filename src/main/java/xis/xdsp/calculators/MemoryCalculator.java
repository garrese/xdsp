package xis.xdsp.calculators;

import xis.xdsp.dto.*;
import xis.xdsp.memory.Memory;
import xis.xdsp.util.AppUtil;
import xis.xdsp.util.ItemK;
import xis.xdsp.util.RecipeK;

import java.util.HashSet;

import static xis.xdsp.dto.RecipeTreeNode.ROOT_NAME;
import static xis.xdsp.dto.RecipeTreeNode.composeMainBranchName;

public class MemoryCalculator {

    public static void calcAllItemsRecipes() {
        for (Item item : Memory.getItems()) {
            item.setInputRecipeList(DataCalculator.calcItemInputRecipes(item.getAbb()));
            item.setOutputRecipeList(DataCalculator.calcItemOutputRecipes(item.getAbb()));
        }
    }

    public static void calcAllRecipesOutputCost() {
        for (Recipe recipe : Memory.getRecipes()) {
            for (String outputItemKey : recipe.getOutputs().keySet()) {
                TransputMap recipeOutputCost = DataCalculator.calcRecipeOutputCost(recipe, outputItemKey);
                AppUtil.securePut(recipe.getOutputsCost(), outputItemKey, recipeOutputCost);
            }
        }
    }

    public static void calcAllRecipesSpraysNeeded() {
        for (Recipe recipe : Memory.getRecipes()) {
            recipe.setRecipeSpraysNeeded(DataCalculator.calcRecipeSpraysNeeded(recipe));
        }
    }

    public static void calcAllRecipesSpraysRawCost() {
        for (Recipe recipe : Memory.getRecipes()) {
            DataCalculator.calcRecipeSpraysRawCost(recipe);
        }
    }

    public static void calcAllRecipeRawCostPrSpeed() {
        for (Recipe recipe : Memory.getRecipes()) {
            DataCalculator.calcRecipeRawCostPrSpeed(recipe);
        }
    }

    public static void calcAllRecipeRawCostPrExtra() {
        for (Recipe recipe : Memory.getRecipes()) {
            DataCalculator.calcRecipeRawCostPrExtra(recipe);
        }
    }


    public static void calcRecipesRawCosts(RecipeAltSeqMap recipeAltSeqMap) {


        Memory.getRecipes().forEach(recipe -> {
            System.out.println("[DataCalculator.calcRecipesItemCostTree] INI " + recipe.getName());
            try {

                RecipeTreeNode root = new RecipeTreeNode();
                root.setName(ROOT_NAME);

                RecipeTreeNode mainBranchNode = new RecipeTreeNode();
                mainBranchNode.setCost(new RecipeTreeCost(null, 1d, recipe.getKey()));
                mainBranchNode.setName(composeMainBranchName(root, mainBranchNode));
                root.addChild(mainBranchNode);

                RecipeTreeCalculator.calcRecipeSequences(recipe, 1, mainBranchNode, recipeAltSeqMap);

//                recipe.setRecipeTreeNode(root);
                recipe.setRecipeRawCost(RecipeTreeCalculator.calcTreeNodeSourcesCost(root));

                System.out.println("[DataCalculator.calcRecipesItemCostTree] FIN " + recipe.getName() + " => " + root);
            } catch (Exception e) {
                System.out.println("[DataCalculator.calcRecipesItemCostTree] ERROR " + recipe.getName() + ". " + e.getClass().getSimpleName() + ":" + e.getMessage());
                if (recipe.getName().equals("Graphene")) {
//                    e.printStackTrace();
                }
            }
        });
    }

    public static void calcSourceItems() {
        Memory.SOURCE_ITEMS = new HashSet<>();
        for(Recipe recipe : Memory.getRecipes()){
            TransputMap transputMap = recipe.getRecipeRawCost();
            Memory.SOURCE_ITEMS.addAll(transputMap.keySet());
        }
    }
}
