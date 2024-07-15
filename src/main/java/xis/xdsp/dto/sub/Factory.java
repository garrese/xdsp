package xis.xdsp.dto.sub;

import lombok.Data;

@Data
public class Factory {

    Integer order;

    String name;
    String typeKey;
    String itemKey;
    Double workConsumption;
    Double idleConsumption;
    Double productionSpeed;

    public Factory(
            Integer order,
            String typeKey,
            String itemKey,
            String name,
            Double workConsumption,
            Double idleConsumption,
            Double productionSpeed) {
        this.order = order;
        this.typeKey = typeKey;
        this.itemKey = itemKey;
        this.name = name;
        this.workConsumption = workConsumption;
        this.idleConsumption = idleConsumption;
        this.productionSpeed = productionSpeed;
    }

}
