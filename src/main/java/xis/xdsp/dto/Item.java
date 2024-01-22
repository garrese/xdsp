package xis.xdsp.dto;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.List;

@Data
public class Item {

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
    String abb;

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
     * @return true if the item can be used as fuel in the in-games fuel consumers
     */
    public boolean isFuel(){
        return fuel != null && fuel.getType() != null && !fuel.getType().isBlank();
    }


}


