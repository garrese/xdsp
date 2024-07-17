package xis.xdsp.calculators;

import xis.xdsp.dto.*;
import xis.xdsp.memory.Memory;
import xis.xdsp.util.AppUtil;

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
                if (recipeOutputCost.size() > 0) {
                    AppUtil.securePut(recipe.getOutputsCost(), outputItemKey, recipeOutputCost);
                }
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

    public static void calcAllRecipesRawCost() {
        for (Recipe recipe : Memory.getRecipes()) {
            DataCalculator.calcRecipeRawCost(recipe);
        }
    }

    public static void calcAllRecipesRawCostPrSpeed() {
        for (Recipe recipe : Memory.getRecipes()) {
            DataCalculator.calcRecipeRawCostPrSpeed(recipe);
        }
    }

    public static void calcAllRecipesRawCostPrExtra() {
        for (Recipe recipe : Memory.getRecipes()) {
            DataCalculator.calcRecipeRawCostPrExtra(recipe);
        }
    }


    public static void calcItemsRawCosts(RecipeAltSeqMap recipeAltSeqMap) {
        Memory.getItems().forEach(item -> {
            System.out.println("[MemoryCalculator.calcItemsRawCosts] INI " + item.getName());
            try {

                RecipeTreeNode root = new RecipeTreeNode();
                root.setName(ROOT_NAME);

                RecipeTreeNode mainBranchNode = new RecipeTreeNode();
                mainBranchNode.setCost(new RecipeTreeCost(item.getAbb(), 1d, null));
                mainBranchNode.setName(composeMainBranchName(root, mainBranchNode));
                root.addChild(mainBranchNode);

                RecipeTreeCalculator.calcRecipeSequences(null, item.getAbb(), 1, mainBranchNode, recipeAltSeqMap);

//                recipe.setRecipeTreeNode(root);
                item.setItemRawCost(RecipeTreeCalculator.calcTreeNodeRawCost(root));

                System.out.println("[MemoryCalculator.calcItemsRawCosts] FIN " + item.getName() + " => " + root);
            } catch (Exception e) {
                System.out.println("[DataCalculator.calcRecipesItemCostTree] ERROR " + item.getName() + ". " + e.getClass().getSimpleName() + ":" + e.getMessage());
            }
        });
    }

    public static void calcRawItemKeys() {
        Memory.RAW_ITEM_LIST = new HashSet<>();
        for (Recipe recipe : Memory.getRecipes()) {
            TransputMap transputMap = recipe.getRecipeRawCost();
            Memory.RAW_ITEM_LIST.addAll(transputMap.keySet());
        }
    }
}
