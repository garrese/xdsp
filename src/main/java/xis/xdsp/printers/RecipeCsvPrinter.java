package xis.xdsp.printers;

import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.TransputMap;
import xis.xdsp.memory.Memory;
import xis.xdsp.printers.formatters.RecipeFormatter;
import xis.xdsp.util.K;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class RecipeCsvPrinter {

    public static void printRecipes(String path) {

//        Path ResourcePath = Paths.get(Objects.requireNonNull(this.getClass().getResource("/")).getPath());

        path = "C:/TMP/";
        File file = new File(path, "generated.csv");

        String fileName = path + "generated_" + System.currentTimeMillis() + ".csv";

        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName, true))) {
            for (Recipe recipe : Memory.getRecipes()) {

                RecipeFormatter recipeFormatter = new RecipeFormatter(recipe);
                String[] itemCostHeadersArray = recipeFormatter.getItemsCostHeadersArray();

                w.write(cells("Recipe",recipe.getName(), recipe.getOutputs().toString(), "Total"));
                w.write(cells(itemCostHeadersArray));
                w.newLine();

                w.write(emtpyCells(1));
                w.write(cells(recipe.getCode(), "Original"));
                w.write(cell(recipe.getRecipeSourcesCost().getTotal()));
                for (String itemCostHeader : itemCostHeadersArray) {
                    Double cost = recipe.getRecipeSourcesCost().get(itemCostHeader);
                    w.write(cost == null ? emtpyCells(1) : cell(cost));
                }
                w.newLine();

                writePrLines(w, itemCostHeadersArray, "Extra", recipe.getRecipeSourcesCostPrExtra());
                writePrLines(w, itemCostHeadersArray, "Speed", recipe.getRecipeSourcesCostPrSpeed());

            }
        } catch (IOException ex) {
            System.out.println("printRecipes error" + ex);
        }

    }

    private static void writePrLines(
            BufferedWriter w,
            String[] itemCostHeadersArray,
            String type,
            Map<String, TransputMap> RecipeSourcesCostPr) throws IOException {

        for (Map.Entry<String, TransputMap> entry : RecipeSourcesCostPr.entrySet()) {
            w.write(emtpyCells(1));
            String prItemKey = entry.getKey();
            TransputMap prTransputMap = entry.getValue();
            w.write(cells(prItemKey, type));
            w.write(cell(prTransputMap.getTotal()));
            for (String itemCostHeader : itemCostHeadersArray) {
                Double cost = prTransputMap.get(itemCostHeader);
                w.write(cost == null ? emtpyCells(1) : cell(cost));
            }
            w.newLine();
        }

    }


    public static String cell(String val) {
        return val + K.CSV_SEPARATOR;
    }

    public static String cell(Double val) {
        return cell(String.format("%1$,.4f", val));
//        return cell(String.format("%.2f", val));
    }

    public static String cells(String... vals) {
        StringBuilder result = new StringBuilder();
        for (String val : vals) {
            result.append(cell(val));
        }
        return result.toString();
    }

    public static String cells(Double... vals) {
        StringBuilder result = new StringBuilder();
        for (Double val : vals) {
            result.append(cell(val));
        }
        return result.toString();
    }

    public static String emtpyCells(int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            result.append(K.CSV_SEPARATOR);
        }
        return result.toString();
    }
}
