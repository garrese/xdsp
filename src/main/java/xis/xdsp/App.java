package xis.xdsp;

import xis.xdsp.model.DataIntegrityChecker;
import xis.xdsp.model.ItemCsvReader;
import xis.xdsp.model.RecipeCsvReader;
import xis.xdsp.system.Memory;
import xis.xdsp.util.AppUtil;

public class App {

    public static void main(String[] args) throws Exception {
        ItemCsvReader itemCsvReader = new ItemCsvReader();
        RecipeCsvReader recipeCsvReader = new RecipeCsvReader();

        Memory.ITEMS = itemCsvReader.readItemListCsv();
        Memory.RECIPES = recipeCsvReader.readRecipeListCsv();
        AppUtil.printMap(Memory.ITEMS);
        AppUtil.printMap(Memory.RECIPES);

        DataIntegrityChecker.checkRecipes();

    }


}
