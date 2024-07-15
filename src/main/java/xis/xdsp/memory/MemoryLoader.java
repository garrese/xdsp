package xis.xdsp.memory;

import xis.xdsp.calculators.MemoryCalculator;
import xis.xdsp.readers.HardcodedReader;
import xis.xdsp.readers.ItemCsvReader;
import xis.xdsp.readers.RecipeCsvReader;

public class MemoryLoader {


    public static void load() throws Exception {
        read();
        calc();
    }

    public static void read() throws Exception {
        ItemCsvReader itemCsvReader = new ItemCsvReader();
        Memory.setItems(itemCsvReader.readItemListCsv());

        RecipeCsvReader recipeCsvReader = new RecipeCsvReader();
        Memory.setRecipes(recipeCsvReader.readRecipeListCsv());

        MemoryChecker.checkRecipes();

        Memory.PROLIFERATORS = HardcodedReader.readProliferatorsMap();
        Memory.FACTORIES = HardcodedReader.readFactoriesMap();

    }

    public static void calc(){
        MemoryCalculator.calcAllItemsRecipes();
        MemoryCalculator.calcAllRecipesOutputCost();
        MemoryCalculator.calcAllRecipesSpraysNeeded();

        MemoryCalculator.calcRecipesRawCosts();
        MemoryCalculator.calcAllRecipesSpraysRawCost();
        MemoryCalculator.calcAllRecipeRawCostPrSpeed();
        MemoryCalculator.calcAllRecipeRawCostPrExtra();

        MemoryCalculator.calcSourceItems();
    }



}
