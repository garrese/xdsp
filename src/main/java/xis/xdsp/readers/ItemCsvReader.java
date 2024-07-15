package xis.xdsp.readers;

import xis.xdsp.dto.Item;
import xis.xdsp.dto.ItemMap;
import xis.xdsp.util.AppUtil;
import xis.xdsp.util.CsvReader;
import xis.xdsp.util.PrintUtil;

import java.util.List;

public class ItemCsvReader extends CsvReader {


    public static final String CSV_NAME = "items.csv";

    public static final int LINE_START = 2;
    public static final int COL_TYPE = 0;
    public static final int COL_NAME = 1;
    public static final int COL_ABB = 2;
    public static final int COL_ABBC = 3;
    public static final int COL_RARE = 4;
    public static final int COL_FUEL_TYPE = 5;
    public static final int COL_FUEL_E = 6;
    public static final int COL_FUEL_CHAMBER = 7;

    public ItemMap readItemListCsv() throws Exception {
        List<List<String>> lineList = readCsv(CSV_NAME);
        return readCsvLines(lineList);
    }

    public ItemMap readCsvLines(List<List<String>> lineList) throws Exception {
        ItemMap itemMap = new ItemMap();
        int line = LINE_START;
        try {
            while (line < lineList.size()) {
                List<String> colList = lineList.get(line);
                Item item = readCsvCols(colList);
                AppUtil.securePut(itemMap,item.getAbb(),item);
                line++;
            }
        } catch (Exception e) {
            System.out.println("ERROR: line=" + line);
            PrintUtil.printMap(itemMap);
            throw e;
        }
        return itemMap;
    }

    public Item readCsvCols(List<String> colList) throws Exception {
        Item item = new Item();
        int col = 0;
        String val = null;
        try {
            while (col < colList.size()) {
                val = colList.get(col);
                switch (col) {
                    case COL_TYPE:
                        item.setType(val);
                        break;
                    case COL_NAME:
                        item.setName(val);
                        break;
                    case COL_ABB:
                        item.setAbb(val);
                        break;
                    case COL_ABBC:
                        //not used
                        break;
                    case COL_RARE:
                        item.setRare(parseOneAsBoolean(val));
                        break;
                    case COL_FUEL_TYPE:
                        item.getFuel().setType(val);
                        break;
                    case COL_FUEL_E:
                        item.getFuel().setEnergy(parseDouble(val));
                        break;
                    case COL_FUEL_CHAMBER:
                        item.getFuel().setChamber(parseFormattedPercentage(val));
                        break;
                    default:
                        System.out.println("WARN: col " + col + " not indexed");
                }
                col++;
            }
        } catch (Exception e) {
            System.out.println("ERROR: col=" + col + ", val=" + val);
            throw e;
        }
        return item;
    }


}
