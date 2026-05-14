package me.jddev0.ep.inventory;

import net.minecraft.world.inventory.ContainerData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CombinedContainerData implements ContainerData {
    private final int dataCount;
    private final ContainerData[] backingData;
    private final Map<Integer, Integer> backingDataIndexLookup;
    private final Map<Integer, Integer> dataOffsetIndexLookup;

    public CombinedContainerData(ContainerData... backingData) {
        this.backingData = Arrays.copyOf(backingData, backingData.length);
        this.backingDataIndexLookup = new HashMap<>();
        this.dataOffsetIndexLookup = new HashMap<>();

        int dataIndex = 0;
        for(int i = 0;i < backingData.length;i++) {
            int dataCount = backingData[i].getCount();

            for(int j = 0;j < dataCount;j++) {
                backingDataIndexLookup.put(dataIndex + j, i);
                dataOffsetIndexLookup.put(dataIndex + j, dataIndex);
            }

            dataIndex += dataCount;
        }

        this.dataCount = dataIndex;
    }

    @Override
    public int get(int index) {
        Integer backingDataIndex = backingDataIndexLookup.get(index);
        if(backingDataIndex == null)
            return 0;

        int dataOffsetIndex = dataOffsetIndexLookup.get(index);
        return backingData[backingDataIndex].get(index - dataOffsetIndex);
    }

    @Override
    public void set(int index, int value) {
        Integer backingDataIndex = backingDataIndexLookup.get(index);
        if(backingDataIndex == null)
            return;

        int dataOffsetIndex = dataOffsetIndexLookup.get(index);
        backingData[backingDataIndex].set(index - dataOffsetIndex, value);
    }

    @Override
    public int getCount() {
        return dataCount;
    }
}
