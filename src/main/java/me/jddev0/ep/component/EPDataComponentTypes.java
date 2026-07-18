package me.jddev0.ep.component;

import com.mojang.serialization.Codec;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.codec.CodecFix;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.UnaryOperator;

public final class EPDataComponentTypes {
    private EPDataComponentTypes() {}

    public static <T> DataComponentType<T> registerDataComponentType(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, EPAPI.id(name),
                builderOperator.apply(DataComponentType.builder()).build());
    }

    public static final DataComponentType<Long> ENERGY = registerDataComponentType("energy", builder ->
            builder.persistent(CodecFix.NON_NEGATIVE_LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

    public static final DataComponentType<Boolean> ACTIVE = registerDataComponentType("active", builder ->
            builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DataComponentType<Boolean> WORKING = registerDataComponentType("working", builder ->
            builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DataComponentType<Integer> PROGRESS = registerDataComponentType("progress", builder ->
            builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DataComponentType<Integer> MAX_PROGRESS = registerDataComponentType("max_progress", builder ->
            builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DataComponentType<Long> ENERGY_PRODUCTION_LEFT = registerDataComponentType("energy_production_left", builder ->
            builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));

    public static final DataComponentType<CurrentItemStackComponent> CURRENT_ITEM = registerDataComponentType("current_item", builder ->
            builder.persistent(CurrentItemStackComponent.CODEC).networkSynchronized(CurrentItemStackComponent.STREAM_CODEC));

    public static final DataComponentType<InventoryComponent> INVENTORY = registerDataComponentType("inventory", builder ->
            builder.persistent(InventoryComponent.CODEC).networkSynchronized(InventoryComponent.STREAM_CODEC));

    public static final DataComponentType<Direction> CURRENT_FACE = registerDataComponentType("current_face", builder ->
            builder.persistent(Direction.CODEC).networkSynchronized(Direction.STREAM_CODEC));

    public static final DataComponentType<Integer> ACTION_COOLDOWN = registerDataComponentType("action_cooldown", builder ->
            builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DataComponentType<DimensionalPositionComponent> DIMENSIONAL_POSITION =
            registerDataComponentType("dimensional_position", builder ->
                    builder.persistent(DimensionalPositionComponent.CODEC).networkSynchronized(DimensionalPositionComponent.STREAM_CODEC));

    public static final DataComponentType<Block> SOURCE_BLOCK =
            registerDataComponentType("source_block", builder ->
                    builder.persistent(BuiltInRegistries.BLOCK.byNameCodec()).
                            networkSynchronized(ByteBufCodecs.registry(Registries.BLOCK)));

    public static final DataComponentType<BlockEntityType<?>> SOURCE_BLOCK_ENTITY =
            registerDataComponentType("source_block_entity", builder ->
                    builder.persistent(BuiltInRegistries.BLOCK_ENTITY_TYPE.byNameCodec()).
                            networkSynchronized(ByteBufCodecs.registry(Registries.BLOCK_ENTITY_TYPE)));

    public static final DataComponentType<MachineConfigurationComponent> MACHINE_CONFIGURATION =
            registerDataComponentType("machine_configuration", builder ->
                    builder.persistent(MachineConfigurationComponent.CODEC).networkSynchronized(MachineConfigurationComponent.STREAM_CODEC));

    public static final DataComponentType<Unit> NO_REPAIR = registerDataComponentType("no_repair", builder ->
            builder.persistent(Codec.unit(Unit.INSTANCE)).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)));

    public static void register() {

    }
}
