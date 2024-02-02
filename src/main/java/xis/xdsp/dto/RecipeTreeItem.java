package xis.xdsp.dto;

import com.google.gson.Gson;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
//@ToString
public class RecipeTreeItem {

    public static final String ROOT_KEY = "root";

    List<String> forkList = new ArrayList<>();

    transient RecipeTreeItem parent;

    List<String> TreeMapKeys = new ArrayList<>();

    RecipeTreeItemCost item;

    /**
     * RecipeKey from IemCost as key
     */
    LinkedHashMap<String, RecipeTreeItem> childMap = new LinkedHashMap<>();

    public void addChild(RecipeTreeItem child) {
        child.setParent(this);
        getChildMap().put(child.getItem().getRecipeKey(), child);
    }

    public RecipeTreeItem getRoot() {
        if (parent == null) {
            return this;
        } else {
            return getParent().getRoot();
        }
    }

    public RecipeTreeItem getThisMainBranch() {
        if (parent != null && parent.getItem().getRecipeKey().equals(ROOT_KEY)) {
            return this;
        } else {
            return getParent().getThisMainBranch();
        }
    }


    /**
     * Gets the RecipeTreeItem copy without parent and out of it
     */
    public RecipeTreeItem getCopy() {
        RecipeTreeItem copy = new RecipeTreeItem();
        copy.setForkList(new ArrayList<>(forkList));
        copy.setItem(item.getCopy());
        for (Map.Entry<String, RecipeTreeItem> itemEntry : getChildMap().entrySet()) {
            copy.getChildMap().put(itemEntry.getKey(), itemEntry.getValue().getCopy());
        }
        return copy;
    }

    /**
     * Navigates through a main branch based on a list of keys.
     * The first key in the list is not taken into account, since it corresponds to the first node in the main branch.
     */
    public static RecipeTreeItem navigate(RecipeTreeItem mainBranch, List<String> treeMapKeys) {
        RecipeTreeItem lastFound = mainBranch;
        for (int i = 1; i < treeMapKeys.size(); i++) {
            lastFound = lastFound.getChildMap().get(treeMapKeys.get(i));
            if (lastFound == null) {
                return null;
            }
        }
        return lastFound;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
