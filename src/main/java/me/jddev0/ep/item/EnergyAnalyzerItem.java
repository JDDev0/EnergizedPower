package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnergyAnalyzerItem extends EnergizedPowerEnergyItem {
    public static final long ENERGY_CONSUMPTION_PER_USE = ModConfigs.COMMON_ENERGY_ANALYZER_ENERGY_CONSUMPTION_PER_USE.getValue();
    public static final long ENERGY_CAPACITY = ModConfigs.COMMON_ENERGY_ANALYZER_CAPACITY.getValue();

    public EnergyAnalyzerItem(Item.Properties props) {
        super(props, ENERGY_CAPACITY, ModConfigs.COMMON_ENERGY_ANALYZER_TRANSFER_RATE.getValue(), 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, tooltip, type);

        if(Minecraft.getInstance().hasShiftDown()) {
            tooltip.accept(Component.translatable("tooltip.energizedpower.energy_analyzer.txt.shift.1").withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("tooltip.energizedpower.energy_analyzer.txt.shift.2",
                    EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).withStyle(ChatFormatting.GRAY));
        }else {
            tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    private void useItem(ItemStack itemStack, Player player, List<Component> lines) {
        if(getEnergy(itemStack) >= ENERGY_CONSUMPTION_PER_USE)
            setEnergy(itemStack, getEnergy(itemStack) - ENERGY_CONSUMPTION_PER_USE);

        for(Component component:lines)
            player.displayClientMessage(component, false);
        player.displayClientMessage(Component.empty(), false);
    }

    private void addOutputTextForEnergyStorage(List<Component> components, @Nullable EnergyStorage energyStorage, boolean blockFaceSpecificInformation) {
        if(energyStorage == null) {
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.no_energy_block" + (blockFaceSpecificInformation?"_side":"")).
                    withStyle(ChatFormatting.RED));

            return;
        }

        components.add(Component.translatable("txt.energizedpower.energy_analyzer.energy_output",
                EnergyUtils.getEnergyWithPrefix(energyStorage.getAmount()),
                EnergyUtils.getEnergyWithPrefix(energyStorage.getCapacity())).withStyle(ChatFormatting.GOLD));

        if(energyStorage.supportsInsertion())
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.energy_can_receive" + (blockFaceSpecificInformation?"_side":"")).
                    withStyle(ChatFormatting.GOLD));

        if(energyStorage.supportsExtraction())
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.energy_can_extract" + (blockFaceSpecificInformation?"_side":"")).
                    withStyle(ChatFormatting.GOLD));
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        ItemStack stack = useOnContext.getItemInHand();

        if(getEnergy(stack) < ENERGY_CONSUMPTION_PER_USE) {
            useItem(stack, useOnContext.getPlayer(), List.of(
                    Component.translatable("txt.energizedpower.energy_analyzer.no_energy_left",
                            EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).withStyle(ChatFormatting.RED)
            ));

            return InteractionResult.SUCCESS;
        }

        BlockPos blockPos = useOnContext.getClickedPos();

        List<Component> components = new ArrayList<>();
        components.add(level.getBlockState(blockPos).getBlock().getName().withStyle(ChatFormatting.UNDERLINE, ChatFormatting.AQUA));

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(blockEntity == null) {
            components.add(Component.translatable("txt.energizedpower.energy_analyzer.no_block_entity").withStyle(ChatFormatting.RED));

            useItem(stack, useOnContext.getPlayer(), components);

            return InteractionResult.SUCCESS;
        }

        addOutputTextForEnergyStorage(components, EnergyStorage.SIDED.find(level, blockPos, null), false);

        components.add(Component.translatable("txt.energizedpower.energy_analyzer.output_side_information").withStyle(ChatFormatting.BLUE));
        addOutputTextForEnergyStorage(components, EnergyStorage.SIDED.find(level, blockPos, useOnContext.getClickedFace()), true);

        useItem(stack, useOnContext.getPlayer(), components);

        return InteractionResult.SUCCESS;
    }
}
