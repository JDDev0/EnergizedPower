package me.jddev0.ep.fluid;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.ModItems;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public final class ModFluids {
    private ModFluids() {}

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, EnergizedPowerMod.MODID);

    public static final Supplier<FlowingFluid> DIRTY_WATER = FLUIDS.register("dirty_water",
            () -> new ForgeFlowingFluid.Source(ModFluids.DIRTY_WATER_PROPS));
    public static final Supplier<FlowingFluid> FLOWING_DIRTY_WATER = FLUIDS.register("flowing_dirty_water",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.DIRTY_WATER_PROPS));
    public static final RegistryObject<LiquidBlock> DIRTY_WATER_BLOCK = ModBlocks.BLOCKS.register("dirty_water",
            () -> new LiquidBlock(DIRTY_WATER, BlockBehaviour.Properties.ofFullCopy(Blocks.WATER)));
    public static final RegistryObject<BucketItem> DIRTY_WATER_BUCKET_ITEM = ModItems.ITEMS.register("dirty_water_bucket",
            () -> new BucketItem(DIRTY_WATER, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));
    private static final ForgeFlowingFluid.Properties DIRTY_WATER_PROPS = new ForgeFlowingFluid.Properties(
            ModFluidTypes.DIRTY_WATER_FLUID_TYPE, DIRTY_WATER, FLOWING_DIRTY_WATER
    ).explosionResistance(100.f).block(DIRTY_WATER_BLOCK).bucket(DIRTY_WATER_BUCKET_ITEM);

    public static void register(IEventBus modEventBus) {
        FLUIDS.register(modEventBus);
    }
}
