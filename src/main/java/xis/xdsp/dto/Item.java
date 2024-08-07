package xis.xdsp.dto;

import com.google.gson.Gson;
import lombok.Data;
import xis.xdsp.dto.sub.Fuel;
import xis.xdsp.dto.sub.HasKey;

import java.util.List;

@Data
public class Item implements HasKey {

    /**
     * In-game type
     * Ingested value
     */
    String type;

    /**
     * In-game name
     * Ingested value
     */
    String name;

    /**
     * Item abbreviation. Unique identifier.
     * Ingested value
     */
    String key;

    /**
     * In-game rare descriptor
     * Ingested value
     */
    boolean rare;

    /**
     * In-game fuel properties
     * Ingested value
     */
    Fuel fuel = new Fuel();

    List<String> outputRecipeList;

    List<String> inputRecipeList;

    /**
     * Sources cost for the recipe (not for separate outputs).
     */
    TransputMap itemRawCost = new TransputMap();

    /**
     * @return true if the item can be used as fuel in the in-games fuel consumers
     */
    public boolean isFuel(){
        return fuel != null && fuel.getType() != null && !fuel.getType().isBlank();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}


