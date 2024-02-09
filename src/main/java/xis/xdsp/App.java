package xis.xdsp;

import xis.xdsp.dto.RecipeTreeCost;
import xis.xdsp.model.*;
import xis.xdsp.system.Memory;
import xis.xdsp.util.AppUtil;

public class App {

    public static void main(String[] args) throws Exception {


        RecipeTreeCost chip = new RecipeTreeCost();


//        circ -> Ir,1 ; Co,1
//            -> [Ir out recipes], [Co out recipes]
//              -> 1x<recipeCost> [Ir out recipe1]  <<< es el 1x<recipeCost> para outRecipe1 lo que tengo que almacenar. Por lo que cada item tendrá su arbol único personal con sus totales
//                                                      Hay que guardar los totales con las referencias. Guardar el árbol no tiene sentido, ya puedo recorrer las dependencias con la estructura en memoria
//                                                      En verdad no haría falta guardar nada? tôdo se puede calcular en el momento.
//        RecipeCost
//        LinkedHashMap<String,ItemCost> recipeCost;
//
//        ItemCost iron = new ItemCost();
//            iron.setItemsRequired(1d);
//            iron.getItemCostMap().put("IrO-Mim",null);
//            iron.getItemCostMap().put("IrO-Creative",null);
//
//
//        ItemCost co = new ItemCost();
//            co.setItemsRequired(1d);
//            co.getItemCostMap().put("CoO-Mim",null);
//
//        System.out.println(iron);

        loadData();
    }

    public static void loadData() throws Exception {
        ItemCsvReader itemCsvReader = new ItemCsvReader();
        RecipeCsvReader recipeCsvReader = new RecipeCsvReader();

        Memory.ITEMS = itemCsvReader.readItemListCsv();
        Memory.RECIPES = recipeCsvReader.readRecipeListCsv();

        DataCalculator.calcItemsRecipes();
        DataCalculator.calcRecipesItemCost();
        DataCalculator.calcRecipesItemCostTree();

        AppUtil.printMap(Memory.ITEMS);
        AppUtil.printMap(Memory.RECIPES);

        DataIntegrityChecker.checkRecipes();


    }


}
