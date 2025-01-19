package me.jddev0.ep.fluid;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.item.EPItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;

public final class EPFluids {
    private EPFluids() {}

    public static final FlowableFluid DIRTY_WATER = registerFluid("dirty_water", new DirtyWaterFluid.Source());
    public static final FlowableFluid FLOWING_DIRTY_WATER = registerFluid("flowing_dirty_water", new DirtyWaterFluid.Flowing());
    public static final FluidBlock DIRTY_WATER_BLOCK = createBlock("dirty_water",
            props -> new DirtyWaterFluidBlock(DIRTY_WATER, props), AbstractBlock.Settings.copy(Blocks.WATER));
    public static final BucketItem DIRTY_WATER_BUCKET_ITEM = createItem("dirty_water_bucket",
            DIRTY_WATER, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1));

    private static <T extends Fluid> T registerFluid(String name, T fluid) {
        return Registry.register(Registries.FLUID, EPAPI.id(name), fluid);
    }

    private static <T extends Block> T createBlock(String name, Function<AbstractBlock.Settings, T> factory,
                                                   AbstractBlock.Settings props) {
        return EPBlocks.registerBlock(name, factory, props);
    }

    public static BucketItem createItem(String name, Fluid fluid, Item.Settings props) {
        return EPItems.registerItem(name, p -> new BucketItem(fluid, p), props);
    }

    public static void register() {

    }
}
