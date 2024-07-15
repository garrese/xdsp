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


    public static void calcRecipesRawCosts() {

        RecipeAltSeqMap recipeAltSeqMap = new RecipeAltSeqMap();
        recipeAltSeqMap.put(ItemK.EGr, RecipeK.EGr_Sm);
        recipeAltSeqMap.put(ItemK.Acid, RecipeK.Acid_Chem);
        recipeAltSeqMap.put(ItemK.Oil, RecipeK.PlasRef_Refi);
        recipeAltSeqMap.put(ItemK.Dmd, RecipeK.Dmd_Sm);
        recipeAltSeqMap.put(ItemK.SilO, RecipeK.SilO_Mim);
        recipeAltSeqMap.put(ItemK.OCr, RecipeK.OCr_Chem);
        recipeAltSeqMap.put(ItemK.Gr, RecipeK.Gr_Sm);
        recipeAltSeqMap.put(ItemK.Cont, RecipeK.Cont_As);
        recipeAltSeqMap.put(ItemK.H, RecipeK.H_Orb);
        recipeAltSeqMap.put(ItemK.NTube, RecipeK.NTube_Chem);
        recipeAltSeqMap.put(ItemK.Cas, RecipeK.Cas_As);
        recipeAltSeqMap.put(ItemK.PhC, RecipeK.PhC_As);
        recipeAltSeqMap.put(ItemK.D, RecipeK.D_Frtr);
        recipeAltSeqMap.put(ItemK.CrSil, RecipeK.CrSil_Sm);


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
