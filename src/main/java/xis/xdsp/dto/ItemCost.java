package xis.xdsp.dto;

import com.google.gson.Gson;
import lombok.Data;


@Data
public class ItemCost {

    public ItemCost() {
    }

    public ItemCost(String itemKey, Double amount, String recipeKey) {
        this.itemKey = itemKey;
        this.amount = amount;
        this.recipeKey = recipeKey;
    }

    String itemKey;

    Double amount;

    String recipeKey;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
