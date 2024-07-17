package xis.xdsp.calculators;

import xis.xdsp.dto.*;
import xis.xdsp.ex.RecipeAltSeqException;
import xis.xdsp.memory.Memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeTreeCalculator {


    /**
     * Actualmente hasta 2024-07-16:
     * Se llama a una recipe (nodeRecipe) con una cantidad (amount).
     * Para esta recipe, se recorre cada item input:
     *      Se calcula la cantidad del item input (multiplicando por el amount indicado para esta recipe). Se setea el coste (RecipeTreeCost).
     *      Se desambigua la receta para este input.
     *      Se llama en recursivo a este función con la receta desambiguada y la cantidad calculada.
     *
     * Correcciones necesarias en 2024-07-16:
     * No se está teniendo en cuenta si la receta actual (nodeRecipe) genera más de 1 output. No se está dividiendo.
     * Se puede ir incluyendo la generación (costes negativos) para recetas con más de 1 tipo de output.
     * Soluciones:
     *      Para la receta actual tengo el coste por item producido (que incluye costes negativos).
     *      Problema: no sé el item que quiero producir. Hay que añadirlo como argumento de la función recursiva (como nodeItem).
     *      Se puede sustituir el cálculo de inputAmount por el de los costes por item producido.
     *      Problema: esta función se está usando para calcular el coste de la receta, no de un item de la receta.
     *      Es en la primera llamada (raiz) cuando no sé el nodeItem que quiero. En las subllamadas sí que busco un item concreto. Es esto incoherente?
     *      Puedo informar nulo el nodeItem en la primera llamada (porque busco el coste de la receta completa), e informarlo en las subsiguientes (donde busco el coste de un item).
     *      O quizás debería cambiar el enfoque, y calcular el coste de items desde el comienzo, no el de recetas. Y parece más coherente. Voy a hacer esto.
     *      Hay que mover la desambiguación del item desde dentro del bucle de costes hacia el inicio de la función. Y se puede quitar del argumento (nodeRecipe).
     *      ...
     *
     */
    public static RecipeTreeNode calcRecipeSequences(Recipe parentNodeRecipe, String nodeItemKey, double amount, RecipeTreeNode currentNode, Map<String, RecipeAltSeq> altSeqMap) throws Exception {

        if (currentNode.getCost() == null) {
            throw new Exception("calcRecipeSequences ERROR: cost null for node " + currentNode);
        }

        if ("Core".equals(nodeItemKey)) {
//            System.out.println("break-point here");
        }

        // Debug vars, do not delete
        RecipeTreeNode root = currentNode.getRoot();

        Recipe nodeRecipe = calcRecipeByItem(parentNodeRecipe, nodeItemKey, currentNode, altSeqMap);

        currentNode.getCost().setRecipeKey(nodeRecipe.getKey());
        currentNode.getRecipeHistory().add(nodeRecipe.getKey());
        currentNode.generateName();

        TransputMap itemCost = nodeRecipe.getOutputsCost().get(nodeItemKey);
        if(itemCost != null) {
            for (Map.Entry<String, Double> costEntry : itemCost.entrySet()) {
                String costItemKey = costEntry.getKey();
                Double costItemValue = costEntry.getValue();

                double inputAmount = amount * costItemValue;

                RecipeTreeNode childNode = new RecipeTreeNode();
                childNode.setCost(new RecipeTreeCost(costItemKey, inputAmount, null));
                childNode.generateName();
                currentNode.addChild(childNode);

                if(costItemValue > 0) {
                    calcRecipeSequences(nodeRecipe, costItemKey, inputAmount, childNode, altSeqMap);
                }
            }
        }

        return currentNode;
    }

    private static Recipe calcRecipeByItem(Recipe parentNodeRecipe, String nodeItemKey, RecipeTreeNode currentNode, Map<String, RecipeAltSeq> altSeqMap) throws RecipeAltSeqException {
        Item nodeItem = Memory.getItem(nodeItemKey);
        List<String> nodeItemRecipeList = nodeItem.getOutputRecipeList();
        RecipeMap nodeItemRecipeMap = Memory.getRecipesMapByList(nodeItemRecipeList);
        List<Recipe> filteredRecipeList = new ArrayList<>(nodeItemRecipeMap.values());
        Recipe nodeRecipe = selectItemRecipe(parentNodeRecipe, currentNode, altSeqMap, nodeItemKey, filteredRecipeList);
        return nodeRecipe;
    }

    private static Recipe selectItemRecipe(Recipe parentNodeRecipe, RecipeTreeNode currentNode, Map<String, RecipeAltSeq> altSeqMap, String inputItemKey, List<Recipe> filteredRecipeList) throws RecipeAltSeqException {
        Recipe selectedCostRecipe;
        if (inputItemKey.equals("EGr")) {
//                System.out.println("break-point here");
        }
        if (filteredRecipeList.size() > 1) {
//                System.out.println("Alternative recipes detected: " + filteredRecipeList.stream().map(Recipe::getCode).toList());
        }

        filteredRecipeList = filterNonCircularRecipes(parentNodeRecipe, currentNode, filteredRecipeList);
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
    private static List<Recipe> filterNonCircularRecipes(Recipe parentNodeRecipe, RecipeTreeNode recipeTreeNode, List<Recipe> recipeList) {
        ArrayList<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipeList) {
            if (parentNodeRecipe != null && recipe.getKey().equals(parentNodeRecipe.getKey())) {
                break;
            }
//            A recipe can be repeated. For example, if one item that need plastic is made of another item of plastic.
//            if (recipeTreeNode.getRecipeHistory().contains(recipe.getCode())) {
//                break;
//            }
            result.add(recipe);
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

    public static TransputMap calcTreeNodeRawCost(RecipeTreeNode recipeTreeNode) {
        TransputMap rawCost = new TransputMap();
        recipeTreeNode.forEach(node -> {
            if (node.getName().equals("SilO-Mim(SilO)")) {
//                System.out.println("break-here");
            }
            if (!node.getName().equals("root")) {
                RecipeTreeCost cost = node.getCost();
                String itemKey = cost.getItemKey();
                String recipeKey = cost.getRecipeKey();

                boolean mainBranch = itemKey == null;
                if (node.getChildMap().size() == 0 && !mainBranch) {

                    Double currentItemCost = cost.getAmount();
                    Double accumulatedItemCost = rawCost.get(itemKey);
                    Double totalItemCost = currentItemCost;
                    if (accumulatedItemCost != null) {
                        totalItemCost += accumulatedItemCost;
                    }
                    rawCost.put(itemKey, totalItemCost);
                }
            }
        });
        return rawCost;
    }
}
