package xis.xdsp;

import xis.xdsp.memory.Memory;
import xis.xdsp.memory.MemoryLoader;
import xis.xdsp.util.AppUtil;

public class App {

    public static void main(String[] args) throws Exception {
        MemoryLoader.load();

        AppUtil.printMap(Memory.getItemsMap());
        AppUtil.printMap(Memory.getRecipesMap());
//        printJavaConstants();
    }

    public static void printJavaConstants(){
        System.out.println("============== JAVA K ITEMS ===============");
        AppUtil.printItemKeysJavaK();
        System.out.println("============== JAVA K RECIPES ===============");
        AppUtil.printRecipeKeysJavaK();
        System.out.println("============== JAVA K END ===============");
    }


}
