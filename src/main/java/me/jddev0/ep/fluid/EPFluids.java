package me.jddev0.ep.fluid;

import me.jddev0.ep.api.EPAPI;
import net.minecraft.core.Registry;
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

public final class EPFluids {
    private EPFluids() {}

    public static final FlowingFluid DIRTY_WATER = registerFluid("dirty_water", new DirtyWaterFluid.Source());
    public static final FlowingFluid FLOWING_DIRTY_WATER = registerFluid("flowing_dirty_water", new DirtyWaterFluid.Flowing());
    public static final LiquidBlock DIRTY_WATER_BLOCK = createBlock("dirty_water",
            new DirtyWaterFluidBlock(DIRTY_WATER, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final BucketItem DIRTY_WATER_BUCKET_ITEM = createItem("dirty_water_bucket",
            new BucketItem(DIRTY_WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final FlowingFluid LIQUID_XP = registerFluid("liquid_xp", new LiquidXPFluid.Source());
    public static final FlowingFluid FLOWING_LIQUID_XP = registerFluid("flowing_liquid_xp", new LiquidXPFluid.Flowing());
    public static final LiquidBlock LIQUID_XP_BLOCK = createBlock("liquid_xp",
            new LiquidXPFluidBlock(LIQUID_XP, BlockBehaviour.Properties.ofFullCopy(Blocks.LAVA).lightLevel(state -> 10)));
    public static final BucketItem LIQUID_XP_BUCKET_ITEM = createItem("liquid_xp_bucket",
            new BucketItem(LIQUID_XP, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    private static <T extends Fluid> T registerFluid(String name, T fluid) {
        return Registry.register(BuiltInRegistries.FLUID, EPAPI.id(name), fluid);
    }

    private static <T extends Block> T createBlock(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, EPAPI.id(name), block);
    }

    public static <T extends Item> T createItem(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, EPAPI.id(name), item);
    }

    public static void register() {

    }
}
