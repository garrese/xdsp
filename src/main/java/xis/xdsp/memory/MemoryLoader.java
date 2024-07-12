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

        Memory.setProliferators(HardcodedReader.readProliferators());

        MemoryChecker.checkRecipes();
    }

    public static void calc(){
        MemoryCalculator.calcAllItemsRecipes();
        MemoryCalculator.calcAllRecipesOutputCost();
        MemoryCalculator.calcAllRecipesSpraysNeeded();

        MemoryCalculator.calcResourcesCosts();
    }



}
