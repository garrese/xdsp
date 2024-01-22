package xis.xdsp;

import xis.xdsp.model.*;
import xis.xdsp.system.Memory;
import xis.xdsp.util.AppUtil;

public class App {

    public static void main(String[] args) throws Exception {
        loadData();
    }

    public static void loadData() throws Exception {
        ItemCsvReader itemCsvReader = new ItemCsvReader();
        RecipeCsvReader recipeCsvReader = new RecipeCsvReader();

        Memory.ITEMS = itemCsvReader.readItemListCsv();
        Memory.RECIPES = recipeCsvReader.readRecipeListCsv();
        Memory.RECIPES.forEach( (recipeCode, recipe) -> {
            recipe.setItemCost(DataCalculator.calcOneItemCost(recipe));
        });
        DataIntegrator.findItemRecipes();

        AppUtil.printMap(Memory.ITEMS);
        AppUtil.printMap(Memory.RECIPES);


        DataIntegrityChecker.checkRecipes();
    }


}
