package xis.xdsp;

import xis.xdsp.calculators.MemoryCalculator;
import xis.xdsp.calculators.RfpCalculator;
import xis.xdsp.dto.sub.Rfp;
import xis.xdsp.memory.Memory;
import xis.xdsp.memory.MemoryLoader;
import xis.xdsp.printers.ItemCsvWriter;
import xis.xdsp.printers.RecipeCsvWriter;
import xis.xdsp.printers.RfpCsvWriter2;
import xis.xdsp.util.PrintUtil;

import java.util.List;

public class App {

    public static List<String> costHeaderListOrder = List.of("IrO", "CoO", "C", "Crude", "H", "SilO", "TitO", "Stn", "Wat", "Acid", "D", "Oil",
            "Ice", "Stal", "Kim", "FSil", "UMag", "Grat", "Ph", "DfMx", "Neur", "NegSin", "Shard", "Core", "MatRec");

    public static void main(String[] args) throws Exception {
        MemoryLoader.load();

        PrintUtil.printMap(Memory.getItemsMap());
        PrintUtil.printMap(Memory.getRecipesMap());
        System.out.println(Memory.RAW_ITEM_LIST);
//        printJavaConstants();
        alternativeRecipes();
        alternativeRecipesSelected();

        generateItemCsv();
//        generateRfpCsv2();
    }

    public static void recipeCsvPrinter() {
        RecipeCsvWriter recipeCsvWriter = new RecipeCsvWriter();
        recipeCsvWriter.printRecipes("C:/TMP/");
    }

    public static void generateRfpCsv2() throws Exception {
        List<String> excludedRecipes = List.of("OCr-As(o)","Core-Drop","MatRec-Drop","NegSin-Drop","Shard-Drop","Neur-Drop","DfMx-Drop","Ph-RR");
        List<Rfp> rfpList = RfpCalculator.calcAllRfp(excludedRecipes, true);

        RfpCsvWriter2 printer = new RfpCsvWriter2();
        printer.printRecipes("C:/TMP/", rfpList, costHeaderListOrder);
    }

    public static void generateItemCsv() throws Exception {
        ItemCsvWriter printer = new ItemCsvWriter();
        List<String> excludedItems = List.of("Log", "Plant");
        printer.printItems("C:/TMP/", null, excludedItems);
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
