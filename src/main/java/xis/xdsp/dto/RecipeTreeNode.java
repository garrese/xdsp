package xis.xdsp.dto;

import com.google.gson.Gson;
import lombok.*;
import xis.xdsp.util.AppUtil;

import java.util.*;
import java.util.function.Consumer;

@Getter
@Setter
@NoArgsConstructor
//@ToString
public class RecipeTreeNode {

    public static final String ROOT_NAME = "root";

    String name;

    transient RecipeTreeNode parent;

    ArrayList<String> path = new ArrayList<>();

    RecipeTreeCost cost = new RecipeTreeCost();

    TransputMap extraProducts;

    ArrayList<String> recipeHistory = new ArrayList<>();

    ArrayList<String> recipeExclusions;
//
//    List<String> tags;

    LinkedHashSet<String> alternativeRecipes;


    /**
     * Key is item.recipeKey+"_"+index
     */
    LinkedHashMap<String, RecipeTreeNode> childMap = new LinkedHashMap<>();

    public RecipeTreeNode getRoot() {
        if (parent == null) {
            return this;
        } else {
            return getParent().getRoot();
        }
    }

    public RecipeTreeNode getMainBranch() {
        if (parent != null && parent.getName().equals(ROOT_NAME)) {
            return this;
        } else {
            return getParent().getMainBranch();
        }
    }

    /**
     * Gets the RecipeTreeItem copy without parent and out of it.
     * Path is not generated either.
     */
    public RecipeTreeNode getCopy(String alternativeName) {
        RecipeTreeNode copy = new RecipeTreeNode();
        copy.setCost(this.getCost().getCopy());
        copy.generateName(alternativeName);
        copy.setRecipeHistory(new ArrayList<>(this.getRecipeHistory()));
//        if (this.getTags() != null) {
//            copy.setTags(new ArrayList<>(this.getTags()));
//        }
        if (this.getRecipeExclusions() != null) {
            copy.setRecipeExclusions(new ArrayList<>(this.getRecipeExclusions()));
        }
        if (this.getAlternativeRecipes() != null) {
            copy.setAlternativeRecipes(new LinkedHashSet<>(this.getAlternativeRecipes()));
        }

        for (RecipeTreeNode child : getChildMap().values()) {
            copy.addChild(child.getCopy(), false, false);
        }
        return copy;
    }

    public RecipeTreeNode getCopy() {
        return getCopy(null);
    }

    public void generateName(String alternativeName) {
        String name = alternativeName == null ?  cost.getItemKey() : alternativeName;
        this.name = name;
    }

    public void generateName() {
        generateName(null);
    }

    private void addChild(RecipeTreeNode child, boolean generatePath, boolean generateRecipeHistory) {
        child.setParent(this);
        if (child.getName() == null) {
            throw new IllegalArgumentException("addChild: child name is null");
        }
        if (generatePath) {
            child.generatePath();
        }
        if (generateRecipeHistory) {
            child.generateRecipeHistory();
        }
        AppUtil.securePut(getChildMap(), child.getName(), child);
    }

    public void addChild(RecipeTreeNode child) {
        addChild(child, true, true);
    }

    private void generatePath() {
        if (parent != null && !ROOT_NAME.equals(parent.getName())) {
            this.setPath(new ArrayList<>(getParent().getPath()));
        }
        this.getPath().add(this.getName());
    }

    private void generateRecipeHistory() {
        if (parent != null && !ROOT_NAME.equals(parent.getName())) {
            this.setRecipeHistory(new ArrayList<>(getParent().getRecipeHistory()));
        }
//        this.getRecipeHistory().add(this.getCost().recipeKey);
    }

    public RecipeTreeNode createFork() {
        RecipeTreeNode root = getRoot();
        RecipeTreeNode mainBranch = getMainBranch();
        String forkMainBranchName = composeMainBranchName(root, mainBranch);

        List<String> forkEquivalentPath = new ArrayList<>(this.getPath());
        forkEquivalentPath.removeFirst();
        forkEquivalentPath.addFirst(forkMainBranchName);

        RecipeTreeNode forkMainBranch = mainBranch.getCopy(forkMainBranchName);
        root.addChild(forkMainBranch, true, false);
        forkMainBranch.regenerateChildsPaths();

        RecipeTreeNode forkEquivalentNode = navigate(root, forkEquivalentPath);

        return forkEquivalentNode;
    }

    public void regenerateChildsPaths() {
        this.getPath().clear();
        this.generatePath();
        for (RecipeTreeNode child : this.getChildMap().values()) {
            child.regenerateChildsPaths();
        }
    }


    /**
     * Navigates through a node based on a list of keys.
     */
    public static RecipeTreeNode navigate(RecipeTreeNode rootNode, List<String> path) {
        RecipeTreeNode lastFound = rootNode;
        for (int i = 0; i < path.size(); i++) {
            String pathNode = path.get(i);
            lastFound = lastFound.getChildMap().get(pathNode);
            if (lastFound == null) {
                throw new IllegalArgumentException("pathNode not found: " + pathNode);
            }
        }
        return lastFound;
    }


    public static String composeMainBranchName(RecipeTreeNode root, RecipeTreeNode mainBranch){
        int forkIndex = root.getChildMap().size() + 1;
        String mainBranchName = mainBranch.getCost().getItemKey() + "_" + forkIndex;
        return mainBranchName;
    }

    public void addRecipeExclusion(String exclude) {
        if (getRecipeExclusions() == null) {
            setRecipeExclusions(new ArrayList<>());
        }
        getRecipeExclusions().add(exclude);
    }

//    public void addTag(String tag) {
//        if (getTags() == null) {
//            setTags(new ArrayList<>());
//        }
//        getTags().add(tag);
//    }

    public void addAlternativeRecipeList(List<String> alternativeRecipes) {
        if (getAlternativeRecipes() == null) {
            setAlternativeRecipes(new LinkedHashSet<>());
        }
        getAlternativeRecipes().addAll(alternativeRecipes);
    }

    public void addAlternativeRecipe(String alternativeRecipes) {
        if (getAlternativeRecipes() == null) {
            setAlternativeRecipes(new LinkedHashSet<>());
        }
        getAlternativeRecipes().add(alternativeRecipes);
    }

    public void forEach(Consumer<? super RecipeTreeNode> consumer){
        consumer.accept(this);
        getChildMap().values().forEach(child -> child.forEach(consumer));
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


}
