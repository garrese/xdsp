package xis.xdsp.printers.base;

import xis.xdsp.dto.sub.HasKey;
import xis.xdsp.util.GsonUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

public class JsonWriter {

    public static void writeMap(String path, String name, Map<?,?> objectMap) throws Exception {
        String fileName = genFileName(path, name);
        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName, true))) {
            w.write(start());
            w.newLine();
            int i = 1;
            for (Map.Entry<?,?> obj : objectMap.entrySet()) {
                w.write(mapObject(obj.getKey().toString(), obj.getValue(), i == objectMap.size()));
                w.newLine();
                i++;
            }
            w.write(end());
        } catch (IOException ex) {
            System.out.println("JsonWriter error" + ex);
        }
    }

    public static void writeCollection(String path, String name, Collection<? extends HasKey> objectList) throws Exception {
        String fileName = genFileName(path, name);
        try (BufferedWriter w = new BufferedWriter(new FileWriter(fileName, true))) {
            w.write(start());
            w.newLine();
            int i = 1;
            for (HasKey obj : objectList) {
                w.write(mapObject(obj.getKey(), obj, i == objectList.size()));
                w.newLine();
                i++;
            }
            w.write(end());
        } catch (IOException ex) {
            System.out.println("JsonWriter error" + ex);
        }
    }

    private static String genFileName(String path, String name) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String date = df.format(System.currentTimeMillis());
        String fileName = path + name + "_" + date + ".json";
        System.out.println("Generating " + fileName);
        return fileName;
    }

    public static String start() {
        return "{";
    }

    public static String end() {
        return "}";
    }

    public static String arrayStart(String name) {
        return "\"" + name + "\": [";
    }

    public static String arrayEnd() {
        return "]";
    }

    public static String mapObject(String name, Object o, boolean isLast) {
        return "\"" + name + "\":" + arrayObject(o, isLast);
    }

    public static String arrayObject(Object o, boolean isLast) {
        String json = GsonUtil.GSON.toJson(o);
        if(!isLast){
            json += ",";
        }
        return  json;
    }
}
