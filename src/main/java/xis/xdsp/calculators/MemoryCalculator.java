package xis.xdsp.calculators;

import xis.xdsp.dto.*;
import xis.xdsp.dto.sub.Rfp;
import xis.xdsp.memory.Memory;
import xis.xdsp.util.AppUtil;
import xis.xdsp.util.ItemK;
import xis.xdsp.util.RecipeK;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import static xis.xdsp.dto.RecipeTreeNode.ROOT_NAME;
import static xis.xdsp.dto.RecipeTreeNode.composeMainBranchName;

public class MemoryCalculator {

    public static void calcAllItemsRecipes() {
        for (Item item : Memory.getItems()) {
            item.setInputRecipeList(DataCalculator.calcItemInputRecipes(item.getKey()));
            item.setOutputRecipeList(DataCalculator.calcItemOutputRecipes(item.getKey()));
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


    public static void calcAllItemsRawCosts(RecipeAltSeqMap recipeAltSeqMap) {

        Memory.RECIPE_TREE_NODES = new LinkedHashMap<>();

        Memory.getItems().forEach(item -> {
            System.out.println("[MemoryCalculator.calcItemsRawCosts] INI " + item.getName());
            try {

                RecipeTreeNode root = new RecipeTreeNode();
                root.setName(ROOT_NAME);

                RecipeTreeNode mainBranchNode = new RecipeTreeNode();
                mainBranchNode.setCost(new RecipeTreeCost(item.getKey(), 1d, null));
                mainBranchNode.setName(composeMainBranchName(root, mainBranchNode));
                root.addChild(mainBranchNode);

                RecipeTreeCalculator.calcRecipeSequences(null, item.getKey(), 1, mainBranchNode, recipeAltSeqMap);
                item.setItemRawCost(RecipeTreeCalculator.calcTreeNodeRawCost(root));

                Memory.RECIPE_TREE_NODES.put(mainBranchNode.getName(),root);

                System.out.println("[MemoryCalculator.calcAllItemsRawCosts] FIN " + item.getName() + " => " + root);
            } catch (Exception e) {
                System.out.println("[MemoryCalculator.calcAllItemsRawCosts] ERROR " + item.getName() + ". " + e.getClass().getSimpleName() + ":" + e.getMessage());
            }
        });
    }

    public static void calcAllRfps(){
        List<String> excludedRecipes = List.of("OCr-As(o)","Core-Drop","MatRec-Drop","NegSin-Drop","Shard-Drop","Neur-Drop","DfMx-Drop","Ph-RR");
        List<Rfp> rfpList = RfpCalculator.calcAllRfp(excludedRecipes, true);
        Memory.RFPS = new LinkedHashMap<>();
        for (Rfp rfp: rfpList) {
            Memory.RFPS.put(rfp.getKey(), rfp);
        }
    }


    public static void calcRawItemKeys() {
        Memory.RAW_ITEM_LIST = new HashSet<>();
        for (Recipe recipe : Memory.getRecipes()) {
            TransputMap transputMap = recipe.getRecipeRawCost();
            Memory.RAW_ITEM_LIST.addAll(transputMap.keySet());
        }
    }

    public static RecipeAltSeqMap getAltSeqMap(){
        RecipeAltSeqMap recipeAltSeqMap = new RecipeAltSeqMap();

        //BASIC
        recipeAltSeqMap.put(ItemK.EGr, RecipeK.EGr_Sm);
        recipeAltSeqMap.put(ItemK.Acid, RecipeK.Acid_Chem);  //*
        recipeAltSeqMap.put(ItemK.Oil, RecipeK.PlasRef_Refi);
        recipeAltSeqMap.put(ItemK.Dmd, RecipeK.Dmd_Sm);
        recipeAltSeqMap.put(ItemK.SilO, RecipeK.SilO_Mim);
        recipeAltSeqMap.put(ItemK.OCr, RecipeK.OCr_Chem);
        recipeAltSeqMap.put(ItemK.Gr, RecipeK.Gr_Sm);
        recipeAltSeqMap.put(ItemK.Cont, RecipeK.Cont_As);
        recipeAltSeqMap.put(ItemK.H, RecipeK.PlasRef_Refi);  //*
        recipeAltSeqMap.put(ItemK.NTube, RecipeK.NTube_Chem);
        recipeAltSeqMap.put(ItemK.Cas, RecipeK.Cas_As);
        recipeAltSeqMap.put(ItemK.PhC, RecipeK.PhC_As);
        recipeAltSeqMap.put(ItemK.D, RecipeK.D_Frtr);
        recipeAltSeqMap.put(ItemK.CrSil, RecipeK.CrSil_Sm);

        //RENEWABLE
//        recipeAltSeqMap.put(ItemK.EGr, RecipeK.EGr_Sm);
//        recipeAltSeqMap.put(ItemK.Acid, RecipeK.Acid_Pump);  //*
//        recipeAltSeqMap.put(ItemK.Oil, RecipeK.PlasRef_Refi);
//        recipeAltSeqMap.put(ItemK.Dmd, RecipeK.Dmd_Sm);
//        recipeAltSeqMap.put(ItemK.SilO, RecipeK.SilO_Mim);
//        recipeAltSeqMap.put(ItemK.OCr, RecipeK.OCr_Chem);
//        recipeAltSeqMap.put(ItemK.Gr, RecipeK.Gr_Sm);
//        recipeAltSeqMap.put(ItemK.Cont, RecipeK.Cont_As);
//        recipeAltSeqMap.put(ItemK.H, RecipeK.H_Orb);        //*
//        recipeAltSeqMap.put(ItemK.NTube, RecipeK.NTube_Chem);
//        recipeAltSeqMap.put(ItemK.Cas, RecipeK.Cas_As);
//        recipeAltSeqMap.put(ItemK.PhC, RecipeK.PhC_As);
//        recipeAltSeqMap.put(ItemK.D, RecipeK.D_Orb);        //*
//        recipeAltSeqMap.put(ItemK.CrSil, RecipeK.CrSil_Sm);


        return recipeAltSeqMap;
    }
}
