package xis.xdsp.dto;

import lombok.Data;

@Data
public class Recipe {

    String name;
    String with;
    String code;
    TransputMap outputs = new TransputMap();
    Double time;
    TransputMap inputs = new TransputMap();

    public boolean isSource() {
        if (inputs.size() == 0) return true;
        else return false;
    }

}
