package xis.xdsp.dto;

import com.google.gson.Gson;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecipeCost {

    String label;

    RecipeCost parent;

    ItemCost itemCost;

    List<RecipeCost> recipeCostList = new ArrayList<>();

    public void addRecipeCost(RecipeCost recipeCost){
        recipeCost.setParent(this);
        getRecipeCostList().add(recipeCost);
    }

    public RecipeCost getRoot(){
        if(parent==null){
            return this;
        }else{
            return getRoot();
        }
    }

    public RecipeCost getCopy(){
        RecipeCost copy = new RecipeCost();
        copy.setLabel(label);
        copy.setParent(parent);
        copy.setItemCost(itemCost);
        copy.setRecipeCostList(new ArrayList<>(getRecipeCostList()));
        return copy;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
