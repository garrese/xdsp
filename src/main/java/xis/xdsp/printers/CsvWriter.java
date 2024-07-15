package xis.xdsp.printers;

import xis.xdsp.util.K;

import java.text.DecimalFormat;

public class CsvWriter {

    DecimalFormat perc1d = new DecimalFormat("0.0%");
    DecimalFormat num0d = new DecimalFormat("0");
    DecimalFormat num2d = new DecimalFormat("0.00");
    DecimalFormat num3d = new DecimalFormat("0.000");

    public String cellString(String val) {
        return "\"" + val + "\"" + K.CSV_SEPARATOR;
    }

    public String cellsString(String... vals) {
        StringBuilder result = new StringBuilder();
        for (String val : vals) {
            result.append(cellString(val));
        }
        return result.toString();
    }

    public String cellsDouble(Double... vals) {
        StringBuilder result = new StringBuilder();
        for (Double val : vals) {
            result.append(cellDouble(val));
        }
        return result.toString();
    }

    public String cellDouble(Double val) {
        return cellString(String.format("%1$,.4f", val));
//        return cell(String.format("%.2f", val));
    }

    public String cell(Object o) {
        if (o == null) {
            return K.CSV_SEPARATOR;
        } else if (o instanceof Double) {
            return cellDouble((Double) o);
        } else {
            return cellString((String) o);
        }
    }

    public String cells(Object... os) {
        StringBuilder result = new StringBuilder();
        for (Object o : os) {
            result.append(cell(o));
        }
        return result.toString();
    }

    public String emtpyCells(int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            result.append(K.CSV_SEPARATOR);
        }
        return result.toString();
    }

    public String cellFormat(Object val, DecimalFormat df) {
        if (val == null) return cell(val);
        else return cellString(df.format(val));
    }

    public String cellPerc1d(Double val) {
        return cellFormat(val, perc1d);
    }

    public String cellNum0d(Double val) {
        return cellFormat(val, num0d);
    }

    public String cellNum2d(Double val) {
        return cellFormat(val, num2d);
    }

    public String cellNum3d(Double val) {
        return cellFormat(val, num3d);
    }


}
