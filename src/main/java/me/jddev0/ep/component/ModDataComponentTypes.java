package me.jddev0.ep.component;

import com.mojang.serialization.Codec;
import me.jddev0.ep.EnergizedPowerMod;
import net.minecraft.component.DataComponentType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Direction;

import java.util.function.UnaryOperator;

public final class ModDataComponentTypes {
    private ModDataComponentTypes() {}

    public static <T> DataComponentType<T> registerDataComponentType(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, new Identifier(EnergizedPowerMod.MODID, name),
                builderOperator.apply(DataComponentType.builder()).build());
    }

    public static final DataComponentType<Boolean> ACTIVE = registerDataComponentType("active", builder ->
        builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));

    public static final DataComponentType<Boolean> WORKING = registerDataComponentType("working", builder ->
        builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL));

    public static final DataComponentType<Integer> PROGRESS = registerDataComponentType("progress", builder ->
            builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));

    public static final DataComponentType<Integer> MAX_PROGRESS = registerDataComponentType("max_progress", builder ->
            builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));

    public static final DataComponentType<Long> ENERGY_PRODUCTION_LEFT = registerDataComponentType("energy_production_left", builder ->
            builder.codec(Codec.LONG).packetCodec(PacketCodecs.VAR_LONG));

    public static final DataComponentType<CurrentItemStackComponent> CURRENT_ITEM = registerDataComponentType("current_item", builder ->
            builder.codec(CurrentItemStackComponent.CODEC).packetCodec(CurrentItemStackComponent.PACKET_CODEC));

    public static final DataComponentType<InventoryComponent> INVENTORY = registerDataComponentType("inventory", builder ->
            builder.codec(InventoryComponent.CODEC).packetCodec(InventoryComponent.PACKET_CODEC));

    public static final DataComponentType<Direction> CURRENT_FACE = registerDataComponentType("current_face", builder ->
        builder.codec(Direction.CODEC).packetCodec(Direction.PACKET_CODEC));

    public static final DataComponentType<Integer> ACTION_COOLDOWN = registerDataComponentType("action_cooldown", builder ->
        builder.codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));

    public static final DataComponentType<DimensionalPositionComponent> DIMENSIONAL_POSITION =
            registerDataComponentType("dimensional_position", builder ->
                    builder.codec(DimensionalPositionComponent.CODEC).packetCodec(DimensionalPositionComponent.PACKET_CODEC));

    public static final DataComponentType<Unit> NO_REPAIR = registerDataComponentType("no_repair", builder ->
            builder.codec(Codec.unit(Unit.INSTANCE)).packetCodec(PacketCodec.unit(Unit.INSTANCE)));

    public static void register() {

    }
}
