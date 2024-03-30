package xis.xdsp;

import xis.xdsp.dto.RecipeTreeCost;
import xis.xdsp.model.*;
import xis.xdsp.system.Memory;
import xis.xdsp.util.AppUtil;
import xis.xdsp.util.ItemK;
import xis.xdsp.util.RecipeK;

public class App {

    public static void main(String[] args) throws Exception {
        readCsvs();
        calc();

        AppUtil.printMap(Memory.ITEMS);
        AppUtil.printMap(Memory.RECIPES);
//        printJavaConstants();
    }

    public static void readCsvs() throws Exception {
        ItemCsvReader itemCsvReader = new ItemCsvReader();
        RecipeCsvReader recipeCsvReader = new RecipeCsvReader();
        Memory.ITEMS = itemCsvReader.readItemListCsv();
        Memory.RECIPES = recipeCsvReader.readRecipeListCsv();
        DataIntegrityChecker.checkRecipes();
    }

    public static void calc(){
        DataCalculator.calcItemsRecipes();
        DataCalculator.calcRecipesItemCost();

        DataCalculator.calcResourcesCosts();
    }

    public static void printJavaConstants(){
        System.out.println("============== JAVA K ITEMS ===============");
        AppUtil.printItemKeysJavaK();
        System.out.println("============== JAVA K RECIPES ===============");
        AppUtil.printRecipeKeysJavaK();
        System.out.println("============== JAVA K END ===============");
    }


}
