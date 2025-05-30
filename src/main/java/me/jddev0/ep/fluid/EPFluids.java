package me.jddev0.ep.fluid;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public final class EPFluids {
    private EPFluids() {}

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, EPAPI.MOD_ID);

    public static final DeferredHolder<Fluid, FlowingFluid> DIRTY_WATER = FLUIDS.register("dirty_water",
            () -> new BaseFlowingFluid.Source(EPFluids.DIRTY_WATER_PROPS));
    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_DIRTY_WATER = FLUIDS.register("flowing_dirty_water",
            () -> new BaseFlowingFluid.Flowing(EPFluids.DIRTY_WATER_PROPS));
    public static final DeferredBlock<LiquidBlock> DIRTY_WATER_BLOCK = createBlock("dirty_water",
            props -> new LiquidBlock(DIRTY_WATER.get(), props), BlockBehaviour.Properties.ofFullCopy(Blocks.WATER));
    public static final DeferredItem<BucketItem> DIRTY_WATER_BUCKET_ITEM = createItem("dirty_water_bucket",
            DIRTY_WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1));
    private static final BaseFlowingFluid.Properties DIRTY_WATER_PROPS = new BaseFlowingFluid.Properties(
            EPFluidTypes.DIRTY_WATER_FLUID_TYPE, DIRTY_WATER, FLOWING_DIRTY_WATER
    ).explosionResistance(100.f).block(DIRTY_WATER_BLOCK).bucket(DIRTY_WATER_BUCKET_ITEM);

    private static <T extends Block> DeferredBlock<T> createBlock(String name, Function<BlockBehaviour.Properties, T> factory,
                                                   BlockBehaviour.Properties props) {
        return EPBlocks.registerBlock(name, factory, props);
    }

    public static DeferredItem<BucketItem> createItem(String name, DeferredHolder<Fluid, FlowingFluid> fluid, Item.Properties props) {
        return EPItems.registerItem(name, p -> new BucketItem(fluid.get(), p), props);
    }

    public static void register(IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
    }
}
