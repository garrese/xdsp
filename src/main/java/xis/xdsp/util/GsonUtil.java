package xis.xdsp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

    public static Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();
}
