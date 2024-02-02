package xis.xdsp.model;

import xis.xdsp.dto.*;
import xis.xdsp.system.Memory;

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
            result.put(ingredient.getKey(), ingredientRatio);
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


            RecipeTreeItem rootItem = new RecipeTreeItem();
            rootItem.setItem(new RecipeTreeItemCost(null, null, "root"));

            RecipeTreeItem recipeItem = new RecipeTreeItem();
            recipeItem.setItem(new RecipeTreeItemCost(null, 1d, recipe.getCode()));
            rootItem.addChild(recipeItem);

            calcRecipeCostTree(recipe, 1, recipeItem);

            recipe.setRecipeTreeItem(rootItem);

            System.out.println("[DataCalculator.calcRecipesItemCostTree] FIN " + recipe.getName() + " => " + recipe.getRecipeTreeItem());
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

    public static RecipeTreeItem calcRecipeCostTree(Recipe recipe, double amount, RecipeTreeItem recipeTreeItem) {
//        System.out.println("[DataCalculator.calcRecipeCostTree] INI (" + recipe.getName() + "," + amount + "," + recipeCostTree + ")");
//        if (recipe.getName().equals("Supersonic Missile Set")) return new RecipeCostTree();

        if(recipe.getCode().equals("Gr-Sm")){
            System.out.println("break-point here");
        }

        //TODO las ramas nuevas tienen que tener key de child diferente
        //TODO hay que copiar las ramas nuevas antes de añadir cada variante

        TransputMap itemCost = recipe.getItemCost();
        for (Map.Entry<String, Double> inputCost : itemCost.entrySet()) {
            Item item = Memory.ITEMS.get(inputCost.getKey());

            int alternativeRecipe = 0;
            for (String outputRecipeKey : item.getOutputRecipeList()) {
                if (outputRecipeKey.equals("PlasRef-Refi")) break;
                Recipe outputRecipe = Memory.RECIPES.get(outputRecipeKey);
                double inputAmount = amount * inputCost.getValue();

                RecipeTreeItem outputRecipeTreeItem = new RecipeTreeItem();
                outputRecipeTreeItem.setTreeMapKeys(new ArrayList<>(recipeTreeItem.getTreeMapKeys()));
                outputRecipeTreeItem.getTreeMapKeys().add(outputRecipeKey);
                outputRecipeTreeItem.setItem(new RecipeTreeItemCost(inputCost.getKey(), inputAmount, outputRecipe.getCode()));

                if (alternativeRecipe == 0) {
                    recipeTreeItem.addChild(outputRecipeTreeItem);
                } else {
                    RecipeTreeItem thisMainBranch = recipeTreeItem.getThisMainBranch();
                    RecipeTreeItem thisMainBranchCopy = thisMainBranch.getCopy();
                    thisMainBranchCopy.getForkList().add(outputRecipeKey);
                    thisMainBranchCopy.addChild(outputRecipeTreeItem);
                    recipeTreeItem.getRoot().addChild(thisMainBranchCopy);

                    RecipeTreeItem.navigate(thisMainBranchCopy,outputRecipeTreeItem.getTreeMapKeys());
                }

                if (!outputRecipe.isSource()) {
                    calcRecipeCostTree(outputRecipe, amount * inputAmount, outputRecipeTreeItem);
                }
//                System.out.println("outputRecipeKey=" + outputRecipeKey + ", recipeCostTree.getCost=" + recipeCostTree.getCost());
                alternativeRecipe++;
            }
        }
        return recipeTreeItem;
    }

}
