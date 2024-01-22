package xis.xdsp.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static xis.xdsp.system.K.CSV_SEPARATOR;
import static xis.xdsp.util.AppUtil.isBlank;

public class CsvReader {

    DecimalFormat decimalFormat;

    public CsvReader(){
        decimalFormat = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        decimalFormat.setDecimalFormatSymbols(symbols);
    }

    public List<List<String>> readCsv(String csvName) throws IOException {
        List<List<String>> records = new ArrayList<>();
        String csvPath = AppUtil.getResource(getClass(),csvName).getPath();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(CSV_SEPARATOR);
                records.add(Arrays.asList(values));
            }
        }
        return records;
    }

    public boolean parseOneAsBoolean(String val){
        if(val == null) return false;
        return "1".equals(val);
    }

    public Double parseFormattedPercentage(String val){
        return 0D;
    }

    public Double parseDouble(String val) throws ParseException {
        if(isBlank(val)) return null;
        return decimalFormat.parse(val).doubleValue();
    }

}
