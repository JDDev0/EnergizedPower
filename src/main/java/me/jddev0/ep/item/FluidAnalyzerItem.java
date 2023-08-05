package me.jddev0.ep.item;

import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class FluidAnalyzerItem extends EnergizedPowerEnergyItem {
    public static final int ENERGY_CONSUMPTION_PER_USE = 8;
    public static final int ENERGY_CAPACITY = 2048;

    public FluidAnalyzerItem(Properties props) {
        super(props, () -> new ReceiveOnlyEnergyStorage(0, ENERGY_CAPACITY, 32));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.fluid_analyzer.txt.shift.1").withStyle(ChatFormatting.GRAY));
            components.add(Component.translatable("tooltip.energizedpower.fluid_analyzer.txt.shift.2",
                    EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).withStyle(ChatFormatting.GRAY));
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    private void useItem(ItemStack itemStack, Player player, List<Component> lines) {
        if(getEnergy(itemStack) >= ENERGY_CONSUMPTION_PER_USE)
            setEnergy(itemStack, getEnergy(itemStack) - ENERGY_CONSUMPTION_PER_USE);

        for(Component component:lines)
            player.sendSystemMessage(component);
        player.sendSystemMessage(Component.empty());
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
                    i + 1, fluidEmpty?"":Component.translatable(fluidStorage.getFluidInTank(i).getTranslationKey()).append(" "),
                    FluidUtils.getFluidAmountWithPrefix(fluidAmount), FluidUtils.getFluidAmountWithPrefix(fluidStorage.getTankCapacity(i)))
            ).withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide)
            return InteractionResult.SUCCESS;

        if(getEnergy(stack) < ENERGY_CONSUMPTION_PER_USE) {
            useItem(stack, useOnContext.getPlayer(), List.of(
                    Component.translatable("txt.energizedpower.fluid_analyzer.no_energy_left",
                            EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).withStyle(ChatFormatting.RED)
            ));

            return InteractionResult.SUCCESS;
        }

        BlockPos blockPos = useOnContext.getClickedPos();

        List<Component> components = new LinkedList<>();
        components.add(level.getBlockState(blockPos).getBlock().getName().withStyle(ChatFormatting.UNDERLINE, ChatFormatting.AQUA));

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(blockEntity == null) {
            components.add(Component.translatable("txt.energizedpower.fluid_analyzer.no_fluid_block").withStyle(ChatFormatting.RED));

            useItem(stack, useOnContext.getPlayer(), components);

            return InteractionResult.SUCCESS;
        }

        LazyOptional<IFluidHandler> fluidStorageLazyOptional = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER);
        addOutputTextForFluidStorage(components, fluidStorageLazyOptional.isPresent()?fluidStorageLazyOptional.orElse(null):null, false);

        components.add(Component.translatable("txt.energizedpower.fluid_analyzer.output_side_information").withStyle(ChatFormatting.GOLD));
        LazyOptional<IFluidHandler> fluidStorageLazyOptionalSided = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, useOnContext.getClickedFace());
        addOutputTextForFluidStorage(components, fluidStorageLazyOptionalSided.isPresent()?fluidStorageLazyOptional.orElse(null):null, true);

        useItem(stack, useOnContext.getPlayer(), components);

        return InteractionResult.SUCCESS;
    }
}
