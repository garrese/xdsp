package xis.xdsp.model;

import xis.xdsp.dto.*;
import xis.xdsp.system.Memory;
import xis.xdsp.util.AppUtil;

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

    public static void calcRecipesItemCostTree() {
        Memory.RECIPES.values().forEach(recipe -> {
            System.out.println("[DataCalculator.calcRecipesItemCostTree] INI " + recipe.getName());

            try {

                RecipeTreeNode root = new RecipeTreeNode();
                root.setName(ROOT_NAME);

                RecipeTreeNode mainBranchNode = new RecipeTreeNode();
                mainBranchNode.setCost(new RecipeTreeCost(null, 1d, recipe.getCode()));
                mainBranchNode.setName(composeMainBranchName(root, mainBranchNode));
                root.addChild(mainBranchNode);

                calcRecipeCostTree(recipe, 1, mainBranchNode);

                recipe.setRecipeTreeNode(root);

                System.out.println("[DataCalculator.calcRecipesItemCostTree] FIN " + recipe.getName() + " => " + recipe.getRecipeTreeNode());
            } catch (Exception e) {
                System.out.println("[DataCalculator.calcRecipesItemCostTree] ERROR " + recipe.getName() + ". " + e.getClass().getSimpleName() + ":" + e.getMessage());
                if (recipe.getName().equals("Graphene")) {
                    e.printStackTrace();
                }
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


    //        circ -> Ir,1 ; Co,1
//            -> [Ir out recipes], [Co out recipes]
//              -> 1x<recipeCost> [Ir out recipe1]  <<< es el 1x<recipeCost> para outRecipe1 lo que tengo que almacenar. Por lo que cada item tendrá su arbol único personal con sus totales
//                                                      Hay que guardar los totales con las referencias. Guardar el árbol no tiene sentido, ya puedo recorrer las dependencias con la estructura en memoria
//                                                      En verdad no haría falta guardar nada? tôdo se puede calcular en el momento.

    public static RecipeTreeNode calcRecipeCostTree(Recipe nodeRecipe, double amount, RecipeTreeNode currentNode) throws Exception {
//        System.out.println("[DataCalculator.calcRecipeCostTree] INI (" + recipe.getName() + "," + amount + "," + recipeCostTree + ")");
//        if (recipe.getName().equals("Supersonic Missile Set")) return new RecipeCostTree();

        if (nodeRecipe.getCode().equals("Gr-Sm")) {
            System.out.println("break-point here");
        }

        List<RecipeTreeNode> forkList = new ArrayList<>();

        RecipeTreeNode root = currentNode.getRoot();
        TransputMap itemCost = nodeRecipe.getItemCost();

        Map<String, Recipe> selectedRecipeMap = new HashMap<>();

        for (Map.Entry<String, Double> itemCostEntry : itemCost.entrySet()) {
            Item costItem = Memory.ITEMS.get(itemCostEntry.getKey());

            if (itemCostEntry.getKey().equals("Oil")) {
//                System.out.println("break-point here");
            }
            if (nodeRecipe.getCode().equals("RefRef-Refi") && currentNode.getName().equals("RefRef-Refi") && costItem.getAbb().equals("H")) {
//                System.out.println("br");
            }

            List<String> costRecipeList = costItem.getOutputRecipeList();
            boolean areAlternativeRecipes = costRecipeList.size() > 1;

            RecipeMap costRecipeMap = Memory.getRecipeMap(costRecipeList);
            List<Recipe> recipeLeftList = new ArrayList<>(costRecipeMap.values());

            recipeLeftList = filterNonCircularRecipes(nodeRecipe, currentNode, recipeLeftList);
            recipeLeftList = filterExcludedRecipes(currentNode, recipeLeftList);

            if(areAlternativeRecipes) {
                root.addAlternativeRecipeList(recipeLeftList.stream().map(Recipe::getCode).toList());
            }

            Recipe selectedRecipe = extractAlternativeRecipe(recipeLeftList);
            selectedRecipeMap.put(itemCostEntry.getKey(), selectedRecipe);
            if(root.getAlternativeRecipes() != null && root.getAlternativeRecipes().contains(selectedRecipe.getCode())){
                currentNode.addAlternativeRecipe(selectedRecipe.getCode());
            }

            for (int i = 0; i < recipeLeftList.size(); i++) {
                RecipeTreeNode fork = currentNode.createFork();
                forkList.add(fork);
                RecipeTreeNode forkMain = fork.getMainBranch();
                forkMain.addRecipeExclusion(selectedRecipe.getCode());
            }
        }

        for (Map.Entry<String, Double> itemCostEntry : itemCost.entrySet()) {
            Recipe selectedCostRecipe = selectedRecipeMap.get(itemCostEntry.getKey());

            double inputAmount = amount * itemCostEntry.getValue();
            RecipeTreeNode childNode = new RecipeTreeNode();
            childNode.setCost(new RecipeTreeCost(itemCostEntry.getKey(), inputAmount, selectedCostRecipe.getCode()));
            childNode.generateName();
            childNode.getRecipeHistory().add(selectedCostRecipe.getCode());

            currentNode.addChild(childNode);

            if (!selectedCostRecipe.isSource()) {
                calcRecipeCostTree(selectedCostRecipe, amount * inputAmount, childNode);
            }
        }

        for(RecipeTreeNode fork: forkList){
            calcRecipeCostTree(nodeRecipe, amount, fork);
        }

        return currentNode;
    }

    /**
     * Discards recipes that cause circular loops.
     * Discards same recipe and recipes with the costItem as inputs.
     * Discards recipes in the RecipeTreeNode recipeHistory
     */
    private static List<Recipe> filterNonCircularRecipes(Recipe nodeRecipe, RecipeTreeNode recipeTreeNode, List<Recipe> costRecipeList) {
        ArrayList<Recipe> result = new ArrayList<>();
        for (Recipe costRecipe : costRecipeList) {
            if (costRecipe.getCode().equals(nodeRecipe.getCode())) {
                break;
            }
//            if(nodeRecipe.getOutputs().containsKey(costItem.getAbb())){
//                break;
//            }
            if (recipeTreeNode.getRecipeHistory().contains(costRecipe.getCode())) {
                break;
            }
            result.add(costRecipe);
        }
        return result;
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

    private static Recipe extractAlternativeRecipe(List<Recipe> alternativeRecipeList) {
        Recipe extractedRecipe;
        ArrayList<Recipe> nonSources = new ArrayList<>(alternativeRecipeList.stream().filter(r -> !r.isSource()).toList());
        if (!nonSources.isEmpty()) {
            extractedRecipe = nonSources.getFirst();
        } else {
            extractedRecipe = alternativeRecipeList.getFirst();
        }
        alternativeRecipeList.remove(extractedRecipe);
        return extractedRecipe;
    }

    private static List<Recipe> filterExcludedRecipes(RecipeTreeNode recipeTreeNode, List<Recipe> costRecipeList) {

        RecipeTreeNode main = recipeTreeNode.getMainBranch();
        if(main.getRecipeExclusions() == null){
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


}



