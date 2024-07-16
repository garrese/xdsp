package xis.xdsp.calculators;

import xis.xdsp.dto.Recipe;
import xis.xdsp.dto.TransputMap;
import xis.xdsp.dto.sub.*;
import xis.xdsp.memory.Memory;
import xis.xdsp.util.AppUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Recipe Factory Proliferator Calculator
 */
public class RfpCalculator {

    public static List<Rfp> calcAllRfp(List<String> excludeRecipeKeyList) {
        if (excludeRecipeKeyList == null) excludeRecipeKeyList = new ArrayList<>();
        ArrayList<Rfp> result = new ArrayList<>();
        for (Recipe recipe : Memory.getRecipes()) {
//            if(recipe.getKey().equals("AnRod-As")){
//                System.out.println("break-point-here");
//            }
            if (!excludeRecipeKeyList.contains(recipe.getKey()) && !recipe.isSource()) {
                for (Factory factory : Memory.FACTORIES.get(recipe.getWith()).values()) {
                    result.add(calcRfp(recipe.getKey(), factory.getItemKey(), null, null));
                    for (Proliferator pr : Memory.getProliferators()) {
                        result.add(calcRfp(recipe.getKey(), factory.getItemKey(), pr.getItemKey(), Proliferator.ProliferatorMode.EXTRA));
                        result.add(calcRfp(recipe.getKey(), factory.getItemKey(), pr.getItemKey(), Proliferator.ProliferatorMode.SPEED));
                    }
                }
            }
        }
        return result;
    }

    public static Rfp calcRfp(
            String recipeK,
            String factoryItemK,
            String proliferatorItemK,
            Proliferator.ProliferatorMode proliferatorMode) {
        Rfp rfp = new Rfp();

        rfp.setRecipeKey(recipeK);
        rfp.setFactoryItemKey(factoryItemK);
        rfp.setProliferatorKey(proliferatorItemK);
        rfp.setProliferatorMode(proliferatorMode);

        Recipe recipe = Memory.getRecipe(recipeK);
        Factory factory = Memory.getFactoriesByType(recipe.getWith()).get(factoryItemK);
        Proliferator proliferator = Memory.getProliferator(proliferatorItemK);

        rfp.setItemOrder(recipe.getOrdinations().getOrder());
        rfp.setFactoryOrder(factory.getOrder());
        calcProliferatorModeOrder(proliferatorMode, rfp);

        rfp.setFactoryTypeKey(recipe.getWith());
        double time = calcTime(rfp, recipe, factory, proliferator, proliferatorMode);
        calcEnergy(rfp, factory, proliferator, time);

        rfp.setInputStatsMap(
                calcTransputStats(
                        recipe.getInputs(),
                        time,
                        proliferator,
                        proliferatorMode,
                        false
                )
        );
        rfp.setOutputStatsMap(
                calcTransputStats(
                        recipe.getOutputs(),
                        time,
                        proliferator,
                        proliferatorMode,
                        true
                )
        );

        calcRawCosts(proliferatorItemK, proliferatorMode, rfp, recipe);
        calcRawCostPercentages(rfp, recipe, proliferator);
        return rfp;
    }

    private static void calcProliferatorModeOrder(Proliferator.ProliferatorMode proliferatorMode, Rfp rfp) {
        if (rfp.getProliferatorMode() == null) {
            rfp.setProliferatorModeOrder(1);
        } else {
            switch (proliferatorMode) {
                case EXTRA -> rfp.setProliferatorModeOrder(2);
                case SPEED -> rfp.setProliferatorModeOrder(3);
            }
        }
    }

    private static double calcTime(
            Rfp rfp,
            Recipe recipe,
            Factory factory,
            Proliferator proliferator,
            Proliferator.ProliferatorMode proliferatorMode) {

        double time = recipe.getTime();
        if (time == 0) return time;
        time /= factory.getProductionSpeed();
        if (proliferator != null && proliferatorMode.equals(Proliferator.ProliferatorMode.SPEED)) {
            time /= 1 + proliferator.getProductionSpeedup();
        }
        rfp.setTime(time);
        return time;
    }

    private static void calcEnergy(
            Rfp rfp,
            Factory factory,
            Proliferator proliferator,
            double time) {
        if (time != 0) {
            double energy = time * factory.getWorkConsumption();
            if (proliferator != null) {
                double energyMod = 1 + proliferator.getEnergyConsumption();
                energy *= energyMod;
            }
            rfp.setEnergy(energy);
        }
    }

    public static TransputStatsMap calcTransputStats(
            TransputMap recipeTransputMap,
            double time,
            Proliferator proliferator,
            Proliferator.ProliferatorMode proliferatorMode,
            boolean isOutput
    ) {
        TransputStatsMap transputStatsMap = new TransputStatsMap();

        for (Map.Entry<String, Double> entry : recipeTransputMap.entrySet()) {
            String itemK = entry.getKey();
            Double itemQty = entry.getValue();

            if (isOutput && Proliferator.ProliferatorMode.EXTRA.equals(proliferatorMode)) {
                itemQty *= 1 + proliferator.getExtraProducts();
            }

            TransputStats transputStats = new TransputStats();
            transputStats.setItemK(itemK);
            transputStats.setQuantity(itemQty);
            if (time != 0) transputStats.setItemsPerSecond(itemQty / time);

            transputStatsMap.put(itemK, transputStats);
        }

        return transputStatsMap;
    }


    private static void calcRawCosts(String proliferatorItemK, Proliferator.ProliferatorMode proliferatorMode, Rfp rfp, Recipe recipe) {
        if (proliferatorMode == null) {
            rfp.setRawCostMap(recipe.getRecipeRawCost());
        } else if (proliferatorMode.equals(Proliferator.ProliferatorMode.EXTRA)) {
            rfp.setRawCostMap(recipe.getRecipeRawCostPrExtra().get(proliferatorItemK));
        } else if (proliferatorMode.equals(Proliferator.ProliferatorMode.SPEED)) {
            rfp.setRawCostMap(recipe.getRecipeRawCostPrSpeed().get(proliferatorItemK));
        }
        if (rfp.getRawCostMap() != null) {
            rfp.setRawCostTotal(rfp.getRawCostMap().calcTotal());
        }
    }


    private static void calcRawCostPercentages(Rfp rfp, Recipe recipe, Proliferator proliferator) {
        rfp.setRawCostMapPercetageOfTotal(new TransputMap());
        if (rfp.getRawCostMap() != null) {
            for (Map.Entry<String, Double> entry : rfp.getRawCostMap().entrySet()) {
                String itemK = entry.getKey();
                Double itemQty = entry.getValue();

                Double percentageOfTotal = itemQty / rfp.getRawCostTotal();
                AppUtil.securePut(rfp.getRawCostMapPercetageOfTotal(), itemK, percentageOfTotal);

                if (proliferator != null) {
                    if (rfp.getRawCostMapPercetageOfNoPr() == null) rfp.setRawCostMapPercetageOfNoPr(new TransputMap());
                    Double noPrItemQty = recipe.getRecipeRawCost().get(itemK);
                    if (noPrItemQty != null) {
                        Double percentageOfNoPr = itemQty / noPrItemQty;
                        AppUtil.securePut(rfp.getRawCostMapPercetageOfNoPr(), itemK, percentageOfNoPr);
                    }
                }
            }
        }
    }

}
