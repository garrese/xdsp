package xis.xdsp.memory;

import xis.xdsp.calculators.MemoryCalculator;
import xis.xdsp.dto.RecipeAltSeqMap;
import xis.xdsp.readers.HardcodedReader;
import xis.xdsp.readers.ItemCsvReader;
import xis.xdsp.readers.RecipeCsvReader;
import xis.xdsp.util.ItemK;
import xis.xdsp.util.RecipeK;

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

        MemoryCalculator.calcRecipesRawCosts(getAltSeqMap());
        MemoryCalculator.calcAllRecipesSpraysRawCost();
        MemoryCalculator.calcAllRecipeRawCostPrSpeed();
        MemoryCalculator.calcAllRecipeRawCostPrExtra();

        MemoryCalculator.calcSourceItems();
    }

    public static RecipeAltSeqMap getAltSeqMap(){
        RecipeAltSeqMap recipeAltSeqMap = new RecipeAltSeqMap();
        recipeAltSeqMap.put(ItemK.EGr, RecipeK.EGr_Sm);
        recipeAltSeqMap.put(ItemK.Acid, RecipeK.Acid_Chem);
        recipeAltSeqMap.put(ItemK.Oil, RecipeK.PlasRef_Refi);
        recipeAltSeqMap.put(ItemK.Dmd, RecipeK.Dmd_Sm);
        recipeAltSeqMap.put(ItemK.SilO, RecipeK.SilO_Mim);
        recipeAltSeqMap.put(ItemK.OCr, RecipeK.OCr_Chem);
        recipeAltSeqMap.put(ItemK.Gr, RecipeK.Gr_Sm);
        recipeAltSeqMap.put(ItemK.Cont, RecipeK.Cont_As);
        recipeAltSeqMap.put(ItemK.H, RecipeK.PlasRef_Refi);
        recipeAltSeqMap.put(ItemK.NTube, RecipeK.NTube_Chem);
        recipeAltSeqMap.put(ItemK.Cas, RecipeK.Cas_As);
        recipeAltSeqMap.put(ItemK.PhC, RecipeK.PhC_As);
        recipeAltSeqMap.put(ItemK.D, RecipeK.D_Frtr);
        recipeAltSeqMap.put(ItemK.CrSil, RecipeK.CrSil_Sm);
        return recipeAltSeqMap;
    }



}
