package xis.xdsp.dto.sub;

import lombok.Data;
import xis.xdsp.dto.TransputMap;

import static xis.xdsp.util.GsonUtil.GSON;

/**
 * Recipe Factory Proliferator
 */
@Data
public class Rfp implements HasKey {

    Integer itemOrder;
    Integer factoryOrder;
    Integer proliferatorModeOrder;

    String recipeKey;
    String factoryTypeKey;
    String factoryItemKey;
    String proliferatorKey;
    Proliferator.ProliferatorMode proliferatorMode;

    Double time;
    Double energy;

    TransputStatsMap inputStatsMap;
    TransputStatsMap outputStatsMap;

    TransputMap rawCostMap;
    Double rawCostTotal;

    /**
     * Difference in percentages from not proliferator sources cost
     */
    TransputMap rawCostMapPercetageOfTotal;

    TransputMap rawCostMapPercetageOfNoPr;

    public String getKey() {
        String key = recipeKey;
        if (factoryItemKey != null) key += "_" + factoryItemKey;
        if (proliferatorKey != null) key += "_" + proliferatorKey;
        if (getProliferatorMode() != null) key += "_" + proliferatorMode;
        return key;
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
