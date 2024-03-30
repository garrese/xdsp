package xis.xdsp.util;

import xis.xdsp.system.Memory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AppUtil {

    public static void printMap(HashMap<?, ?> map) {
        System.out.println("Map.size=" + map.size());
        map.forEach((key, val) -> {
            System.out.println(key + " > " + val);
        });
    }

    public static URL getResource(Class<?> callingClass, String resource) {
        return Objects.requireNonNull(callingClass.getClassLoader().getResource(resource));
    }

    public static boolean isBlank(String string) {
        return string == null || string.isBlank();
    }

    public static <K, V> void securePut(Map<K, V> map, K key, V value) {
        if (map.get(key) != null) {
            throw new RuntimeException("key already exists! key=" + key);
        }
        map.put(key, value);
    }

    /**
     * Nombre original
     */
    public static final String nombre = """
            /*** ejemplo 
            """;

    public static void printItemKeysJavaK(){
        Memory.getItems().forEach(item -> {
            String k = """
                    /**
                     * Name: %s, type: %s, rare: %s
                     */
                    public static final String %s = "%s";
                    
                    """;
            System.out.printf(
                    (k),
                    item.getName(), item.getType(), item.isRare(),
                    item.getAbb(), item.getAbb()
            );
        });
    }

    public static void printRecipeKeysJavaK(){
        Memory.getRecipes().forEach(recipe -> {
            String k = """
                   /**
                     * Name: %s, with: %s
                     */
                    public static final String %s = "%s";
                    
                    """;
            System.out.printf(
                    (k),
                    recipe.getName(), recipe.getWith(),
                    recipe.getCode()
                            .replace("-","_")
                            .replace("(a)","_a")
                            .replace("(o)","_o"),
                    recipe.getCode());
        });
    }

}
