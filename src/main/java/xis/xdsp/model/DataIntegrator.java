package xis.xdsp.model;

import xis.xdsp.dto.Item;
import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.TransputMap;
import xis.xdsp.system.Memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataIntegrator {

    public static void findItemRecipes(){
        for(Item item: Memory.ITEMS.values()){
            item.setInputRecipeList(findInputRecipes(item.getAbb()));
            item.setOutputRecipeList(findOutputRecipes(item.getAbb()));
        }
    }

    private static List<String> findOutputRecipes(String abb){
        List<String> result = new ArrayList<>();
        for(Recipe recipe: Memory.RECIPES.values()){
            for(String outputAbb: recipe.getOutputs().keySet()){
                if(outputAbb.equals(abb)){
                    result.add(recipe.getCode());
                }
            }
        }
        return result;
    }

    private static List<String> findInputRecipes(String abb){
        List<String> result = new ArrayList<>();
        for(Recipe recipe: Memory.RECIPES.values()){
            for(String inputAbb: recipe.getInputs().keySet()){
                if(inputAbb.equals(abb)){
                    result.add(recipe.getCode());
                }
            }
        }
        return result;
    }


}
