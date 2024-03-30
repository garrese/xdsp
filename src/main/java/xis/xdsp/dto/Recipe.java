package xis.xdsp.dto;

import lombok.Data;

import java.util.Objects;

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

//    RecipeTreeNode recipeTreeNode;

    TransputMap sourcesCost;

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
                && Objects.equals(code, recipe.code) && Objects.equals(outputs, recipe.outputs)
                && Objects.equals(time, recipe.time) && Objects.equals(inputs, recipe.inputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, with, code, outputs, time, inputs);
    }
}
