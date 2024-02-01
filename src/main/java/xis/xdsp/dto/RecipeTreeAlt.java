package xis.xdsp.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RecipeTreeAlt {

    List<String> forkList = new ArrayList<>();

    /**
     * RecipeKey from IemCost as key
     */
    LinkedHashMap<String, RecipeTreeItem> childMap = new LinkedHashMap<>();

}
