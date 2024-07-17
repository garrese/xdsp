package xis.xdsp.printers.csv;

import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.TransputMap;
import xis.xdsp.memory.Memory;
import xis.xdsp.printers.base.CsvWriter;
import xis.xdsp.printers.formatters.RecipeFormatter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class RecipeCsvWriter extends CsvWriter {

    public void writeRecipes(String path) {

//        Path ResourcePath = Paths.get(Objects.requireNonNull(this.getClass().getResource("/")).getPath());
        String fileName = path + "RecipeCsvPrinter_" + System.currentTimeMillis() + ".csv";

        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName, true))) {
            for (Recipe recipe : Memory.getRecipes()) {

                RecipeFormatter recipeFormatter = new RecipeFormatter(recipe);
                String[] itemCostHeadersArray = recipeFormatter.getItemsCostHeadersArray();

                w.write(cellsString("Recipe",recipe.getName(), recipe.getOutputs().toString(), "Total"));
                w.write(cellsString(itemCostHeadersArray));
                w.newLine();

                w.write(emtpyCells(1));
                w.write(cellsString(recipe.getKey(), "Original"));
                w.write(cellDouble(recipe.getRecipeRawCost().calcTotal()));
                for (String itemCostHeader : itemCostHeadersArray) {
                    Double cost = recipe.getRecipeRawCost().get(itemCostHeader);
                    w.write(cost == null ? emtpyCells(1) : cellDouble(cost));
                }
                w.newLine();

                writePrLines(w, itemCostHeadersArray, "Extra", recipe.getRecipeRawCostPrExtra());
                writePrLines(w, itemCostHeadersArray, "Speed", recipe.getRecipeRawCostPrSpeed());

            }
        } catch (IOException ex) {
            System.out.println("RecipeCsvPrinter error" + ex);
        }

    }

    private void writePrLines(
            BufferedWriter w,
            String[] itemCostHeadersArray,
            String type,
            Map<String, TransputMap> RecipeSourcesCostPr) throws IOException {

        for (Map.Entry<String, TransputMap> entry : RecipeSourcesCostPr.entrySet()) {
            w.write(emtpyCells(1));
            String prItemKey = entry.getKey();
            TransputMap prTransputMap = entry.getValue();
            w.write(cellsString(prItemKey, type));
            w.write(cellDouble(prTransputMap.calcTotal()));
            for (String itemCostHeader : itemCostHeadersArray) {
                Double cost = prTransputMap.get(itemCostHeader);
                w.write(cost == null ? emtpyCells(1) : cellDouble(cost));
            }
            w.newLine();
        }

    }


}
