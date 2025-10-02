package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class FluidAnalyzerItem extends EnergizedPowerEnergyItem {
    public static final long ENERGY_CONSUMPTION_PER_USE = ModConfigs.COMMON_FLUID_ANALYZER_ENERGY_CONSUMPTION_PER_USE.getValue();
    public static final long ENERGY_CAPACITY = ModConfigs.COMMON_FLUID_ANALYZER_CAPACITY.getValue();

    public FluidAnalyzerItem(Item.Settings props) {
        super(props, ENERGY_CAPACITY, ModConfigs.COMMON_FLUID_ANALYZER_TRANSFER_RATE.getValue(), 0);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, tooltip, type);

        if(MinecraftClient.getInstance().isShiftPressed()) {
            tooltip.accept(Text.translatable("tooltip.energizedpower.fluid_analyzer.txt.shift.1").formatted(Formatting.GRAY));
            tooltip.accept(Text.translatable("tooltip.energizedpower.fluid_analyzer.txt.shift.2",
                    EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).formatted(Formatting.GRAY));
        }else {
            tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    private void useItem(ItemStack itemStack, PlayerEntity player, List<Text> lines) {
        if(getEnergy(itemStack) >= ENERGY_CONSUMPTION_PER_USE)
            setEnergy(itemStack, getEnergy(itemStack) - ENERGY_CONSUMPTION_PER_USE);

        for(Text component:lines)
            player.sendMessage(component, false);
        player.sendMessage(Text.empty(), false);
    }

    private void addOutputTextForFluidStorage(List<Text> components, @Nullable Storage<FluidVariant> fluidStorage, boolean blockFaceSpecificInformation) {
        if(fluidStorage == null) {
            components.add(Text.translatable("txt.energizedpower.fluid_analyzer.no_fluid_block" + (blockFaceSpecificInformation?"_side":"")).
                    formatted(Formatting.RED));

            return;
        }

        int tankCount = 0;
        Iterator<StorageView<FluidVariant>> fluidStorageIterator = fluidStorage.iterator();
        while(fluidStorageIterator.hasNext() && tankCount < 100) {
            //Limit max iterations to 100
            tankCount++;

            fluidStorageIterator.next();
        }

        components.add(Text.translatable("txt.energizedpower.fluid_analyzer.fluid_output.tank_count" + (blockFaceSpecificInformation?"_side":""),
                tankCount).formatted(Formatting.BLUE));

        int tankNum = 0;
        for(StorageView<FluidVariant> fluidView:fluidStorage) {
            if(++tankNum > 100) //Limit max iterations to 100
                break;

            boolean fluidEmpty = fluidView.isResourceBlank();

            long fluidAmount = fluidEmpty?0:fluidView.getAmount();

            components.add(Text.literal("• ").append(
                    Text.translatable("txt.energizedpower.fluid_analyzer.fluid_output.tank_fluid_content",
                            tankNum, fluidEmpty?"":Text.translatable(fluidView.getResource().getFluid().getDefaultState().getBlockState().getBlock().getTranslationKey()).append(" "),
                    FluidUtils.getFluidAmountWithPrefix(FluidUtils.convertDropletsToMilliBuckets(fluidAmount)),
                            FluidUtils.getFluidAmountWithPrefix(FluidUtils.convertDropletsToMilliBuckets(fluidView.getCapacity())))
            ).formatted(Formatting.BLUE));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext useOnContext) {
        World level = useOnContext.getWorld();
        if(level.isClient())
            return ActionResult.SUCCESS;

        ItemStack stack = useOnContext.getStack();

        if(getEnergy(stack) < ENERGY_CONSUMPTION_PER_USE) {
            useItem(stack, useOnContext.getPlayer(), List.of(
                    Text.translatable("txt.energizedpower.fluid_analyzer.no_energy_left",
                            EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).formatted(Formatting.RED)
            ));

            return ActionResult.SUCCESS;
        }

        BlockPos blockPos = useOnContext.getBlockPos();

        List<Text> components = new ArrayList<>();
        components.add(level.getBlockState(blockPos).getBlock().getName().formatted(Formatting.UNDERLINE, Formatting.AQUA));

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(blockEntity == null) {
            components.add(Text.translatable("txt.energizedpower.fluid_analyzer.no_fluid_block").formatted(Formatting.RED));

            useItem(stack, useOnContext.getPlayer(), components);

            return ActionResult.SUCCESS;
        }

        addOutputTextForFluidStorage(components, FluidStorage.SIDED.find(level, blockPos, null), false);

        components.add(Text.translatable("txt.energizedpower.fluid_analyzer.output_side_information").formatted(Formatting.GOLD));
        addOutputTextForFluidStorage(components, FluidStorage.SIDED.find(level, blockPos, useOnContext.getSide()), true);

        useItem(stack, useOnContext.getPlayer(), components);

        return ActionResult.SUCCESS;
    }
}
