package xis.xdsp.dto.sub;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransputStatsMap extends LinkedHashMap<String, TransputStats> {

//    Double total;

    public TransputStatsMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public TransputStatsMap(int initialCapacity) {
        super(initialCapacity);
    }

    public TransputStatsMap() {
    }

    public TransputStatsMap(Map<? extends String, ? extends TransputStats> m) {
        super(m);
    }

    public TransputStatsMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

//    public void updateTotal() {
//        total = this.values().stream().mapToDouble(TransputStats::getQuantity).sum();
//    }

}
