package xis.xdsp.dto;

import com.google.gson.Gson;

import java.util.LinkedHashMap;

public class TransputMap extends LinkedHashMap<String, Double> {

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
