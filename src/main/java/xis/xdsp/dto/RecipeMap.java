package xis.xdsp.dto;

import com.google.gson.Gson;

import java.util.LinkedHashMap;

/**
 * Map of [Recipe Key, Recipe]
 */
public class RecipeMap extends LinkedHashMap<String, Recipe> {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
