package me.jddev0.ep.item;

import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class FluidAnalyzerItem extends EnergizedPowerEnergyItem {
    public static final long ENERGY_CONSUMPTION_PER_USE = ModConfigs.COMMON_FLUID_ANALYZER_ENERGY_CONSUMPTION_PER_USE.getValue();
    public static final long ENERGY_CAPACITY = ModConfigs.COMMON_FLUID_ANALYZER_CAPACITY.getValue();

    public FluidAnalyzerItem(Item.Properties props) {
        super(props, ENERGY_CAPACITY, ModConfigs.COMMON_FLUID_ANALYZER_TRANSFER_RATE.getValue(), 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, tooltip, type);

        if(Minecraft.getInstance().hasShiftDown()) {
            tooltip.accept(Component.translatable("tooltip.energizedpower.fluid_analyzer.txt.shift.1").withStyle(ChatFormatting.GRAY));
            tooltip.accept(Component.translatable("tooltip.energizedpower.fluid_analyzer.txt.shift.2",
                    EnergyUtils.getEnergyWithPrefix(ENERGY_CONSUMPTION_PER_USE)).withStyle(ChatFormatting.GRAY));
        }else {
            tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    private void useItem(ItemStack itemStack, Player player, List<Component> lines) {
        if(getEnergy(itemStack) >= ENERGY_CONSUMPTION_PER_USE)
            setEnergy(itemStack, getEnergy(itemStack) - ENERGY_CONSUMPTION_PER_USE);

        for(Component component:lines)
            player.sendSystemMessage(component);
        player.sendSystemMessage(Component.empty());
    }

    private void addOutputTextForFluidStorage(List<Component> components, @Nullable Storage<FluidVariant> fluidStorage, boolean blockFaceSpecificInformation) {
        if(fluidStorage == null) {
            components.add(Component.translatable("txt.energizedpower.fluid_analyzer.no_fluid_block" + (blockFaceSpecificInformation?"_side":"")).
                    withStyle(ChatFormatting.RED));

            return;
        }

        int tankCount = 0;
        Iterator<StorageView<FluidVariant>> fluidStorageIterator = fluidStorage.iterator();
        while(fluidStorageIterator.hasNext() && tankCount < 100) {
            //Limit max iterations to 100
            tankCount++;

            fluidStorageIterator.next();
        }

        components.add(Component.translatable("txt.energizedpower.fluid_analyzer.fluid_output.tank_count" + (blockFaceSpecificInformation?"_side":""),
                tankCount).withStyle(ChatFormatting.BLUE));

        int tankNum = 0;
        for(StorageView<FluidVariant> fluidView:fluidStorage) {
            if(++tankNum > 100) //Limit max iterations to 100
                break;

            boolean fluidEmpty = fluidView.isResourceBlank();

            long fluidAmount = fluidEmpty?0:fluidView.getAmount();

            components.add(Component.literal("• ").append(
                    Component.translatable("txt.energizedpower.fluid_analyzer.fluid_output.tank_fluid_content",
                            tankNum, fluidEmpty?"":Component.translatable(fluidView.getResource().getFluid().defaultFluidState().createLegacyBlock().getBlock().getDescriptionId()).append(" "),
                    FluidUtils.getFluidAmountWithPrefix(FluidUtils.convertDropletsToMilliBuckets(fluidAmount)),
                            FluidUtils.getFluidAmountWithPrefix(FluidUtils.convertDropletsToMilliBuckets(fluidView.getCapacity())))
            ).withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide())
            return InteractionResult.SUCCESS;

        ItemStack stack = useOnContext.getItemInHand();

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
        if(blockEntity == null) {
            components.add(Component.translatable("txt.energizedpower.fluid_analyzer.no_fluid_block").withStyle(ChatFormatting.RED));

            useItem(stack, useOnContext.getPlayer(), components);

            return InteractionResult.SUCCESS;
        }

        addOutputTextForFluidStorage(components, FluidStorage.SIDED.find(level, blockPos, null), false);

        components.add(Component.translatable("txt.energizedpower.fluid_analyzer.output_side_information").withStyle(ChatFormatting.GOLD));
        addOutputTextForFluidStorage(components, FluidStorage.SIDED.find(level, blockPos, useOnContext.getClickedFace()), true);

        useItem(stack, useOnContext.getPlayer(), components);

        return InteractionResult.SUCCESS;
    }
}
