package xis.xdsp;

import xis.xdsp.model.*;
import xis.xdsp.util.AppUtil;

public class App {

    public static void main(String[] args) throws Exception {
        readCsvs();
        calc();

        AppUtil.printMap(Memory.getItemsMap());
        AppUtil.printMap(Memory.getRecipesMap());
//        printJavaConstants();
    }

    public static void readCsvs() throws Exception {
        ItemCsvReader itemCsvReader = new ItemCsvReader();
        RecipeCsvReader recipeCsvReader = new RecipeCsvReader();
        Memory.setItems(itemCsvReader.readItemListCsv());
        Memory.setRecipes(recipeCsvReader.readRecipeListCsv());
        DataIntegrityChecker.checkRecipes();
    }

    public static void calc(){
        MemoryCalculator.calcAllItemsRecipes();
        MemoryCalculator.calcAllRecipesOutputCost();
        MemoryCalculator.calcAllRecipesSpraysNeeded();

        MemoryCalculator.calcResourcesCosts();
    }

    public static void printJavaConstants(){
        System.out.println("============== JAVA K ITEMS ===============");
        AppUtil.printItemKeysJavaK();
        System.out.println("============== JAVA K RECIPES ===============");
        AppUtil.printRecipeKeysJavaK();
        System.out.println("============== JAVA K END ===============");
    }


}
