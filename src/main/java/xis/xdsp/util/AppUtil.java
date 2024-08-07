package xis.xdsp.util;

import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class AppUtil {

    public static URL getResource(Class<?> callingClass, String resource) {
        return Objects.requireNonNull(callingClass.getClassLoader().getResource(resource));
    }

    public static boolean isBlank(String string) {
        return string == null || string.isBlank();
    }

    public static <K, V> void securePut(Map<K, V> map, K key, V value) {
        if (map.get(key) != null) {
            throw new RuntimeException("key already exists! key=" + key);
        }
        map.put(key, value);
    }

}
