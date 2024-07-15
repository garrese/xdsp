package xis.xdsp.dto;

import lombok.Data;
import xis.xdsp.dto.sub.RecipeOrdinations;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class Recipe {

    RecipeOrdinations ordinations;

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
    String key;

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
     * Calculated items cost of each output. Direct items, not sources. Negative values for coproduced outputs.
     * Map<ItemKey,TransputMap>
     * Post-ingest calculated
     *
     * Examples:
     * Ir-Sm itemsCost={Ir={"IrO":1.0}}
     * Coil-As itemsCost={Coil={"Mag":1.0,"Co":0.5}}
     * PlasRef-Refi itemsCost={Oil={"Crude":1.0,"H":-0.5}, H={"Crude":2.0,"Oil":-2.0}}
     */
    Map<String, TransputMap> outputsCost = new LinkedHashMap<>();

//    RecipeTreeNode recipeTreeNode;

    /**
     * Sources cost for the recipe (not for separate outputs).
     */
    TransputMap recipeRawCost = new TransputMap();

    /**
     * Map<ItemKey for 3 Proliferators,TransputMap>
     */
    Map<String, TransputMap> recipeRawCostPrExtra = new LinkedHashMap<>();

    Map<String, TransputMap> recipeRawCostPrSpeed = new LinkedHashMap<>();

    Double recipeSpraysNeeded;

    /**
     * Map<ItemKey for 3 Proliferators,TransputMap>
     */
    Map<String, TransputMap> recipeSpraysRawCost = new LinkedHashMap<>();

    public boolean isSource() {
        return inputs.size() == 0;
    }

//    @Override
//    public String toString() {
//        return new Gson().toJson(this);
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(name, recipe.name) && Objects.equals(with, recipe.with)
                && Objects.equals(key, recipe.key) && Objects.equals(outputs, recipe.outputs)
                && Objects.equals(time, recipe.time) && Objects.equals(inputs, recipe.inputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, with, key, outputs, time, inputs);
    }
}
