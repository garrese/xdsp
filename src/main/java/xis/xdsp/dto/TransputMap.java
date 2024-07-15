package xis.xdsp.dto;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Map of input/output quantities [item key, quantity]
 */
public class TransputMap extends LinkedHashMap<String, Double> {

    public TransputMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TransputMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TransputMap() {
    }

    public TransputMap(Map<? extends String, ? extends Double> m) {
        super(m);
    }

    public TransputMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public void sumTransputMap(TransputMap toSumTransputMap) {
        for (Map.Entry<String, Double> toSumTransput : toSumTransputMap.entrySet()) {
            sumTransput(toSumTransput.getKey(), toSumTransput.getValue());
        }
    }

    public void sumTransput(String toSumKey, Double toSumValue) {
        Double thisTransputValue = this.get(toSumKey);
        Double value;
        if (thisTransputValue != null) {
            value = toSumValue + thisTransputValue;
        } else {
            value = toSumValue;
        }
        this.put(toSumKey, value);
    }

    public void multiply(Double toMultiplyValue) {
        this.forEach((k, v) -> this.put(k, v * toMultiplyValue));
    }

    public void divideNumerator(Double numerator) {
        this.forEach((k, v) -> this.put(k, numerator / v));
    }

    public void divideDenominator(Double denominator) {
        this.forEach((k, v) -> this.put(k, v / denominator));
    }

    public Double calcTotal(){
        return this.values().stream().mapToDouble(Double::doubleValue).sum();
    }

}
