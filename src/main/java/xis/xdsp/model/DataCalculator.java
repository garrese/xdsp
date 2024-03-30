package xis.xdsp.model;

import xis.xdsp.dto.*;
import xis.xdsp.ex.RecipeAltSeqException;
import xis.xdsp.system.Memory;
import xis.xdsp.util.AppUtil;
import xis.xdsp.util.ItemK;
import xis.xdsp.util.RecipeK;

import java.util.*;

import static xis.xdsp.dto.RecipeTreeNode.ROOT_NAME;
import static xis.xdsp.dto.RecipeTreeNode.composeMainBranchName;

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
            AppUtil.securePut(result, ingredient.getKey(), ingredientRatio);
        }

        return result;

    }

    static double calcTransputMapTotalItems(TransputMap transputMap) {
        double total = 0;
        for (Double n : transputMap.values()) {
            total += n;
        }
        return total;
    }

    public static void calcItemsRecipes() {
        for (Item item : Memory.ITEMS.values()) {
            item.setInputRecipeList(calcInputRecipes(item.getAbb()));
            item.setOutputRecipeList(calcOutputRecipes(item.getAbb()));
        }
    }

    public static void calcRecipesItemCost() {
        Memory.RECIPES.values().forEach(recipe -> recipe.setItemCost(DataCalculator.calcOneItemCost(recipe)));
    }

    public static void calcResourcesCosts() {

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


        Memory.RECIPES.values().forEach(recipe -> {
            System.out.println("[DataCalculator.calcRecipesItemCostTree] INI " + recipe.getName());
            try {

                RecipeTreeNode root = new RecipeTreeNode();
                root.setName(ROOT_NAME);

                RecipeTreeNode mainBranchNode = new RecipeTreeNode();
                mainBranchNode.setCost(new RecipeTreeCost(null, 1d, recipe.getCode()));
                mainBranchNode.setName(composeMainBranchName(root, mainBranchNode));
                root.addChild(mainBranchNode);

                calcRecipeSequences(recipe, 1, mainBranchNode, recipeAltSeqMap);

//                recipe.setRecipeTreeNode(root);
                recipe.setSourcesCost(calcSourcesCost(root));

                System.out.println("[DataCalculator.calcRecipesItemCostTree] FIN " + recipe.getName() + " => " + root);
            } catch (Exception e) {
                System.out.println("[DataCalculator.calcRecipesItemCostTree] ERROR " + recipe.getName() + ". " + e.getClass().getSimpleName() + ":" + e.getMessage());
                if (recipe.getName().equals("Graphene")) {
//                    e.printStackTrace();
                }
            }
        });
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

    private static List<String> calcOutputRecipes(String abb) {
        List<String> result = new ArrayList<>();
        for (Recipe recipe : Memory.RECIPES.values()) {
            for (String outputAbb : recipe.getOutputs().keySet()) {
                if (outputAbb.equals(abb)) {
                    result.add(recipe.getCode());
                }
            }
        }
        return result;
    }

    private static List<String> calcInputRecipes(String abb) {
        List<String> result = new ArrayList<>();
        for (Recipe recipe : Memory.RECIPES.values()) {
            for (String inputAbb : recipe.getInputs().keySet()) {
                if (inputAbb.equals(abb)) {
                    result.add(recipe.getCode());
                }
            }
        }
        return result;
    }

    public static RecipeTreeNode calcRecipeSequences(Recipe nodeRecipe, double amount, RecipeTreeNode currentNode, Map<String, RecipeAltSeq> altSeqMap) throws Exception {
//        System.out.println("[DataCalculator.calcRecipeCostTree] INI (" + recipe.getName() + "," + amount + "," + recipeCostTree + ")");
//        if (recipe.getName().equals("Supersonic Missile Set")) return new RecipeCostTree();

        if (nodeRecipe.getCode().equals("Turb-As")) {
//            System.out.println("break-point here");
        }

        RecipeTreeNode root = currentNode.getRoot();
        TransputMap nodeItemCost = nodeRecipe.getItemCost();

        for (Map.Entry<String, Double> nodeItemCostEntry : nodeItemCost.entrySet()) {
            Item costItem = Memory.ITEMS.get(nodeItemCostEntry.getKey());
            List<String> costRecipeList = costItem.getOutputRecipeList();
            RecipeMap costRecipeMap = Memory.getRecipeMap(costRecipeList);
            List<Recipe> filteredRecipeList = new ArrayList<>(costRecipeMap.values());

            if (costItem.getAbb().equals("EGr")) {
//                System.out.println("break-point here");
            }


            if (filteredRecipeList.size() > 1) {
//                System.out.println("Alternative recipes detected: " + filteredRecipeList.stream().map(Recipe::getCode).toList());
            }
            filteredRecipeList = filterNonCircularRecipes(nodeRecipe, currentNode, filteredRecipeList);
            filteredRecipeList = filterExcludedRecipes(currentNode, filteredRecipeList);
//            filteredRecipeList = filterNonSourcesFirst(filteredRecipeList);

            Recipe selectedCostRecipe;
            int alternativeRecipes = filteredRecipeList.size();
            if (alternativeRecipes == 1) {
                selectedCostRecipe = filteredRecipeList.getFirst();
            } else if (alternativeRecipes > 1) {
                if (altSeqMap == null || altSeqMap.get(costItem.getAbb()) == null) {
                    throw new RuntimeException("No alternative sequence map for [" + costItem.getAbb() + "] alternativeRecipes: "
                            + filteredRecipeList.stream().map(Recipe::getCode).toList());
                }
                RecipeAltSeq itemAltSeq = altSeqMap.get(costItem.getAbb());
                selectedCostRecipe = filterAlternatives(itemAltSeq, filteredRecipeList);
            } else {
                throw new RuntimeException("No recipes found for [" + costItem.getAbb() + "]");
            }

            double inputAmount = amount * nodeItemCostEntry.getValue();
            RecipeTreeNode childNode = new RecipeTreeNode();
            childNode.setCost(new RecipeTreeCost(costItem.getAbb(), inputAmount, selectedCostRecipe.getCode()));
            childNode.generateName();
            childNode.getRecipeHistory().add(selectedCostRecipe.getCode());

            currentNode.addChild(childNode);

            if (!selectedCostRecipe.isSource()) {
                calcRecipeSequences(selectedCostRecipe, inputAmount, childNode, altSeqMap);
            }
        }

        return currentNode;
    }

    /**
     * Discards recipes that cause circular loops.
     */
    private static List<Recipe> filterNonCircularRecipes(Recipe nodeRecipe, RecipeTreeNode recipeTreeNode, List<Recipe> costRecipeList) {
        ArrayList<Recipe> result = new ArrayList<>();
        for (Recipe costRecipe : costRecipeList) {
            if (costRecipe.getCode().equals(nodeRecipe.getCode())) {
                break;
            }
//            A recipe can be repeated. For example, if one item that need plastic is made of another item of plastic.
//            if (recipeTreeNode.getRecipeHistory().contains(costRecipe.getCode())) {
//                break;
//            }
            result.add(costRecipe);
        }
        return result;
    }

    public static Recipe filterAlternatives(RecipeAltSeq recipeAltSeq, List<Recipe> alternativeRecipeList) throws RecipeAltSeqException {
        try {
            String next = recipeAltSeq.getNext();
            for (Recipe recipe : alternativeRecipeList) {
                if (next.equals(recipe.getCode())) {
                    return recipe;
                }
            }
            throw new RecipeAltSeqException("Next alternative sequence recipe (" + next + ") not found in alternatives: " + alternativeRecipeList);
        } catch (RecipeAltSeqException e) {
            boolean manualMode = false;
            System.out.println("RecipeAltSeqException ex: " + e.getMessage());
            if (manualMode) {
                return new Recipe();
            } else {
                throw e;
            }
        }
    }

//    private static List<Recipe> filterSources(List<Recipe> costRecipeList){
//        ArrayList<Recipe> nonSources = new ArrayList<>(costRecipeList.stream().filter(r -> !r.isSource()).toList());
//        if(!nonSources.isEmpty()){
//            return nonSources;
//        }
//    }

    private void createForks(List<RecipeTreeNode> forks, List<Recipe> costRecipeList) {
        ArrayList<Recipe> nonSources = new ArrayList<>(costRecipeList.stream().filter(r -> !r.isSource()).toList());
        if (!nonSources.isEmpty()) {

        }
    }

    /**
     * Select nonSources recipes before sources
     */
    private static List<Recipe> filterNonSourcesFirst(List<Recipe> alternativeRecipeList) {
        ArrayList<Recipe> nonSources = new ArrayList<>(alternativeRecipeList.stream().filter(r -> !r.isSource()).toList());
        if (!nonSources.isEmpty()) {
            return nonSources;
        } else {
            return alternativeRecipeList;
        }
    }

    private static List<Recipe> filterExcludedRecipes(RecipeTreeNode recipeTreeNode, List<Recipe> costRecipeList) {

        RecipeTreeNode main = recipeTreeNode.getMainBranch();
        if (main.getRecipeExclusions() == null || main.getRecipeExclusions().isEmpty()) {
            return costRecipeList;
        }

        ArrayList<Recipe> result = new ArrayList<>();
        for (Recipe costRecipe : costRecipeList) {
            if (!main.getRecipeExclusions().contains(costRecipe.getCode())) {
                result.add(costRecipe);
            }
        }
        return result;
    }

    public static TransputMap calcSourcesCost(RecipeTreeNode recipeTreeNode) {
        TransputMap sourcesCost = new TransputMap();
        recipeTreeNode.forEach(node -> {
            if (!node.getName().equals("root")) {
                RecipeTreeCost cost = node.getCost();
                String itemKey = cost.getItemKey();
                String recipeKey = cost.getRecipeKey();

                if (Memory.RECIPES.get(recipeKey).isSource()) {

                    Double currentItemCost = cost.getAmount();
                    Double accumulatedItemCost = sourcesCost.get(itemKey);
                    Double totalItemCost = currentItemCost;
                    if (accumulatedItemCost != null) {
                        totalItemCost += accumulatedItemCost;
                    }
                    sourcesCost.put(itemKey, totalItemCost);
                }
            }
        });
        return sourcesCost;
    }


}



