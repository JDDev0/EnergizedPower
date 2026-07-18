package me.jddev0.ep.machine.configuration;

import com.mojang.serialization.Codec;
import me.jddev0.ep.fluid.InputOutputFluidStorage;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class IOConfiguration {
    public static final Codec<IOConfiguration> CODEC = Codec.INT_STREAM.
            xmap(slotGroupIdStream -> {
                int[] slotGroupIds = slotGroupIdStream.toArray();
                if(slotGroupIds.length != 6)
                    throw new IllegalArgumentException("Slot group ids must be of size 6");

                return new IOConfiguration(slotGroupIds);
            }, ioConfiguration -> Arrays.stream(ioConfiguration.slotGroupIds));

    public static final StreamCodec<FriendlyByteBuf, IOConfiguration> STREAM_CODEC = StreamCodec.of((buffer, entry) -> {
        for(int i = 0;i < 6;i++)
            buffer.writeVarInt(entry.slotGroupIds[i]);
    }, buffer -> {
        int[] slotGroupIds = new int[6];
        for(int i = 0;i < 6;i++)
            slotGroupIds[i] = buffer.readVarInt();

        return new IOConfiguration(slotGroupIds);
    });

    //Direction.ordinal() to slot group id (1 per direction => 6 entries)
    private final int[] slotGroupIds;

    public IOConfiguration() {
        slotGroupIds = new int[6];
        for(int i = 0;i < 6;i++)
            slotGroupIds[i] = -1;
    }

    private IOConfiguration(int[] slotGroupIds) {
        this.slotGroupIds = slotGroupIds;
    }

    /**
     * @param direction Relative Direction
     * @param slotGroupId valid values: -1 (not configured), slot group index < slot group count
     */
    public void setSlotGroupId(RelativeDirection direction, int slotGroupId) {
        slotGroupIds[direction.ordinal()] = slotGroupId;
    }

    /**
     * @param direction Relative Direction
     * @return slotGroupId: -1 (not configured), slot group index < slot group count
     */
    public int getSlotGroupId(RelativeDirection direction) {
        return slotGroupIds[direction.ordinal()];
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || getClass() != o.getClass()) return false;
        IOConfiguration that = (IOConfiguration)o;
        return Objects.deepEquals(slotGroupIds, that.slotGroupIds);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(slotGroupIds);
    }

    public boolean validate(int slotGroupCount) {
        for(int i = 0;i < 6;i++) {
            int id = slotGroupIds[i];

            if(id < -1 || id >= slotGroupCount)
                return false;
        }

        return true;
    }

    public InputOutputItemHandler createSidedItemHandlerFor(
            @NotNull List<@NotNull SlotGroup> slotGroups, @NotNull ResourceHandler<ItemResource> handler,
            @NotNull Direction facing, @NotNull Direction side
    ) {
        int slotGroupId = getSlotGroupId(RelativeDirection.resolve(facing, side));
        if(slotGroupId != -1 && slotGroupId >= slotGroups.size())
            return null;

        if(slotGroupId == -1)
            return null; //Output should be completely disconnected if not configured

        final SlotGroup slotGroup = slotGroups.get(slotGroupId);
        return new InputOutputItemHandler(handler, (i, stack) -> {
            return slotGroup.getSlots().stream().anyMatch(entry -> entry.mode().canInput() && entry.index() == i);
        }, i -> {
            return slotGroup.getSlots().stream().anyMatch(entry -> entry.mode().canOutput() && entry.index() == i);
        });
    }

    public InputOutputFluidStorage createSidedFluidHandlerFor(
            @NotNull List<@NotNull SlotGroup> slotGroups, @NotNull ResourceHandler<FluidResource> handler,
            @NotNull Direction facing, @NotNull Direction side
    ) {
        int slotGroupId = getSlotGroupId(RelativeDirection.resolve(facing, side));
        if(slotGroupId != -1 && slotGroupId >= slotGroups.size())
            return null;

        if(slotGroupId == -1)
            return null; //Output should be completely disconnected if not configured

        final SlotGroup slotGroup = slotGroups.get(slotGroupId);
        return new InputOutputFluidStorage(handler, (i, stack) -> {
            return slotGroup.getSlots().stream().anyMatch(entry -> entry.mode().canInput() && entry.index() == i);
        }, i -> {
            return slotGroup.getSlots().stream().anyMatch(entry -> entry.mode().canOutput() && entry.index() == i);
        });
    }
}
