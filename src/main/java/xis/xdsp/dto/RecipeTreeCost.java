package xis.xdsp.dto;

import lombok.Data;


@Data
public class RecipeTreeCost {

    public RecipeTreeCost() {
    }

    public RecipeTreeCost(String itemKey, Double amount, String recipeKey) {
        this.itemKey = itemKey;
        this.amount = amount;
        this.recipeKey = recipeKey;
    }

    String itemKey;

    Double amount;

    String recipeKey;

    public RecipeTreeCost getCopy() {
        return new RecipeTreeCost(itemKey, amount, recipeKey);
    }

//    @Override
//    public String toString() {
//        return new Gson().toJson(this);
//    }

}
