package me.jddev0.ep.item;

import me.jddev0.ep.block.entity.MachineConfiguratorConfigurable;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.component.MachineConfigurationComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public class MachineConfiguratorItem extends Item implements IEPItemExtension {
    public MachineConfiguratorItem(Properties props) {
        super(props);
    }

    public static boolean hasConfiguration(ItemStack itemStack) {
        return itemStack.has(EPDataComponentTypes.SOURCE_BLOCK) &&
                itemStack.has(EPDataComponentTypes.SOURCE_BLOCK_ENTITY) &&
                itemStack.has(EPDataComponentTypes.MACHINE_CONFIGURATION);
    }

    @Override
    public InteractionResult onItemUseFirst(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        if(level.isClientSide() || !(level instanceof ServerLevel))
            return InteractionResult.SUCCESS;

        Player player = useOnContext.getPlayer();

        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if(!(blockEntity instanceof MachineConfiguratorConfigurable machineConfiguratorConfigurable)) {
            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.machine_configurator.not_configurable").withStyle(ChatFormatting.RED)
                ));
            }

            return InteractionResult.SUCCESS;
        }

        ItemStack itemStack = useOnContext.getItemInHand();

        boolean storeApply = player != null && player.isShiftKeyDown();
        if(storeApply) {
            itemStack.set(EPDataComponentTypes.SOURCE_BLOCK, blockState.getBlock());
            itemStack.set(EPDataComponentTypes.SOURCE_BLOCK_ENTITY, blockEntity.getType());
            itemStack.set(EPDataComponentTypes.MACHINE_CONFIGURATION, machineConfiguratorConfigurable.onStoreMachineConfiguration());

            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.machine_configurator.configuration_stored").withStyle(ChatFormatting.GREEN)
                ));
            }

            return InteractionResult.SUCCESS;
        }else {
            MachineConfigurationComponent machineConfiguration = itemStack.get(EPDataComponentTypes.MACHINE_CONFIGURATION);
            BlockEntityType<?> sourceBlockEntity = itemStack.get(EPDataComponentTypes.SOURCE_BLOCK_ENTITY);
            if(!hasConfiguration(itemStack) || machineConfiguration == null || sourceBlockEntity == null) {
                if(player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                            Component.translatable("tooltip.energizedpower.machine_configurator.configuration_missing").withStyle(ChatFormatting.RED)
                    ));
                }

                return InteractionResult.SUCCESS;
            }

            if(sourceBlockEntity != blockEntity.getType()) {
                if(player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                            Component.translatable("tooltip.energizedpower.machine_configurator.configuration_incompatible").withStyle(ChatFormatting.RED)
                    ));
                }

                return InteractionResult.SUCCESS;
            }

            boolean success = machineConfiguratorConfigurable.onApplyMachineConfiguration(machineConfiguration);
            if(success) {
                if(player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                            Component.translatable("tooltip.energizedpower.machine_configurator.configuration_applied").withStyle(ChatFormatting.GREEN)
                    ));
                }
            }else {
                if(player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                            Component.translatable("tooltip.energizedpower.machine_configurator.configuration_invalid").withStyle(ChatFormatting.RED)
                    ));
                }
            }

            return InteractionResult.SUCCESS;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(player.isShiftKeyDown()) {
            if(level.isClientSide())
                return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());

            itemStack.remove(EPDataComponentTypes.SOURCE_BLOCK);
            itemStack.remove(EPDataComponentTypes.SOURCE_BLOCK_ENTITY);
            itemStack.remove(EPDataComponentTypes.MACHINE_CONFIGURATION);

            if(player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(
                        Component.translatable("tooltip.energizedpower.machine_configurator.configuration_cleared").withStyle(ChatFormatting.GREEN)
                ));
            }

            return InteractionResultHolder.success(itemStack);
        }

        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        MachineConfigurationComponent machineConfiguration = itemStack.get(EPDataComponentTypes.MACHINE_CONFIGURATION);
        Block sourceBlock = itemStack.get(EPDataComponentTypes.SOURCE_BLOCK);
        boolean configured = hasConfiguration(itemStack) && machineConfiguration != null && sourceBlock != null;

        components.add(Component.translatable("tooltip.energizedpower.machine_configurator.status").withStyle(ChatFormatting.GRAY).
                append(Component.translatable("tooltip.energizedpower.machine_configurator.status." +
                        (configured?"configured":"unconfigured")).withStyle(configured?ChatFormatting.GREEN:ChatFormatting.RED)));

        if(configured) {
            components.add(Component.empty());

            components.add(Component.translatable("tooltip.energizedpower.machine_configurator.source_block").withStyle(ChatFormatting.GRAY).
                    append(Component.empty().withStyle(ChatFormatting.WHITE).append(sourceBlock.getName())));
        }

        components.add(Component.empty());

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.machine_configurator.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            components.add(Component.translatable("tooltip.energizedpower.machine_configurator.txt.shift.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            components.add(Component.translatable("tooltip.energizedpower.machine_configurator.txt.shift.3").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public Component getName(ItemStack itemStack) {
        return Component.translatable(getDescriptionId() + "." + (hasConfiguration(itemStack)?"configured":"unconfigured"));
    }
}
