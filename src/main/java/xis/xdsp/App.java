package xis.xdsp;

import xis.xdsp.calculators.RfpCalculator;
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
//        System.out.println(Memory.SOURCE_ITEMS);
//        printJavaConstants();
//        alternativeRecipes();

        generateRfpCsv2();


    }

    public static void recipeCsvPrinter() {
        RecipeCsvWriter recipeCsvWriter = new RecipeCsvWriter();
        recipeCsvWriter.printRecipes("C:/TMP/");
    }

    public static void generateRfpCsv() throws Exception {
        List<Rfp> rfpList = RfpCalculator.calcAllRfp();
        List<String> costHeaderListOrder = List.of("IrO", "CoO", "C", "Crude", "H", "SilO", "TitO", "Stn", "Wat", "Ice", "Stal", "Kim", "FSil", "UMag", "Grat");
        RfpCsvWriter printer = new RfpCsvWriter();
        printer.printRecipes("C:/TMP/", rfpList, costHeaderListOrder);
//        rfpList.forEach(System.out::println);
    }

    public static void generateRfpCsv2() throws Exception {
        List<Rfp> rfpList = RfpCalculator.calcAllRfp();
        List<String> costHeaderListOrder = List.of("IrO", "CoO", "C", "Crude", "H", "SilO", "TitO", "Stn", "Wat", "Ice", "Stal", "Kim", "FSil", "UMag", "Grat");
        RfpCsvWriter2 printer = new RfpCsvWriter2();
        printer.printRecipes("C:/TMP/", rfpList, costHeaderListOrder);
//        rfpList.forEach(System.out::println);
    }

    public static void alternativeRecipes() {
        Memory.getItems().forEach(i -> {
            if (i.getOutputRecipeList().size() > 1) {
                System.out.println(i.getName() + " " + i.getOutputRecipeList());
            }
        });
    }


}
