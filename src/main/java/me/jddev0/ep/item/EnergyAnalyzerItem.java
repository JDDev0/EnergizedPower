package me.jddev0.ep.item;

import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.LinkedList;
import java.util.List;

public class EnergyAnalyzerItem extends EnergizedPowerEnergyItem {
    public static final long ENERGY_CONSUMPTION_PER_USE = 8;
    public static final long ENERGY_CAPACITY = 2048;

    public EnergyAnalyzerItem(FabricItemSettings props) {
        super(props, ENERGY_CAPACITY, 32, 0);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, @Nullable World level, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(itemStack, level, tooltip, context);

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.energy_analyzer.txt.shift.1").formatted(Formatting.GRAY));
            tooltip.add(Text.translatable("tooltip.energizedpower.energy_analyzer.txt.shift.2",
                    EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).formatted(Formatting.GRAY));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    private void useItem(ItemStack itemStack, PlayerEntity player, List<Text> lines) {
        if(getEnergy(itemStack) >= ENERGY_CONSUMPTION_PER_USE)
            setEnergy(itemStack, getEnergy(itemStack) - ENERGY_CONSUMPTION_PER_USE);

        for(Text component:lines)
            player.sendMessage(component);
        player.sendMessage(Text.empty());
    }

    private void addOutputTextForEnergyStorage(List<Text> components, @Nullable EnergyStorage energyStorage, boolean blockFaceSpecificInformation) {
        if(energyStorage == null) {
            components.add(Text.translatable("txt.energizedpower.energy_analyzer.no_energy_block").formatted(Formatting.RED));

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

        List<Text> components = new LinkedList<>();
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
