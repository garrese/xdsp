package xis.xdsp.system;

import xis.xdsp.dto.ItemMap;
import xis.xdsp.dto.RecipeMap;
import xis.xdsp.util.AppUtil;

import java.util.List;

public class Memory {

    public static ItemMap ITEMS;
    public static RecipeMap RECIPES;

    public static RecipeMap getRecipeMap(List<String> recipeList) {
        RecipeMap recipeMap = new RecipeMap();
        for (String recipeKey : recipeList) {
            AppUtil.securePut(recipeMap, recipeKey, RECIPES.get(recipeKey));
        }
        return recipeMap;
    }


}
