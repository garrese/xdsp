package xis.xdsp.dto;

import com.google.gson.Gson;
import lombok.Data;


@Data
public class RecipeTreeItemCost {

    public RecipeTreeItemCost() {
    }

    public RecipeTreeItemCost(String itemKey, Double amount, String recipeKey) {
        this.itemKey = itemKey;
        this.amount = amount;
        this.recipeKey = recipeKey;
    }

    String itemKey;

    Double amount;

    String recipeKey;

    public RecipeTreeItemCost getCopy() {
        return new RecipeTreeItemCost(itemKey, amount, recipeKey);
    }

//    @Override
//    public String toString() {
//        return new Gson().toJson(this);
//    }

}
