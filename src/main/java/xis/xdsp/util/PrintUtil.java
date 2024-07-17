package xis.xdsp.util;

import xis.xdsp.memory.Memory;

import java.util.HashMap;

public class PrintUtil
{
    public static String printEx(Exception e) {
        return e.getClass().getSimpleName() + ": " + e.getMessage();
    }

    public static void printItemKeysJavaK() {
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
                    item.getKey(), item.getKey()
            );
        });
    }

    public static void printMap(HashMap<?, ?> map) {
        System.out.println("Map.size=" + map.size());
        map.forEach((key, val) -> {
            System.out.println(key + " > " + val);
        });
    }

    public static void printRecipeKeysJavaK() {
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
                    recipe.getKey()
                            .replace("-", "_")
                            .replace("(a)", "_a")
                            .replace("(o)", "_o"),
                    recipe.getKey());
        });
    }

    public static void printJavaConstants(){
        System.out.println("============== JAVA K ITEMS ===============");
        PrintUtil.printItemKeysJavaK();
        System.out.println("============== JAVA K RECIPES ===============");
        PrintUtil.printRecipeKeysJavaK();
        System.out.println("============== JAVA K END ===============");
    }

}
