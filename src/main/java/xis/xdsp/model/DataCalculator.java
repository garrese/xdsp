package xis.xdsp.model;

import xis.xdsp.dto.*;
import xis.xdsp.system.Memory;
import xis.xdsp.util.AppUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                mainBranchNode.setName(composeMainBranchName(root,mainBranchNode));
                mainBranchNode.setTags(new ArrayList<>());
                root.addChild(mainBranchNode);

                calcRecipeCostTree(recipe, 1, mainBranchNode);

                recipe.setRecipeTreeNode(root);

                System.out.println("[DataCalculator.calcRecipesItemCostTree] FIN " + recipe.getName() + " => " + recipe.getRecipeTreeNode());
            } catch (Exception e) {
                System.out.println("[DataCalculator.calcRecipesItemCostTree] ERROR " + recipe.getName() + ". " + e.getClass().getSimpleName() + ":" + e.getMessage());
                if(recipe.getName().equals("Graphene")) {
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

    public static RecipeTreeNode calcRecipeCostTree(Recipe nodeRecipe, double amount, RecipeTreeNode recipeTreeNode) throws Exception {
//        System.out.println("[DataCalculator.calcRecipeCostTree] INI (" + recipe.getName() + "," + amount + "," + recipeCostTree + ")");
//        if (recipe.getName().equals("Supersonic Missile Set")) return new RecipeCostTree();

        if (nodeRecipe.getCode().equals("Gr-Sm")) {
//            System.out.println("break-point here");
        }

        RecipeTreeNode root = recipeTreeNode.getRoot();
        TransputMap itemCost = nodeRecipe.getItemCost();
        for (Map.Entry<String, Double> inputCost : itemCost.entrySet()) {
            Item costItem = Memory.ITEMS.get(inputCost.getKey());

            if(inputCost.getKey().equals("Oil")){
//                System.out.println("break-point here");
            }

            if(nodeRecipe.getCode().equals("RefRef-Refi")&&recipeTreeNode.getName().equals("RefRef-Refi")&&costItem.getAbb().equals("H")){
//                System.out.println("br");
            }

            List<Recipe> costRecipeList = filterNonCircularRecipes(nodeRecipe, recipeTreeNode, costItem);

            List<RecipeTreeNode> branches = new ArrayList<>();
            branches.add(recipeTreeNode);
            boolean areAlternativeRecipes = costRecipeList.size() > 1;
            if (areAlternativeRecipes) {

                for (int i = 1; i < costRecipeList.size(); i++) {
                    RecipeTreeNode alternativeBranch = recipeTreeNode.createFork(costRecipeList.get(i).getCode());
                    branches.add(alternativeBranch);
                }
            }

            for (int i = 0; i < costRecipeList.size(); i++) {
                RecipeTreeNode workingBranch = branches.get(i);
                Recipe costRecipe = costRecipeList.get(i);

                if (areAlternativeRecipes) {
                    workingBranch.getThisMainBranch().getTags().add(costRecipe.getCode());
                }

                double inputAmount = amount * inputCost.getValue();

                RecipeTreeNode outputRecipeTreeNode = new RecipeTreeNode();
                outputRecipeTreeNode.setCost(new RecipeTreeCost(inputCost.getKey(), inputAmount, costRecipe.getCode()));
                outputRecipeTreeNode.generateName();
                outputRecipeTreeNode.getRecipeHistory().add(costRecipe.getCode());

//                try {
                    workingBranch.addChild(outputRecipeTreeNode);
//                }catch (Exception e){
//                    System.out.println("br");
//                }

                if (!costRecipe.isSource()) {
                    calcRecipeCostTree(costRecipe, amount * inputAmount, outputRecipeTreeNode);
                }
            }
        }

        return recipeTreeNode;
    }

    /**
     * Discards recipes that cause circular loops.
     * Discards same recipe and recipes with the costItem as inputs.
     * Discards recipes in the RecipeTreeNode recipeHistory
     */
    private static List<Recipe> filterNonCircularRecipes(Recipe nodeRecipe, RecipeTreeNode recipeTreeNode, Item costItem) {
        ArrayList<Recipe> result = new ArrayList<>();
        List<Recipe> costRecipeList = costItem.getOutputRecipeList().stream().map(key -> Memory.RECIPES.get(key)).toList();
        for(Recipe costRecipe : costRecipeList){
            if(costRecipe.getCode().equals(nodeRecipe.getCode())){
                break;
            }
//            if(nodeRecipe.getOutputs().containsKey(costItem.getAbb())){
//                break;
//            }
            if(recipeTreeNode.getRecipeHistory().contains(costRecipe.getCode())){
                break;
            }
            result.add(costRecipe);
        }
        return result;
    }


}



