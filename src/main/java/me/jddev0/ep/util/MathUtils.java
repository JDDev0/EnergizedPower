package me.jddev0.ep.util;

import java.util.List;

public final class MathUtils {
    private MathUtils() {}

    /**
     * @param amountToDistribute total amount to be distributed
     * @param containerCapacities must contain positive values only<br>
     *                            <b>Will be modified</b>: This list contains the remaining capacities after calling this method (Might contain 0-values)
     * @return The distribution
     */
    public static int[] distributeEvenly(int amountToDistribute, List<Integer> containerCapacities) {
        int remainingContainerCount = containerCapacities.size();
        int[] amountDistributed = new int[remainingContainerCount];

        while(amountToDistribute > 0 && remainingContainerCount > 0) {
            int amountPerContainer = amountToDistribute / remainingContainerCount;
            int leftoverAmount = amountToDistribute - amountPerContainer * remainingContainerCount;

            for(int i = 0;i < containerCapacities.size();i++) {
                int remainingCapacity = containerCapacities.get(i);
                if(remainingCapacity <= 0)
                    continue;

                int amount = Math.min(amountPerContainer, remainingCapacity);
                if(leftoverAmount > 0 && amount < remainingCapacity) {
                    amount++;
                    leftoverAmount--;
                }

                containerCapacities.set(i, remainingCapacity - amount);
                amountDistributed[i] += amount;

                amountToDistribute -= amount;

                if(amount == remainingCapacity)
                    remainingContainerCount--;
            }
        }

        return amountDistributed;
    }
}
