package xis.xdsp.ia;

import xis.xdsp.dto.Item;
import xis.xdsp.dto.Recipe;
import xis.xdsp.memory.Memory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Intento de calculadaora con la IA Claude Sonnet 3.5.
 * NO FUNCIONA
 */
public class IAClaudeCalculator {


    public static class ItemCost {
        public Map<String, Double> sourceCosts;
        public double totalCost;

        ItemCost() {
            sourceCosts = new HashMap<>();
            totalCost = 0.0;
        }
    }

    private static Map<String, ItemCost> memo = new HashMap<>();
    private static Set<String> currentlyCalculating = new HashSet<>();

    public static ItemCost calculateProductionCost(String itemKey) {
        if (memo.containsKey(itemKey)) {
            return memo.get(itemKey);
        }

        if (currentlyCalculating.contains(itemKey)) {
            // Detectamos una recursión cíclica, retornamos un costo "infinito"
            ItemCost cyclicCost = new ItemCost();
            cyclicCost.totalCost = Double.POSITIVE_INFINITY;
            return cyclicCost;
        }

        currentlyCalculating.add(itemKey);

        Item item = Memory.getItem(itemKey);
        ItemCost lowestCost = null;

        for (String recipeKey : item.getOutputRecipeList()) {
            Recipe recipe = Memory.getRecipe(recipeKey);
            ItemCost currentCost = calculateRecipeCost(recipe);

            if (lowestCost == null || currentCost.totalCost < lowestCost.totalCost) {
                lowestCost = currentCost;
            }
        }

        currentlyCalculating.remove(itemKey);
        memo.put(itemKey, lowestCost);
        return lowestCost;
    }

    private static ItemCost calculateRecipeCost(Recipe recipe) {
        ItemCost cost = new ItemCost();

        if (recipe.isSource()) {
            cost.sourceCosts.put(recipe.getOutputs().keySet().iterator().next(), 1.0);
            cost.totalCost = 1.0;
            return cost;
        }

        for (Map.Entry<String, Double> input : recipe.getInputs().entrySet()) {
            String inputItemKey = input.getKey();
            Double inputQuantity = input.getValue();

            ItemCost inputCost = calculateProductionCost(inputItemKey);

            if (inputCost.totalCost == Double.POSITIVE_INFINITY) {
                // Si detectamos un costo infinito, propagamos este costo
                cost.totalCost = Double.POSITIVE_INFINITY;
                return cost;
            }

            for (Map.Entry<String, Double> sourceCost : inputCost.sourceCosts.entrySet()) {
                String sourceKey = sourceCost.getKey();
                Double sourceQuantity = sourceCost.getValue() * inputQuantity;

                cost.sourceCosts.merge(sourceKey, sourceQuantity, Double::sum);
            }

            cost.totalCost += inputCost.totalCost * inputQuantity;
        }

        // Normalize costs based on output quantity
        Double outputQuantity = recipe.getOutputs().values().iterator().next();
        for (Map.Entry<String, Double> entry : cost.sourceCosts.entrySet()) {
            entry.setValue(entry.getValue() / outputQuantity);
        }
        cost.totalCost /= outputQuantity;

        return cost;
    }

    public static void demo() {
        // Ejemplo de uso
        String itemToCalculate = "Ir"; // Iron Ingot
        ItemCost cost = calculateProductionCost(itemToCalculate);

        System.out.println("Costes de producción para " + itemToCalculate + ":");
        if (cost.totalCost == Double.POSITIVE_INFINITY) {
            System.out.println("Se detectó una recursión cíclica. No se puede calcular el costo.");
        } else {
            System.out.println("Costes de fuentes:");
            for (Map.Entry<String, Double> entry : cost.sourceCosts.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("Coste total: " + cost.totalCost);
        }
    }

    public static void calcAndPrint() {
        Memory.getItemsMap().forEach((k, i) ->{
            IAClaudeCalculator.ItemCost itemCost = IAClaudeCalculator.calculateProductionCost(k);
            System.out.println(k + " > " + itemCost.sourceCosts);
        });
    }

}
