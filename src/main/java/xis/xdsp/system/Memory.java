package xis.xdsp.system;

import xis.xdsp.dto.Item;
import xis.xdsp.dto.ItemMap;
import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.RecipeMap;
import xis.xdsp.util.AppUtil;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class Memory {

    public static ItemMap ITEMS;
    public static RecipeMap RECIPES;

    public static Collection<Recipe> getRecipes(){
        return RECIPES.values();
    }

    public static Collection<Item> getItems(){
        return ITEMS.values();
    }

    public static Recipe getRecipe(String recipeKey){
        return RECIPES.get(recipeKey);
    }

    public static Item getItem(String itemKey){
        return ITEMS.get(itemKey);
    }

    public static RecipeMap getRecipeMap(List<String> recipeList) {
        RecipeMap recipeMap = new RecipeMap();
        for (String recipeKey : recipeList) {
            AppUtil.securePut(recipeMap, recipeKey, RECIPES.get(recipeKey));
        }
        return recipeMap;
    }


}
