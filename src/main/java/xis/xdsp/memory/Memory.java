package xis.xdsp.memory;

import xis.xdsp.dto.*;
import xis.xdsp.dto.sub.Factory;
import xis.xdsp.dto.sub.Proliferator;
import xis.xdsp.util.AppUtil;

import java.util.*;


public class Memory {

    public static ItemMap ITEMS;
    public static RecipeMap RECIPES;

    public static Map<String, Proliferator> PROLIFERATORS;
    public static Map<String, Map<String, Factory>> FACTORIES;

    public static Set<String> RAW_ITEM_LIST;

    public static Map<String, RecipeAltSeq> altSeqMap;

    public static Item getItem(String itemKey) {
        return ITEMS.get(itemKey);
    }

    public static Collection<Item> getItems() {
        return ITEMS.values();
    }

    public static void setItems(ItemMap ITEMS) {
        Memory.ITEMS = ITEMS;
    }

    public static ItemMap getItemsMap() {
        return ITEMS;
    }

    public static Recipe getRecipe(String recipeKey) {
        return RECIPES.get(recipeKey);
    }

    public static Collection<Recipe> getRecipes() {
        return RECIPES.values();
    }

    public static void setRecipes(RecipeMap RECIPES) {
        Memory.RECIPES = RECIPES;
    }

    public static RecipeMap getRecipesMap() {
        return RECIPES;
    }

    public static RecipeMap getRecipesMapByList(List<String> recipeList) {
        RecipeMap recipeMap = new RecipeMap();
        for (String recipeKey : recipeList) {
            AppUtil.securePut(recipeMap, recipeKey, RECIPES.get(recipeKey));
        }
        return recipeMap;
    }

    public static Collection<Proliferator> getProliferators() {
        return PROLIFERATORS.values();
    }

    public static Proliferator getProliferator(String itemK) {
        return PROLIFERATORS.get(itemK);
    }

    public static Map<String, Factory> getFactoriesByType(String type) {
        return FACTORIES.get(type);
    }

    public static Factory getFactory(String typeKey, String itemKey) {
        return FACTORIES.get(typeKey).get(itemKey);
    }

}
