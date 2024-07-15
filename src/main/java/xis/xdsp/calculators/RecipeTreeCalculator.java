package xis.xdsp.calculators;

import xis.xdsp.dto.*;
import xis.xdsp.ex.RecipeAltSeqException;
import xis.xdsp.memory.Memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeTreeCalculator {



    public static RecipeTreeNode calcRecipeSequences(Recipe nodeRecipe, double amount, RecipeTreeNode currentNode, Map<String, RecipeAltSeq> altSeqMap) throws Exception {
//        System.out.println("[DataCalculator.calcRecipeCostTree] INI (" + recipe.getName() + "," + amount + "," + recipeCostTree + ")");
//        if (recipe.getName().equals("Supersonic Missile Set")) return new RecipeCostTree();

        if (nodeRecipe.getKey().equals("H-Orb")) {
            System.out.println("break-point here");
        }

        RecipeTreeNode root = currentNode.getRoot();
        Map<String, TransputMap> nodeRecipeItemCost = nodeRecipe.getOutputsCost();

        for (String inputItemKey : nodeRecipe.getInputs().keySet()) {

            Item costItem = Memory.getItem(inputItemKey);
            List<String> costRecipeList = costItem.getOutputRecipeList();
            RecipeMap costRecipeMap = Memory.getRecipesMapByList(costRecipeList);
            List<Recipe> filteredRecipeList = new ArrayList<>(costRecipeMap.values());
            Recipe selectedCostRecipe = selectCostRecipe(nodeRecipe, currentNode, altSeqMap, inputItemKey, filteredRecipeList);

            double inputAmount = amount * nodeRecipe.getInputs().get(inputItemKey);
            RecipeTreeNode childNode = new RecipeTreeNode();
//            if (!nodeRecipe.isSource()) {
                childNode.setCost(new RecipeTreeCost(inputItemKey, inputAmount, selectedCostRecipe.getKey()));
//            }
            childNode.generateName();
            childNode.getRecipeHistory().add(selectedCostRecipe.getKey());

            currentNode.addChild(childNode);

            if (!selectedCostRecipe.isSource()) {
                calcRecipeSequences(selectedCostRecipe, inputAmount, childNode, altSeqMap);
            }
        }

        return currentNode;
    }

    private static Recipe selectCostRecipe(Recipe nodeRecipe, RecipeTreeNode currentNode, Map<String, RecipeAltSeq> altSeqMap, String inputItemKey, List<Recipe> filteredRecipeList) throws RecipeAltSeqException {
        Recipe selectedCostRecipe;
        if (inputItemKey.equals("EGr")) {
//                System.out.println("break-point here");
        }
        if (filteredRecipeList.size() > 1) {
//                System.out.println("Alternative recipes detected: " + filteredRecipeList.stream().map(Recipe::getCode).toList());
        }

        filteredRecipeList = filterNonCircularRecipes(nodeRecipe, currentNode, filteredRecipeList);
        filteredRecipeList = filterExcludedRecipes(currentNode, filteredRecipeList);
//            filteredRecipeList = filterNonSourcesFirst(filteredRecipeList);


        int alternativeRecipes = filteredRecipeList.size();
        if (alternativeRecipes == 1) {
            selectedCostRecipe = filteredRecipeList.getFirst();
        } else if (alternativeRecipes > 1) {
            if (altSeqMap == null || altSeqMap.get(inputItemKey) == null) {
                throw new RuntimeException("No alternative sequence map for [" + inputItemKey + "] alternativeRecipes: "
                        + filteredRecipeList.stream().map(Recipe::getKey).toList());
            }
            RecipeAltSeq itemAltSeq = altSeqMap.get(inputItemKey);
            selectedCostRecipe = filterAlternatives(itemAltSeq, filteredRecipeList);
        } else {
            throw new RuntimeException("No recipes found for [" + inputItemKey + "]");
        }
        return selectedCostRecipe;
    }

    /**
     * Discards recipes that cause circular loops.
     */
    private static List<Recipe> filterNonCircularRecipes(Recipe nodeRecipe, RecipeTreeNode recipeTreeNode, List<Recipe> costRecipeList) {
        ArrayList<Recipe> result = new ArrayList<>();
        for (Recipe costRecipe : costRecipeList) {
            if (costRecipe.getKey().equals(nodeRecipe.getKey())) {
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
                if (next.equals(recipe.getKey())) {
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
            if (!main.getRecipeExclusions().contains(costRecipe.getKey())) {
                result.add(costRecipe);
            }
        }
        return result;
    }

    public static TransputMap calcTreeNodeSourcesCost(RecipeTreeNode recipeTreeNode) {
        TransputMap sourcesCost = new TransputMap();
        recipeTreeNode.forEach(node -> {
            if (!node.getName().equals("root")) {
                RecipeTreeCost cost = node.getCost();
                String itemKey = cost.getItemKey();
                String recipeKey = cost.getRecipeKey();

                if (Memory.getRecipe(recipeKey).isSource() && itemKey != null) {

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
