package xis.xdsp.printers.formatters;

import lombok.Data;
import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.TransputMap;

import java.util.*;

@Data
public class RecipeFormatter {

    Recipe recipe;

    SortedSet<String> itemsCostHeaders = new TreeSet<>();

    public RecipeFormatter(Recipe recipe) {
        this.recipe = recipe;

        recipe.getRecipeSpraysSourceCost().values().forEach(transputMap -> itemsCostHeaders.addAll(transputMap.keySet()));
        itemsCostHeaders.addAll(recipe.getRecipeSourcesCost().keySet());
        System.out.println(itemsCostHeaders);
    }

    public ArrayList<String> getItemsCostHeadersList(){
        return new ArrayList<>(getItemsCostHeaders());
    }

    public String[] getItemsCostHeadersArray(){
        return itemsCostHeaders.toArray(new String[0]);
    }

}
