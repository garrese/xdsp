package xis.xdsp;

import xis.xdsp.calculators.MemoryCalculator;
import xis.xdsp.memory.Memory;
import xis.xdsp.memory.MemoryLoader;
import xis.xdsp.printers.base.JsonWriter;
import xis.xdsp.printers.csv.ItemCsvWriter;
import xis.xdsp.printers.csv.RecipeCsvWriter;
import xis.xdsp.printers.csv.RfpCsvWriter2;
import xis.xdsp.util.PrintUtil;

import java.util.List;

public class App {

    public static List<String> costHeaderListOrder = List.of("IrO", "CoO", "C", "Crude", "H", "SilO", "TitO", "Stn", "Wat", "Acid", "D", "Oil",
            "Ice", "Stal", "Kim", "FSil", "UMag", "Grat", "Ph", "DfMx", "Neur", "NegSin", "Shard", "Core", "MatRec");

    static String reportsPath = "C:/TMP/";

    static String rawCostsConfigLabel = "Basic";
//    static String rawCostsConfigLabel = "Renewable";

    public static void main(String[] args) throws Exception {

        MemoryLoader.load();

        // PRINT DATA
        PrintUtil.printMap(Memory.getItemsMap());
        PrintUtil.printMap(Memory.getRecipesMap());
        System.out.println(Memory.RAW_ITEM_LIST);
//        printJavaConstants();
        alternativeRecipes();
        alternativeRecipesSelected();

        // CSV WRITERS
//        printItemCsv();
//        new RecipeCsvWriter().writeRecipes("C:/TMP/");
//        new RfpCsvWriter2().writeRecipes(reportsPath, Memory.RFPS.values().stream().toList(), costHeaderListOrder);

        // JSON WRITERS
//        JsonWriter.writeCollection(reportsPath, "Items-" + rawCostsConfigLabel + "-JSON", Memory.getItems());
//        JsonWriter.writeCollection(reportsPath, "Recipes-" + rawCostsConfigLabel + "-JSON", Memory.getRecipes());
//        JsonWriter.writeCollection(reportsPath, "RFPs-" + rawCostsConfigLabel + "-JSON", Memory.getRfps());
//        JsonWriter.writeMap(reportsPath, "RecipeTrees-" + rawCostsConfigLabel + "-JSON", Memory.RECIPE_TREE_NODES);
    }

    public static void printItemCsv() throws Exception {
        ItemCsvWriter printer = new ItemCsvWriter();
        List<String> excludedItems = List.of("Log", "Plant");
        printer.print("C:/TMP/", null, excludedItems);
    }

    public static void alternativeRecipes() {
        System.out.println("========== ALTERNATIVE Recipes ==========");
        Memory.getItems().forEach(i -> {
            if (i.getOutputRecipeList().size() > 1) {
                System.out.println(i.getName() + " " + i.getOutputRecipeList());
            }
        });
    }

    public static void alternativeRecipesSelected() {
        System.out.println("========== SELECTED Alternative Recipe ==========");
        MemoryCalculator.getAltSeqMap().forEach((itemK, recipeAltSeq) -> {
            System.out.println(Memory.getItem(itemK).getName() + "\t" + recipeAltSeq);
        });
    }


}
