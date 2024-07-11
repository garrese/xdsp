package xis.xdsp.dto.sub;

import lombok.Data;

@Data
public class Proliferator {

    Double sprays;
    Double extraProducts;
    Double productionSpeedup;
    Double energyConsumption;

    public Proliferator(Double sprays, Double extraProducts, Double productionSpeedup, Double energyConsumption) {
        this.sprays = sprays;
        this.extraProducts = extraProducts;
        this.productionSpeedup = productionSpeedup;
        this.energyConsumption = energyConsumption;
    }
}
