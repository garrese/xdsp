package xis.xdsp.dto;

import lombok.Data;

@Data
public class Item {

    String type;
    String name;
    String abb;
    boolean rare;
    Fuel fuel = new Fuel();

    public boolean isFuel(){
        return fuel != null && fuel.getType() != null && !fuel.getType().isBlank();
    }


}


