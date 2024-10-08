package me.jddev0.ep.fluid;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.item.ModCreativeModeTab;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ModFluids {
    private ModFluids() {}

    public static final FlowableFluid DIRTY_WATER = registerFluid("dirty_water", new DirtyWaterFluid.Source());
    public static final FlowableFluid FLOWING_DIRTY_WATER = registerFluid("flowing_dirty_water", new DirtyWaterFluid.Flowing());
    public static final FluidBlock DIRTY_WATER_BLOCK = createBlock("dirty_water",
            new DirtyWaterFluidBlock(DIRTY_WATER, FabricBlockSettings.copyOf(Blocks.WATER)));
    public static final BucketItem DIRTY_WATER_BUCKET_ITEM = createItem("dirty_water_bucket",
            new BucketItem(DIRTY_WATER, new FabricItemSettings().group(ModCreativeModeTab.ENERGIZED_POWER_TAB).recipeRemainder(Items.BUCKET).maxCount(1)));

    private static <T extends Fluid> T registerFluid(String name, T fluid) {
        return Registry.register(Registry.FLUID, EPAPI.id(name), fluid);
    }

    private static <T extends Block> T createBlock(String name, T block) {
        return Registry.register(Registry.BLOCK, EPAPI.id(name), block);
    }

    public static <T extends Item> T createItem(String name, T item) {
        return Registry.register(Registry.ITEM, EPAPI.id(name), item);
    }

    public static void register() {

    }
}
