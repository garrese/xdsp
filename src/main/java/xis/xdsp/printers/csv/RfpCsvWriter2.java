package xis.xdsp.printers.csv;

import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.TransputMap;
import xis.xdsp.dto.sub.*;
import xis.xdsp.memory.Memory;
import xis.xdsp.printers.base.CsvWriter;
import xis.xdsp.util.Debug;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RfpCsvWriter2 extends CsvWriter {

    public void writeRecipes(String path, List<Rfp> rfpList, List<String> costHeaderListOrder) throws Exception {
        String fileName = path + "RFP2_" + System.currentTimeMillis() + ".csv";

        rfpList = rfpList.stream().sorted(Comparator.comparing(Rfp::getItemOrder)
                .thenComparing(Rfp::getFactoryOrder)
                .thenComparing(Rfp::getProliferatorModeOrder)
        ).toList();

        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName, true))) {
            String previousZone = null;
            List<String> costHeaderList = null;
            for (Rfp rfp : rfpList) {
                Recipe recipe = Memory.getRecipe(rfp.getRecipeKey());
                Factory factory = Memory.getFactory(rfp.getFactoryTypeKey(), rfp.getFactoryItemKey());
                Proliferator proliferator = Memory.getProliferator(rfp.getProliferatorKey());

                String currentZone = recipe.getKey() + "_" + factory.getItemKey();
                if (!currentZone.equals(previousZone)) {
                    costHeaderList = calcCostHeaderList(recipe);
                    writeHeaders(costHeaderListOrder, w, rfp, costHeaderList);
                }
                previousZone = currentZone;

                w.write(cells(
                        recipe.getName(),
                        factory.getName(),
                        factory.getTypeKey(),
                        proliferator == null ? "" : proliferator.getItemKey(),
                        rfp.getProliferatorMode() == null ? "Normal" : rfp.getProliferatorMode().toString()
                ));
                w.write(cellNum0d(rfp.getEnergy()));
                w.write(cellNum3d(rfp.getTime()));
                w.write(cellNum2d(rfp.getRawCostTotal()));


                writeRawCosts(w, rfp, costHeaderList, costHeaderListOrder);
                writeTransputStats(w, rfp.getInputStatsMap());
                writeTransputStats(w, rfp.getOutputStatsMap());
                w.newLine();

            }
        } catch (IOException ex) {
            System.out.println("RfpCsvWriter error" + ex);
        }

    }

    private void writeHeaders(List<String> costHeaderListOrder, BufferedWriter w, Rfp rfp, List<String> costHeaderList) throws Exception {
        w.write(cells("|Recipe|", "|F|", "|FType|", "|Pr|", "|PrMode|", "|Energy|", "|Time|", "|TotCost| "));
        writeCostHeaders(costHeaderListOrder, w, rfp, costHeaderList);
        writeTransputsHeaders(w, rfp.getInputStatsMap(), "In");
        writeTransputsHeaders(w, rfp.getOutputStatsMap(), "Out");
        w.newLine();
    }

    private void writeCostHeaders(List<String> costHeaderListOrder, BufferedWriter w, Rfp rfp, List<String> costHeaderList) throws Exception {
        if (rfp.getRecipeKey().equals("PlasRef-Refi")) {
            Debug.point();
        }
        if (costHeaderList != null) { //need recipe with PR cost headers, it doesn't matter if Extra or Speed
            List<String> remainingCostHeaders = new ArrayList<>(costHeaderList);
            for (String costHeader : costHeaderListOrder) {
                if (costHeaderList.contains(costHeader)) {
                    remainingCostHeaders.remove(costHeader);
                    w.write(cells("[" + costHeader + "]", "[" + costHeader + "%T]", "[" + costHeader + "%D]"));
                }
            }
            if (remainingCostHeaders.size() > 0) {
                throw new Exception("Missing headers: " + remainingCostHeaders);
            }
        }
    }

    private void writeTransputsHeaders(BufferedWriter w, TransputStatsMap transputStatsMap, String prefix) throws IOException {
        int transputN = 1;
        for (TransputStats transputStats : transputStatsMap.values()) {

//            String header = "[" + prefix + transputN + "-" + transputStats.getItemK() + "]";
//            String speedHeader = "[" + transputN + prefix + "#S" + "]";
//            w.write(cells(header, speedHeader));

            String speedHeader = "[" + prefix + transputN + "-" + transputStats.getItemK() + "/s]";
            w.write(cells(speedHeader));

            transputN++;
        }
    }


    private void writeRawCosts(BufferedWriter w, Rfp rfp, List<String> costHeaderList, List<String> costHeaderListOrder) throws Exception {
        if (rfp.getRecipeKey().equals("PlasRef-Refi")) {
            Debug.point();
        }
        for (String itemKey : costHeaderListOrder) {
            boolean rawCostWritten = false;
            if (rfp.getRawCostMap() != null && rfp.getRawCostMap().get(itemKey) != null) {
                Double itemCost = rfp.getRawCostMap().get(itemKey);
                TransputMap rcmpot = rfp.getRawCostMapPercetageOfTotal();
                TransputMap rcmponp = rfp.getRawCostMapPercetageOfNoPr();

                Double percOfTotal = null;
                if (rcmpot != null) {
                    percOfTotal = rfp.getRawCostMapPercetageOfTotal().get(itemKey);
                }

                Double costDiff = null;
                if (rcmponp != null && rcmponp.get(itemKey) != null) {
                    costDiff = (1 - rfp.getRawCostMapPercetageOfNoPr().get(itemKey)) * -1;
                }

                w.write(cellNum3d(itemCost));
                w.write(cellPerc1d(percOfTotal));
                w.write(cellPerc1d(costDiff));
                rawCostWritten = true;
            }

            if (!rawCostWritten && costHeaderList.contains(itemKey)) {
                w.write(emtpyCells(3));
            }

        }
    }

    private void writeTransputStats(BufferedWriter w, TransputStatsMap transputStatsMap) throws IOException {
        for (TransputStats transputStats : transputStatsMap.values()) {
//            w.write(cellNum3d(transputStats.getQuantity()));
            w.write(cellNum3d(transputStats.getItemsPerSecond()));
        }
    }

    private List<String> calcCostHeaderList(Recipe recipe) {
        TransputMap prTransputSum = new TransputMap();
        for (TransputMap transputMap : recipe.getRecipeRawCostPrExtra().values()) {
            prTransputSum.sumTransputMap(transputMap);
        }
        return new ArrayList<>(prTransputSum.keySet());
    }
}

