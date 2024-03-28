package xis.xdsp.dto;

import com.google.gson.Gson;

import java.util.LinkedHashMap;

/**
 * Map of input/output quantities [item key, quantity]
 */
public class TransputMap extends LinkedHashMap<String, Double> {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
