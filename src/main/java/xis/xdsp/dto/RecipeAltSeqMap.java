package xis.xdsp.dto;

import java.util.LinkedHashMap;

/**
 * Map of [Item Key, RecipeAltSeq]
 */
public class RecipeAltSeqMap extends LinkedHashMap<String, RecipeAltSeq> {

    public void put(String key, String... value) {
        put(key, new RecipeAltSeq(value));
    }

}
