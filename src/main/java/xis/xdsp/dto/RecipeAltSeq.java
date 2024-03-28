package xis.xdsp.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Recipe alternative sequence.
 */
public class RecipeAltSeq {

    private int index = 0;

    ArrayList<String> sequence = new ArrayList<>();

    public RecipeAltSeq() {
    }

    public RecipeAltSeq(String... recipeName) {
        sequence.addAll(List.of(recipeName));
    }

    public RecipeAltSeq(List<String> sequence) {
        this.sequence.addAll(sequence);
    }

    public String getNext() {
        String result = sequence.get(index);
        index++;
        if (index >= sequence.size()) index = 0;
        return result;
    }


}
