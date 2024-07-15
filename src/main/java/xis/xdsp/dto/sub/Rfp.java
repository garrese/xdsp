package xis.xdsp.dto.sub;

import lombok.Data;
import xis.xdsp.dto.TransputMap;

import static xis.xdsp.util.GsonUtil.GSON;

/**
 * Recipe Factory Proliferator
 */
@Data
public class Rfp {

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
    /** Difference in percentages from not proliferator sources cost */
    TransputMap rawCostMapPercetageOfTotal;
    TransputMap rawCostMapPercetageOfNoPr;

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}
