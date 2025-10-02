package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FluidAnalyzerItem extends EnergizedPowerEnergyItem {
    public static final int ENERGY_CONSUMPTION_PER_USE = ModConfigs.COMMON_FLUID_ANALYZER_ENERGY_CONSUMPTION_PER_USE.getValue();
    public static final int ENERGY_CAPACITY = ModConfigs.COMMON_FLUID_ANALYZER_CAPACITY.getValue();

    public FluidAnalyzerItem(Properties props) {
        super(props, () -> new ReceiveOnlyEnergyStorage(0, ENERGY_CAPACITY, ModConfigs.COMMON_FLUID_ANALYZER_TRANSFER_RATE.getValue()));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, components, tooltipFlag);

        if(Minecraft.getInstance().hasShiftDown()) {
            components.accept(Component.translatable("tooltip.energizedpower.fluid_analyzer.txt.shift.1").withStyle(ChatFormatting.GRAY));
            components.accept(Component.translatable("tooltip.energizedpower.fluid_analyzer.txt.shift.2",
                    EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).withStyle(ChatFormatting.GRAY));
        }else {
            components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    private void useItem(ItemStack itemStack, Player player, List<Component> lines) {
        if(getEnergy(itemStack) >= ENERGY_CONSUMPTION_PER_USE)
            setEnergy(itemStack, getEnergy(itemStack) - ENERGY_CONSUMPTION_PER_USE);

        for(Component component:lines)
            player.displayClientMessage(component, false);
        player.displayClientMessage(Component.empty(), false);
    }

    private void addOutputTextForFluidStorage(List<Component> components, @Nullable IFluidHandler fluidStorage, boolean blockFaceSpecificInformation) {
        if(fluidStorage == null) {
            components.add(Component.translatable("txt.energizedpower.fluid_analyzer.no_fluid_block" + (blockFaceSpecificInformation?"_side":"")).
                    withStyle(ChatFormatting.RED));

            return;
        }

        components.add(Component.translatable("txt.energizedpower.fluid_analyzer.fluid_output.tank_count" + (blockFaceSpecificInformation?"_side":""),
                fluidStorage.getTanks()).withStyle(ChatFormatting.BLUE));

        for(int i = 0;i < fluidStorage.getTanks();i++) {
            boolean fluidEmpty = fluidStorage.getFluidInTank(i).isEmpty();

            int fluidAmount = fluidEmpty?0:fluidStorage.getFluidInTank(i).getAmount();

            components.add(Component.literal("• ").append(
                    Component.translatable("txt.energizedpower.fluid_analyzer.fluid_output.tank_fluid_content",
                    i + 1, fluidEmpty?"":Component.translatable(fluidStorage.getFluidInTank(i).getDescriptionId()).append(" "),
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(fluidStorage.getTankCapacity(i)))
            ).withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        if(getEnergy(stack) < ENERGY_CONSUMPTION_PER_USE) {
            useItem(stack, useOnContext.getPlayer(), List.of(
                    Component.translatable("txt.energizedpower.fluid_analyzer.no_energy_left",
                            EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).withStyle(ChatFormatting.RED)
            ));

            return InteractionResult.SUCCESS;
        }

        BlockPos blockPos = useOnContext.getClickedPos();

        List<Component> components = new ArrayList<>();
        components.add(level.getBlockState(blockPos).getBlock().getName().withStyle(ChatFormatting.UNDERLINE, ChatFormatting.AQUA));

        BlockEntity blockEntity = level.getBlockEntity(blockPos);

        IFluidHandler fluidStorage = level.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, level.getBlockState(blockPos), blockEntity, null);
        addOutputTextForFluidStorage(components, fluidStorage, false);

        components.add(Component.translatable("txt.energizedpower.fluid_analyzer.output_side_information").withStyle(ChatFormatting.GOLD));
        IFluidHandler fluidStorageSided = level.getCapability(Capabilities.FluidHandler.BLOCK, blockPos, level.getBlockState(blockPos), blockEntity, useOnContext.getClickedFace());
        addOutputTextForFluidStorage(components, fluidStorageSided, true);

        useItem(stack, useOnContext.getPlayer(), components);

        return InteractionResult.SUCCESS;
    }
}
