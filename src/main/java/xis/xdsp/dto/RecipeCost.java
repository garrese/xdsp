package xis.xdsp.dto;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class RecipeCost {

    String label;

    ItemCost itemCost;

    List<RecipeCost> recipeCostList = new ArrayList<>();

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
