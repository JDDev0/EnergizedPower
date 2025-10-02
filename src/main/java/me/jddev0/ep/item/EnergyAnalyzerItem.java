package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
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
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EnergyAnalyzerItem extends EnergizedPowerEnergyItem {
    public static final long ENERGY_CONSUMPTION_PER_USE = ModConfigs.COMMON_ENERGY_ANALYZER_ENERGY_CONSUMPTION_PER_USE.getValue();
    public static final long ENERGY_CAPACITY = ModConfigs.COMMON_ENERGY_ANALYZER_CAPACITY.getValue();

    public EnergyAnalyzerItem(Item.Settings props) {
        super(props, ENERGY_CAPACITY, ModConfigs.COMMON_ENERGY_ANALYZER_TRANSFER_RATE.getValue(), 0);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, tooltip, type);

        if(MinecraftClient.getInstance().isShiftPressed()) {
            tooltip.accept(Text.translatable("tooltip.energizedpower.energy_analyzer.txt.shift.1").formatted(Formatting.GRAY));
            tooltip.accept(Text.translatable("tooltip.energizedpower.energy_analyzer.txt.shift.2",
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

    private void addOutputTextForEnergyStorage(List<Text> components, @Nullable EnergyStorage energyStorage, boolean blockFaceSpecificInformation) {
        if(energyStorage == null) {
            components.add(Text.translatable("txt.energizedpower.energy_analyzer.no_energy_block" + (blockFaceSpecificInformation?"_side":"")).
                    formatted(Formatting.RED));

            return;
        }

        components.add(Text.translatable("txt.energizedpower.energy_analyzer.energy_output",
                EnergyUtils.getEnergyWithPrefix(energyStorage.getAmount()),
                EnergyUtils.getEnergyWithPrefix(energyStorage.getCapacity())).formatted(Formatting.GOLD));

        if(energyStorage.supportsInsertion())
            components.add(Text.translatable("txt.energizedpower.energy_analyzer.energy_can_receive" + (blockFaceSpecificInformation?"_side":"")).
                    formatted(Formatting.GOLD));

        if(energyStorage.supportsExtraction())
            components.add(Text.translatable("txt.energizedpower.energy_analyzer.energy_can_extract" + (blockFaceSpecificInformation?"_side":"")).
                    formatted(Formatting.GOLD));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext useOnContext) {
        World level = useOnContext.getWorld();
        if(level.isClient())
            return ActionResult.SUCCESS;

        ItemStack stack = useOnContext.getStack();

        if(getEnergy(stack) < ENERGY_CONSUMPTION_PER_USE) {
            useItem(stack, useOnContext.getPlayer(), List.of(
                    Text.translatable("txt.energizedpower.energy_analyzer.no_energy_left",
                            EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).formatted(Formatting.RED)
            ));

            return ActionResult.SUCCESS;
        }

        BlockPos blockPos = useOnContext.getBlockPos();

        List<Text> components = new ArrayList<>();
        components.add(level.getBlockState(blockPos).getBlock().getName().formatted(Formatting.UNDERLINE, Formatting.AQUA));

        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(blockEntity == null) {
            components.add(Text.translatable("txt.energizedpower.energy_analyzer.no_block_entity").formatted(Formatting.RED));

            useItem(stack, useOnContext.getPlayer(), components);

            return ActionResult.SUCCESS;
        }

        addOutputTextForEnergyStorage(components, EnergyStorage.SIDED.find(level, blockPos, null), false);

        components.add(Text.translatable("txt.energizedpower.energy_analyzer.output_side_information").formatted(Formatting.BLUE));
        addOutputTextForEnergyStorage(components, EnergyStorage.SIDED.find(level, blockPos, useOnContext.getSide()), true);

        useItem(stack, useOnContext.getPlayer(), components);

        return ActionResult.SUCCESS;
    }
}
