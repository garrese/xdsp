//package xis.xdsp.dto.sub;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//@Data
//@EqualsAndHashCode(callSuper = true)
//public class CostMap extends LinkedHashMap<String, Cost> {
//
//    Double total;
//
//    public CostMap(int initialCapacity, float loadFactor) {
//        super(initialCapacity, loadFactor);
//    }
//
//    public CostMap(int initialCapacity) {
//        super(initialCapacity);
//    }
//
//    public CostMap() {
//    }
//
//    public CostMap(Map<? extends String, ? extends Cost> m) {
//        super(m);
//    }
//
//    public CostMap(int initialCapacity, float loadFactor, boolean accessOrder) {
//        super(initialCapacity, loadFactor, accessOrder);
//    }
//
//    public void updateTotal() {
//        total = this.values().stream().mapToDouble(Cost::getQuantity).sum();
//    }
//
//}
