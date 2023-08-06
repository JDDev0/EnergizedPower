package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
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
    public static final int ENERGY_CONSUMPTION_PER_USE = ModConfigs.COMMON_ENERGY_ANALYZER_ENERGY_CONSUMPTION_PER_USE.getValue();
    public static final int ENERGY_CAPACITY = ModConfigs.COMMON_ENERGY_ANALYZER_CAPACITY.getValue();

    public EnergyAnalyzerItem(Properties props) {
        super(props, () -> new ReceiveOnlyEnergyStorage(0, ENERGY_CAPACITY, ModConfigs.COMMON_ENERGY_ANALYZER_TRANSFER_RATE.getValue()));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.energy_analyzer.txt.shift.1").withStyle(ChatFormatting.GRAY));
            components.add(Component.translatable("tooltip.energizedpower.energy_analyzer.txt.shift.2",
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

    private void addOutputTextForEnergyStorage(List<Component> components, @Nullable IEnergyStorage energyStorage, boolean blockFaceSpecificInformation) {
        if(energyStorage == null) {
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.no_energy_block" + (blockFaceSpecificInformation?"_side":"")).
                    withStyle(ChatFormatting.RED));

            return;
        }

        components.add(Component.translatable("txt.energizedpower.energy_analyzer.energy_output",
                EnergyUtils.getEnergyWithPrefix(energyStorage.getEnergyStored()),
                EnergyUtils.getEnergyWithPrefix(energyStorage.getMaxEnergyStored())).withStyle(ChatFormatting.GOLD));

        if(energyStorage.canReceive())
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.energy_can_receive" + (blockFaceSpecificInformation?"_side":"")).
                    withStyle(ChatFormatting.GOLD));

        if(energyStorage.canExtract())
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.energy_can_extract" + (blockFaceSpecificInformation?"_side":"")).
                    withStyle(ChatFormatting.GOLD));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide)
            return InteractionResult.SUCCESS;

        if(getEnergy(stack) < ENERGY_CONSUMPTION_PER_USE) {
            useItem(stack, useOnContext.getPlayer(), List.of(
                    Component.translatable("txt.energizedpower.energy_analyzer.no_energy_left",
                            EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).withStyle(ChatFormatting.RED)
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
        addOutputTextForEnergyStorage(components, energyStorageLazyOptional.isPresent()?energyStorageLazyOptional.orElse(null):null, false);

        components.add(Component.translatable("txt.energizedpower.energy_analyzer.output_side_information").withStyle(ChatFormatting.BLUE));
        LazyOptional<IEnergyStorage> energyStorageLazyOptionalSided = blockEntity.getCapability(ForgeCapabilities.ENERGY, useOnContext.getClickedFace());
        addOutputTextForEnergyStorage(components, energyStorageLazyOptionalSided.isPresent()?energyStorageLazyOptionalSided.orElse(null):null, true);

        useItem(stack, useOnContext.getPlayer(), components);

        return InteractionResult.SUCCESS;
    }
}
