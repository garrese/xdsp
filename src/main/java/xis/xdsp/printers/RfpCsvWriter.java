package xis.xdsp.printers;

import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.TransputMap;
import xis.xdsp.dto.sub.*;
import xis.xdsp.memory.Memory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RfpCsvWriter extends CsvWriter {

    public static int INPUT_HEADERS_RESERVED = 6;
    public static int OUTPUT_HEADERS_RESERVED = 2;

    //ordenar por recipe totalCost > Name > factoryname > PrMode > PrKey

    public void printRecipes(String path, List<Rfp> rfpList, List<String> costHeaderList) throws Exception {
        String fileName = path + "RFP_" + System.currentTimeMillis() + ".csv";

        rfpList = rfpList.stream().sorted(Comparator.comparing(Rfp::getItemOrder)
                .thenComparing(Rfp::getFactoryOrder)
                .thenComparing(Rfp::getProliferatorModeOrder)
        ).toList();

        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName, true))) {
            writeHeaderLine(costHeaderList, w);
            for (Rfp rfp : rfpList) {
                writeRfpLine(costHeaderList, w, rfp);
            }
        } catch (IOException ex) {
            System.out.println("RfpCsvWriter error" + ex);
        }

    }

    private void writeHeaderLine(List<String> costHeaderList, BufferedWriter w) throws IOException {
        w.write(cells("Recipe Name", "Fac Name", "Fac Type", "Pr Key", "Pr Mode", "Energy", "Time", "Total Cost"));
        for (String costHeader : costHeaderList) {
            w.write(cells(costHeader));
            w.write(cells(costHeader + " %Tot"));
            w.write(cells(costHeader + " %NoPr"));
        }

        for (int i = 0; i < OUTPUT_HEADERS_RESERVED; i++) {
            w.write(cells("Out" + i));
            w.write(cells("Out" + i + " Qty"));
            w.write(cells("Out" + i + " Speed"));
        }

        for (int i = 0; i < INPUT_HEADERS_RESERVED; i++) {
            w.write(cells("In" + i));
            w.write(cells("In" + i + " Qty"));
            w.write(cells("In" + i + " Speed"));
        }
        w.newLine();
    }

    private void writeRfpLine(List<String> costHeaderList, BufferedWriter w, Rfp rfp) throws Exception {
        Recipe recipe = Memory.getRecipe(rfp.getRecipeKey());
        Factory factory = Memory.getFactory(rfp.getFactoryTypeKey(), rfp.getFactoryItemKey());
        Proliferator proliferator = Memory.getProliferator(rfp.getProliferatorKey());

        w.write(cells(
                recipe.getName(),
                factory.getName(),
                factory.getTypeKey(),
                proliferator == null ? "" : proliferator.getItemKey(),
                rfp.getProliferatorMode() == null ? "Normal" : rfp.getProliferatorMode().toString(),
                rfp.getEnergy(),
                rfp.getTime(),
                rfp.getRawCostTotal()
        ));
        writeRawCosts(costHeaderList, w, rfp);

        int inputSize = rfp.getInputStatsMap().size();
        int outputSize = rfp.getOutputStatsMap().size();
        if (inputSize > INPUT_HEADERS_RESERVED || outputSize > OUTPUT_HEADERS_RESERVED) {
            throw new Exception("Not enough input(" + inputSize + ")/outputs(" + outputSize + ") headers reserved! :" + rfp);
        }
        writeTransputStats(w, rfp.getOutputStatsMap(), OUTPUT_HEADERS_RESERVED);
        writeTransputStats(w, rfp.getInputStatsMap(), INPUT_HEADERS_RESERVED);

        w.newLine();
    }

    private void writeRawCosts(List<String> costHeaderList, BufferedWriter w, Rfp rfp) throws Exception {
        if (rfp.getRawCostMap() != null) {
            List<String> remaining = new ArrayList<>(rfp.getRawCostMap().keySet());
            for (String itemKey : costHeaderList) {
                Double cost = rfp.getRawCostMap().get(itemKey);
                Double percOfTotal = rfp.getRawCostMapPercetageOfTotal().get(itemKey);

                TransputMap rawCostMapPercetageOfNoPr = rfp.getRawCostMapPercetageOfNoPr();
                Double percOfNoPr = rawCostMapPercetageOfNoPr == null ? null : rfp.getRawCostMapPercetageOfNoPr().get(itemKey);

                w.write(cells(cost, percOfTotal, percOfNoPr));
                if (cost != null) {
                    remaining.remove(itemKey);
                }
            }
            if (remaining.size() > 0) {
                throw new Exception("Missing headers: " + remaining);
            }
        }
    }

    private void writeTransputStats(BufferedWriter w, TransputStatsMap transputStatsMap, int headersReserved) throws IOException {
        int headersForStats = 3;
        for (TransputStats transputStats : transputStatsMap.values()) {
            w.write(cells(transputStats.getItemK()));
            w.write(cells(transputStats.getQuantity()));
            w.write(cells(transputStats.getItemsPerSecond()));
        }
        int emptyTransputs = headersReserved - transputStatsMap.values().size();
        if (emptyTransputs > 0) w.write(emtpyCells(emptyTransputs * headersForStats));
    }
}

