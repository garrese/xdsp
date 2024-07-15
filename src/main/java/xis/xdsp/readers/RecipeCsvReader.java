package xis.xdsp.readers;

import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.RecipeMap;
import xis.xdsp.dto.TransputMap;
import xis.xdsp.dto.sub.RecipeOrdinations;
import xis.xdsp.util.AppUtil;
import xis.xdsp.util.CsvReader;
import xis.xdsp.util.PrintUtil;

import java.text.ParseException;
import java.util.List;

import static xis.xdsp.util.AppUtil.isBlank;

public class RecipeCsvReader extends CsvReader {


    public static final String CSV_NAME = "recipes.csv";

    public static final int LINE_START = 2;

    public static final int COL_ORDER = 0;
    public static final int COL_REPLICATOR_ORDER = 1;
    public static final int COL_REPLICATOR_TAB = 2;
    public static final int COL_REPLICATOR_ROW = 3;
    public static final int COL_REPLICATOR_COL = 4;

    public static final int COL_NAME = 5;
    public static final int COL_WITH = 6;
    public static final int COL_CODE = 7;

    public static final int COL_OUT1_ABB = 8;
    public static final int COL_OUT1_QTY = 9;

    public static final int COL_OUT2_ABB = 10;
    public static final int COL_OUT2_QTY = 11;

    public static final int COL_TIME = 12;

    public static final int COL_IN1_ABB = 13;
    public static final int COL_IN1_QTY = 14;

    public static final int COL_IN2_ABB = 15;
    public static final int COL_IN2_QTY = 16;

    public static final int COL_IN3_ABB = 17;
    public static final int COL_IN3_QTY = 18;

    public static final int COL_IN4_ABB = 19;
    public static final int COL_IN4_QTY = 20;

    public static final int COL_IN5_ABB = 21;
    public static final int COL_IN5_QTY = 22;

    public static final int COL_IN6_ABB = 23;
    public static final int COL_IN6_QTY = 24;

    public RecipeMap readRecipeListCsv() throws Exception {
        List<List<String>> lineList = readCsv(CSV_NAME);
        return readCsvLines(lineList);
    }

    public RecipeMap readCsvLines(List<List<String>> lineList) throws Exception {
        RecipeMap recipeMap = new RecipeMap();
        int line = LINE_START;
        try {
            while (line < lineList.size()) {
                List<String> colList = lineList.get(line);
                Recipe recipe = readCsvCols(colList);
                AppUtil.securePut(recipeMap, recipe.getKey(), recipe);
                line++;
            }
        } catch (Exception e) {
            System.out.println("ERROR: line=" + line);
            PrintUtil.printMap(recipeMap);
            throw e;
        }
        return recipeMap;
    }

    public Recipe readCsvCols(List<String> colList) throws Exception {
        Recipe recipe = new Recipe();
        recipe.setOrdinations(new RecipeOrdinations());
        TransputMap inputs = recipe.getInputs();
        TransputMap outputs = recipe.getOutputs();

        int col = 0;
        String openTransput = null;
        String val = null;
        try {
            while (col < colList.size()) {
                val = colList.get(col);
                switch (col) {

                    case COL_ORDER -> recipe.getOrdinations().setOrder(parseInt(val));
                    case COL_REPLICATOR_ORDER -> recipe.getOrdinations().setReplicatorOrder(parseInt(val));
                    case COL_REPLICATOR_TAB -> {
                        if ("I".equals(val)) {
                            recipe.getOrdinations().setReplicatorTab(1);
                        } else if ("B".equals(val)) {
                            recipe.getOrdinations().setReplicatorTab(2);
                        }

                    }
                    case COL_REPLICATOR_ROW -> recipe.getOrdinations().setReplicatorRow(parseInt(val));
                    case COL_REPLICATOR_COL -> recipe.getOrdinations().setReplicatorCol(parseInt(val));

                    case COL_NAME -> recipe.setName(val);
                    case COL_WITH -> recipe.setWith(val);
                    case COL_CODE -> recipe.setKey(val);
                    case COL_TIME -> recipe.setTime(parseDouble(val));
                    case COL_OUT1_ABB, COL_OUT2_ABB, COL_IN1_ABB, COL_IN2_ABB, COL_IN3_ABB, COL_IN4_ABB, COL_IN5_ABB, COL_IN6_ABB ->
                            openTransput = val;
                    case COL_OUT1_QTY, COL_OUT2_QTY -> {
                        addTransput(outputs, openTransput, val);
                        openTransput = null;
                    }
                    case COL_IN1_QTY, COL_IN2_QTY, COL_IN3_QTY, COL_IN4_QTY, COL_IN5_QTY, COL_IN6_QTY -> {
                        addTransput(inputs, openTransput, val);
                        openTransput = null;
                    }
                    default -> System.out.println("WARN: col " + col + " not indexed");
                }
                col++;
            }
        } catch (Exception e) {
            System.out.println("ERROR: col=" + col + ", val=" + val);
            throw e;
        }
        return recipe;
    }

    private void addTransput(TransputMap transputMap, String openTransput, String val) throws ParseException {
        if (!isBlank(openTransput) && !isBlank(val)) {
            AppUtil.securePut(transputMap, openTransput, parseDouble(val));
        }
    }

    private Integer parseInt(String val){
        return val == null || val.isBlank() ? null : Integer.parseInt(val);
    }


}
