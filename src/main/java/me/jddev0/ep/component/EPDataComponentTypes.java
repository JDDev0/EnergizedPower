package me.jddev0.ep.component;

import com.mojang.serialization.Codec;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Direction;

import java.util.function.UnaryOperator;

public final class EPDataComponentTypes {
    private EPDataComponentTypes() {}

    public static <T> ComponentType<T> registerDataComponentType(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, EPAPI.id(name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static final ComponentType<Long> ENERGY = registerDataComponentType("energy", builder ->
            builder.codec(CodecFix.NON_NEGATIVE_LONG).packetCodec(PacketCodecs.VAR_LONG));

    public static final ComponentType<Boolean> ACTIVE = registerDataComponentType("active", builder ->
            builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN));

    public static final ComponentType<Boolean> WORKING = registerDataComponentType("working", builder ->
            builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN));

    public static final ComponentType<Integer> PROGRESS = registerDataComponentType("progress", builder ->
            builder.codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));

    public static final ComponentType<Integer> MAX_PROGRESS = registerDataComponentType("max_progress", builder ->
            builder.codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));

    public static final ComponentType<Long> ENERGY_PRODUCTION_LEFT = registerDataComponentType("energy_production_left", builder ->
            builder.codec(Codec.LONG).packetCodec(PacketCodecs.VAR_LONG));

    public static final ComponentType<CurrentItemStackComponent> CURRENT_ITEM = registerDataComponentType("current_item", builder ->
            builder.codec(CurrentItemStackComponent.CODEC).packetCodec(CurrentItemStackComponent.PACKET_CODEC));

    public static final ComponentType<InventoryComponent> INVENTORY = registerDataComponentType("inventory", builder ->
            builder.codec(InventoryComponent.CODEC).packetCodec(InventoryComponent.PACKET_CODEC));

    public static final ComponentType<Direction> CURRENT_FACE = registerDataComponentType("current_face", builder ->
            builder.codec(Direction.CODEC).packetCodec(Direction.PACKET_CODEC));

    public static final ComponentType<Integer> ACTION_COOLDOWN = registerDataComponentType("action_cooldown", builder ->
            builder.codec(Codecs.NON_NEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT));

    public static final ComponentType<DimensionalPositionComponent> DIMENSIONAL_POSITION =
            registerDataComponentType("dimensional_position", builder ->
                    builder.codec(DimensionalPositionComponent.CODEC).packetCodec(DimensionalPositionComponent.PACKET_CODEC));

    public static final ComponentType<Unit> NO_REPAIR = registerDataComponentType("no_repair", builder ->
            builder.codec(Unit.CODEC).packetCodec(PacketCodec.unit(Unit.INSTANCE)));

    public static void register() {

    }
}
