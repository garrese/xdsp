package xis.xdsp.dto;

import com.google.gson.Gson;
import lombok.Data;

import java.util.ArrayList;

@Data
public class RecipeCostListBad extends ArrayList<RecipeCost> {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
