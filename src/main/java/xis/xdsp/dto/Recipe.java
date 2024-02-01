package xis.xdsp.dto;

import com.google.gson.Gson;
import lombok.Data;

@Data
public class Recipe {

    /**
     * In-game recipe name
     * Ingested value
     */
    String name;

    /**
     * Recipe's building
     * Ingested value
     */
    String with;

    /**
     * Unique recipe identifier
     * Ingested value
     */
    String code;

    /**
     * In-game recipe base outputs
     * Ingested value
     */
    TransputMap outputs = new TransputMap();

    /**
     * In-game recipe base time
     * Ingested value
     */
    Double time;

    /**
     * In-game recipe base inputs
     * Ingested value
     */
    TransputMap inputs = new TransputMap();

    /**
     * Calculated item cost
     * Post-ingest calculated
     */
    TransputMap itemCost;

    RecipeTreeItem recipeTreeItem;

    public boolean isSource() {
        if (inputs.size() == 0) return true;
        else return false;
    }

//    @Override
//    public String toString() {
//        return new Gson().toJson(this);
//    }

}
