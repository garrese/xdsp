package xis.xdsp;

import xis.xdsp.calculators.RfpCalculator;
import xis.xdsp.dto.RecipeAltSeqMap;
import xis.xdsp.dto.sub.Rfp;
import xis.xdsp.memory.Memory;
import xis.xdsp.memory.MemoryLoader;
import xis.xdsp.printers.RecipeCsvWriter;
import xis.xdsp.printers.RfpCsvWriter;
import xis.xdsp.printers.RfpCsvWriter2;
import xis.xdsp.util.PrintUtil;

import java.util.List;

public class App {

    public static void main(String[] args) throws Exception {
        MemoryLoader.load();

        PrintUtil.printMap(Memory.getItemsMap());
        PrintUtil.printMap(Memory.getRecipesMap());
        System.out.println(Memory.SOURCE_ITEMS);
//        printJavaConstants();
        alternativeRecipes();
        alternativeRecipesSelected();

//        generateRfpCsv2();
    }

    public static void recipeCsvPrinter() {
        RecipeCsvWriter recipeCsvWriter = new RecipeCsvWriter();
        recipeCsvWriter.printRecipes("C:/TMP/");
    }

    public static void generateRfpCsv2() throws Exception {
        List<String> excludedRecipes = List.of("OCr-As(o)");
        List<Rfp> rfpList = RfpCalculator.calcAllRfp(excludedRecipes);
        List<String> costHeaderListOrder = List.of("IrO", "CoO", "C", "Crude", "H", "SilO", "TitO", "Stn", "Wat",
                "Ice", "Stal", "Kim", "FSil", "UMag", "Grat", "Ph", "DfMx", "Neur", "NegSin", "Shard", "Core", "MatRec");
        RfpCsvWriter2 printer = new RfpCsvWriter2();

        printer.printRecipes("C:/TMP/", rfpList, costHeaderListOrder);
        for(Rfp rfp : rfpList){
//            if(rfp.getRecipeKey().equals("D-Frtr")){
//                System.out.println("break-here");
//            }
//            System.out.println(rfp);
        }
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
        MemoryLoader.getAltSeqMap().forEach((itemK,recipeAltSeq) -> {
            System.out.println(Memory.getItem(itemK).getName() + "\t" + recipeAltSeq);
        });
    }


}
