package xis.xdsp.printers;

import xis.xdsp.dto.Item;
import xis.xdsp.memory.Memory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ItemCsvWriter extends CsvWriter {

    public void printItems(String path, List<String> costHeaderListOrder, List<String> exclude) throws Exception {
        if (exclude == null) exclude = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String date = df.format(System.currentTimeMillis());
        String fileName = path + "Items-Cheatsheet_" + date + ".csv";
        System.out.println("Generating "+fileName);


        LinkedHashMap test;
        List<Item> itemList = Memory.getItems().stream().toList();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName, true))) {
            for (Item item : itemList) {
                List<String> itemCostHeaderListOrder = costHeaderListOrder;
                if (!exclude.contains(item.getAbb())) {

                    List<String> costHeaderList = new ArrayList<>();
                    if (item.getItemRawCost() != null) {
                        costHeaderList = createOrderedCostHeaderList(item);
                        if (costHeaderListOrder == null) {
                            itemCostHeaderListOrder = new ArrayList<>(costHeaderList);
                        }
                    }

                    writeHeaders(itemCostHeaderListOrder, w, costHeaderList);
                    w.write(cells(
                            item.getName(),
                            item.getAbb(),
                            String.join(",", item.getInputRecipeList()),
                            String.join(",", item.getOutputRecipeList())
                    ));
                    if (itemCostHeaderListOrder != null) {
                        Double positiveTotal = item.getItemRawCost().positiveCopy().calcTotal();
                        w.write(cellNum2d(item.getItemRawCost().calcTotal()));
                        w.write(cellNum2d(positiveTotal));
                        writeRawCosts(itemCostHeaderListOrder, w, item, costHeaderList, positiveTotal);
                    }
                    w.newLine();
                }

            }
        } catch (IOException ex) {
            System.out.println("ItemCsvWriter error" + ex);
        }

    }

    private static List<String> createOrderedCostHeaderList(Item item) {
        List<String> costHeaderList;
        Map<String, Double> itemRawCostMap = item.getItemRawCost();
        itemRawCostMap = itemRawCostMap.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        costHeaderList = new ArrayList<>(itemRawCostMap.keySet().stream().toList());
        Collections.reverse(costHeaderList);
        return costHeaderList;
    }

    private void writeHeaders(List<String> costHeaderListOrder, BufferedWriter w, List<String> costHeaderList) throws Exception {
        w.write(cells("|Name|", "|Key|", "|inputRecipeList|", "|outputRecipeList|", "|TotalBalanceCost|", "|TotalPositiveCost|"));
        writeCostHeaders(costHeaderListOrder, w, costHeaderList);
        w.newLine();
    }

    private void writeCostHeaders(List<String> costHeaderListOrder, BufferedWriter w, List<String> costHeaderList) throws Exception {
        List<String> remainingCostHeaders = new ArrayList<>(costHeaderList);
        for (String costHeader : costHeaderListOrder) {
            if (costHeaderList.contains(costHeader)) {
                remainingCostHeaders.remove(costHeader);
                w.write(cells("[" + costHeader + "]", "[" + costHeader + "%]"));
            }
        }
        if (remainingCostHeaders.size() > 0) {
            System.out.println(costHeaderListOrder);
            System.out.println(costHeaderList);
            throw new Exception("Missing headers: " + remainingCostHeaders);
        }
    }

    private void writeRawCosts(List<String> costHeaderListOrder, BufferedWriter w, Item item, List<String> costHeaderList, Double positiveTotal) throws Exception {
        for (String costHeader : costHeaderListOrder) {
            if (costHeaderList.contains(costHeader)) {
                Double itemCostVal = item.getItemRawCost().get(costHeader);
                w.write(cellNum2d(itemCostVal));

                if (positiveTotal > 0) {
                    Double percentage = itemCostVal / positiveTotal;
                    w.write(cellPerc1d(percentage));
                } else {
                    w.write(emtpyCells(1));
                }
            }
        }
    }


}

