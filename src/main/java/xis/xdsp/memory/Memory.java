package xis.xdsp.memory;

import xis.xdsp.dto.Item;
import xis.xdsp.dto.ItemMap;
import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.RecipeMap;
import xis.xdsp.dto.sub.Proliferator;
import xis.xdsp.util.AppUtil;
import xis.xdsp.util.ItemK;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class Memory {

    private static ItemMap ITEMS;
    private static RecipeMap RECIPES;

    private static Map<String, Proliferator> PROLIFERATORS;

    public static Item getItem(String itemKey){
        return ITEMS.get(itemKey);
    }

    public static Collection<Item> getItems(){
        return ITEMS.values();
    }

    public static void setItems(ItemMap ITEMS) {
        Memory.ITEMS = ITEMS;
    }

    public static ItemMap getItemsMap(){
        return ITEMS;
    }

    public static Recipe getRecipe(String recipeKey){
        return RECIPES.get(recipeKey);
    }

    public static Collection<Recipe> getRecipes(){
        return RECIPES.values();
    }

    public static void setRecipes(RecipeMap RECIPES) {
        Memory.RECIPES = RECIPES;
    }

    public static RecipeMap getRecipesMap(){
        return RECIPES;
    }

    public static RecipeMap getRecipesMapByList(List<String> recipeList) {
        RecipeMap recipeMap = new RecipeMap();
        for (String recipeKey : recipeList) {
            AppUtil.securePut(recipeMap, recipeKey, RECIPES.get(recipeKey));
        }
        return recipeMap;
    }

    public static void setProliferators(Map<String, Proliferator> PROLIFERATORS) {
        Memory.PROLIFERATORS = PROLIFERATORS;
    }

    public static Collection<Proliferator> getProliferators() {
        return PROLIFERATORS.values();
    }

    public static Map<String, Proliferator> getProliferatorsMap() {
        return PROLIFERATORS;
    }
}
