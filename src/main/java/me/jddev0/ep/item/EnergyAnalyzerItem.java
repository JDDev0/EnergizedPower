package me.jddev0.ep.item;

import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
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
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class EnergyAnalyzerItem extends EnergizedPowerEnergyItem {
    public static final int ENERGY_CONSUMPTION_PER_USE = 8;
    public static final int ENERGY_CAPACITY = 2048;

    public EnergyAnalyzerItem(Properties props) {
        super(props, () -> new ReceiveOnlyEnergyStorage(0, ENERGY_CAPACITY, 32));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.energy_analyzer.txt.shift.1").withStyle(ChatFormatting.GRAY));
            components.add(Component.translatable("tooltip.energizedpower.energy_analyzer.txt.shift.2", ENERGY_CONSUMPTION_PER_USE).
                    withStyle(ChatFormatting.GRAY));
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

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide)
            return InteractionResult.SUCCESS;

        if(getEnergy(stack) < ENERGY_CONSUMPTION_PER_USE) {
            useItem(stack, useOnContext.getPlayer(), List.of(
                    Component.translatable("txt.energizedpower.energy_analyzer.no_energy_left", ENERGY_CONSUMPTION_PER_USE).
                            withStyle(ChatFormatting.RED)
            ));

            return InteractionResult.SUCCESS;
        }

        BlockPos blockPos = useOnContext.getClickedPos();

        List<Component> components = new LinkedList<>();
        components.add(level.getBlockState(blockPos).getBlock().getName().withStyle(ChatFormatting.UNDERLINE, ChatFormatting.AQUA));

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(blockEntity == null) {
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.no_block_entity").withStyle(ChatFormatting.RED));

            useItem(stack, useOnContext.getPlayer(), components);

            return InteractionResult.SUCCESS;
        }

        LazyOptional<IEnergyStorage> energyStorageLazyOptional = blockEntity.getCapability(ForgeCapabilities.ENERGY);
        if(!energyStorageLazyOptional.isPresent()) {
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.no_energy_block").withStyle(ChatFormatting.RED));

            useItem(stack, useOnContext.getPlayer(), components);

            return InteractionResult.SUCCESS;
        }

        IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);

        components.add(Component.translatable("txt.energizedpower.energy_analyzer.energy_output", energyStorage.getEnergyStored(),
                energyStorage.getMaxEnergyStored()).withStyle(ChatFormatting.GOLD));

        if(energyStorage.canReceive())
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.energy_can_receive", energyStorage.getEnergyStored(),
                    energyStorage.getMaxEnergyStored()).withStyle(ChatFormatting.GOLD));

        if(energyStorage.canExtract())
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.energy_can_extract", energyStorage.getEnergyStored(),
                    energyStorage.getMaxEnergyStored()).withStyle(ChatFormatting.GOLD));

        useItem(stack, useOnContext.getPlayer(), components);

        return InteractionResult.SUCCESS;
    }
}
