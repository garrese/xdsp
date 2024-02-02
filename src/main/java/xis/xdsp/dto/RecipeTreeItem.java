package xis.xdsp.dto;

import com.google.gson.Gson;
import lombok.*;
import xis.xdsp.util.AppUtil;

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

//    List<String> forkList = new ArrayList<>();

    String outputRecipeFork;

    transient RecipeTreeItem parent;

    List<String> TreeMapPath = new ArrayList<>();

    RecipeTreeItemCost item;

    /**
     * Key is item.recipeKey+"_"+index
     */
    LinkedHashMap<String, RecipeTreeItem> childMap = new LinkedHashMap<>();

    public void addChild(RecipeTreeItem child) {
        child.setParent(this);

        String key = child.getItem().getRecipeKey();
        if (child.getOutputRecipeFork() != null) {
            key += "_" + child.getOutputRecipeFork();
        }

        List<String> childTreeMapPath = new ArrayList<>(getTreeMapPath());
        childTreeMapPath.add(key);
        child.setTreeMapPath(childTreeMapPath);

        AppUtil.securePut(getChildMap(), key, child);

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
     * Gets the RecipeTreeItem copy without parent and out of it.
     */
    public RecipeTreeItem getCopy() {
        RecipeTreeItem copy = new RecipeTreeItem();
//        copy.setForkList(new ArrayList<>(forkList));
        copy.setItem(item.getCopy());
        for (Map.Entry<String, RecipeTreeItem> itemEntry : getChildMap().entrySet()) {
            copy.getChildMap().put(itemEntry.getKey(), itemEntry.getValue().getCopy());
        }
        return copy;
    }

    public RecipeTreeItem createFork(String outputRecipeFork) {

        RecipeTreeItem mainBranch = getThisMainBranch();
        RecipeTreeItem fork = mainBranch.getCopy();
//        fork.getForkList().add(outputRecipeKey);
//        fork.addChild(outputRecipeTreeItem);

        RecipeTreeItem forkEquivalentNode = navigate(fork, fork.getTreeMapPath());
        forkEquivalentNode.setOutputRecipeFork(outputRecipeFork);
        getRoot().addChild(fork);

        return fork;
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
                throw new IllegalArgumentException("treeMapKeys not found");
            }
        }
        return lastFound;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
