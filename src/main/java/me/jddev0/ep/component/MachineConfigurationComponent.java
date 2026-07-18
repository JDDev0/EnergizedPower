package me.jddev0.ep.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.jddev0.ep.machine.configuration.ComparatorMode;
import me.jddev0.ep.machine.configuration.IOConfiguration;
import me.jddev0.ep.machine.configuration.RedstoneMode;
import me.jddev0.ep.machine.configuration.SlotType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class MachineConfigurationComponent {
    public static final Codec<MachineConfigurationComponent> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(RedstoneMode.CODEC.fieldOf("redstone_mode").forGetter((component) -> {
            return component.redstoneMode;
        }), ComparatorMode.CODEC.fieldOf("comparator_mode").forGetter((component) -> {
            return component.comparatorMode;
        }), Codec.unboundedMap(SlotType.CODEC, IOConfiguration.CODEC).fieldOf("io_configurations").forGetter((component) -> {
            return component.ioConfigurations;
        })).apply(instance, MachineConfigurationComponent::new);
    });

    public static final StreamCodec<FriendlyByteBuf, MachineConfigurationComponent> STREAM_CODEC = StreamCodec.ofMember(
            MachineConfigurationComponent::write, MachineConfigurationComponent::new);

    public static Map<SlotType, IOConfiguration> readIOConfigurations(FriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        Map<SlotType, IOConfiguration> ioConfigurations = HashMap.newHashMap(count);

        for(int i = 0;i < count;i++) {
            SlotType slotType = buffer.readEnum(SlotType.class);
            IOConfiguration ioConfiguration = IOConfiguration.STREAM_CODEC.decode(buffer);
            ioConfigurations.put(slotType, ioConfiguration);
        }

        return ioConfigurations;
    }

    private final RedstoneMode redstoneMode;
    private final ComparatorMode comparatorMode;
    private final Map<SlotType, IOConfiguration> ioConfigurations;

    public MachineConfigurationComponent(RedstoneMode redstoneMode, ComparatorMode comparatorMode, Map<SlotType, IOConfiguration> ioConfigurations) {
        this.redstoneMode = redstoneMode;
        this.comparatorMode = comparatorMode;
        this.ioConfigurations = Map.copyOf(ioConfigurations);
    }

    public MachineConfigurationComponent(FriendlyByteBuf buffer) {
        this(buffer.readEnum(RedstoneMode.class), buffer.readEnum(ComparatorMode.class), readIOConfigurations(buffer));
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeEnum(redstoneMode);
        buffer.writeEnum(comparatorMode);

        Set<Map.Entry<SlotType, IOConfiguration>> ioConfigurationSet = ioConfigurations.entrySet();
        buffer.writeVarInt(ioConfigurationSet.size());
        for(Map.Entry<SlotType, IOConfiguration> ioConfiguration:ioConfigurationSet) {
            buffer.writeEnum(ioConfiguration.getKey());
            IOConfiguration.STREAM_CODEC.encode(buffer, ioConfiguration.getValue());
        }
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public ComparatorMode getComparatorMode() {
        return comparatorMode;
    }

    public Map<SlotType, IOConfiguration> getIOConfigurations() {
        return ioConfigurations;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || getClass() != o.getClass()) return false;
        MachineConfigurationComponent that = (MachineConfigurationComponent)o;
        return redstoneMode == that.redstoneMode && comparatorMode == that.comparatorMode && Objects.equals(ioConfigurations, that.ioConfigurations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(redstoneMode, comparatorMode, ioConfigurations);
    }
}
