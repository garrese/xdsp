package xis.xdsp.model;

import xis.xdsp.dto.*;
import xis.xdsp.system.Memory;
import xis.xdsp.util.AppUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

                RecipeTreeItem rootItem = new RecipeTreeItem();
                rootItem.setItem(new RecipeTreeItemCost(null, null, "root"));

                RecipeTreeItem recipeItem = new RecipeTreeItem();
                recipeItem.setItem(new RecipeTreeItemCost(null, 1d, recipe.getCode()));
                rootItem.addChild(recipeItem);

                calcRecipeCostTree(recipe, 1, recipeItem);

                recipe.setRecipeTreeItem(rootItem);

                System.out.println("[DataCalculator.calcRecipesItemCostTree] FIN " + recipe.getName() + " => " + recipe.getRecipeTreeItem());
            } catch (Exception e) {
                System.out.println("[DataCalculator.calcRecipesItemCostTree] ERROR " + recipe.getName() + ". " + e.getClass().getSimpleName() + ":" + e.getMessage());
//                e.printStackTrace();
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

    public static RecipeTreeItem calcRecipeCostTree(Recipe recipe, double amount, RecipeTreeItem recipeTreeItem) throws Exception {
//        System.out.println("[DataCalculator.calcRecipeCostTree] INI (" + recipe.getName() + "," + amount + "," + recipeCostTree + ")");
//        if (recipe.getName().equals("Supersonic Missile Set")) return new RecipeCostTree();

        if (recipe.getCode().equals("Gr-Sm")) {
            System.out.println("break-point here");
        }


        RecipeTreeItem root = recipeTreeItem.getRoot();
        TransputMap itemCost = recipe.getItemCost();
        for (Map.Entry<String, Double> inputCost : itemCost.entrySet()) {
            Item item = Memory.ITEMS.get(inputCost.getKey());

            List<String> outputRecipeList = item.getOutputRecipeList();
            List<RecipeTreeItem> branches = new ArrayList<>();
            branches.add(recipeTreeItem);
            if (outputRecipeList.size() > 1) {

                for (int i = 1; i < outputRecipeList.size(); i++) {
                    RecipeTreeItem alternativeBranch = recipeTreeItem.createFork(outputRecipeList.get(i));
                    branches.add(alternativeBranch);
                }
            }

            int alternativeRecipe = 0;
//            for (String outputRecipeKey : item.getOutputRecipeList()) {
            for (int i = 0; i < outputRecipeList.size(); i++) {
//                if (outputRecipeKey.equals("PlasRef-Refi")) break;
                Recipe outputRecipe = Memory.RECIPES.get(outputRecipeList.get(i));
                double inputAmount = amount * inputCost.getValue();

                RecipeTreeItem outputRecipeTreeItem = new RecipeTreeItem();
//                outputRecipeTreeItem.setTreeMapPath(new ArrayList<>(recipeTreeItem.getTreeMapPath()));
//                outputRecipeTreeItem.getTreeMapPath().add(outputRecipeList.get(i));
                outputRecipeTreeItem.setItem(new RecipeTreeItemCost(inputCost.getKey(), inputAmount, outputRecipe.getCode()));

//                root.getChildMap().get()
                branches.get(i).addChild(outputRecipeTreeItem);

                if (i == 0) {
//                    recipeTreeItem.addChild(outputRecipeTreeItem);
                } else {
                    System.out.println("break-point here");
//                    RecipeTreeItem thisMainBranchCopy = recipeTreeItem.createFork();


                }

                if (!outputRecipe.isSource()) {
                    calcRecipeCostTree(outputRecipe, amount * inputAmount, outputRecipeTreeItem);
                }
//                System.out.println("outputRecipeKey=" + outputRecipeKey + ", recipeCostTree.getCost=" + recipeCostTree.getCost());
            }
        }

        return recipeTreeItem;
    }



}



