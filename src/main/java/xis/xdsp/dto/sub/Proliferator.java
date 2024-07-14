package xis.xdsp.dto.sub;

import lombok.Data;

@Data
public class Proliferator {

    String itemKey;
    String recipeKey;
    Double spraysAvailable;
    Double extraProducts;
    Double productionSpeedup;
    Double energyConsumption;

    public Proliferator(
            String itemKey,
            String recipeKey,
            Double spraysAvailable,
            Double extraProducts,
            Double productionSpeedup,
            Double energyConsumption) {
        this.itemKey = itemKey;
        this.recipeKey = recipeKey;
        this.spraysAvailable = spraysAvailable;
        this.extraProducts = extraProducts;
        this.productionSpeedup = productionSpeedup;
        this.energyConsumption = energyConsumption;
    }
}
