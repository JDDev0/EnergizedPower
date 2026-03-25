package me.jddev0.ep.item;

import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.component.DimensionalPositionComponent;
import me.jddev0.ep.component.InventoryComponent;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.screen.InventoryTeleporterMenu;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.Consumer;

public class InventoryTeleporterItem extends EnergizedPowerEnergyItem implements MenuProvider {
    public static final long CAPACITY = ModConfigs.COMMON_INVENTORY_TELEPORTER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_INVENTORY_TELEPORTER_TRANSFER_RATE.getValue();

    public InventoryTeleporterItem(Item.Properties props) {
        super(props, CAPACITY, MAX_RECEIVE, 0);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if(hand == InteractionHand.OFF_HAND)
            return InteractionResult.PASS;

        if(level.isClientSide() || !(player instanceof ServerPlayer serverPlayer))
            return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);

        if(player.isShiftKeyDown())
            player.openMenu(this);
        else
            teleportPlayer(itemStack, serverPlayer);

        return InteractionResult.SUCCESS.heldItemTransformedTo(itemStack);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.inventory_teleporter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new InventoryTeleporterMenu(id, inventory, getInventory(player.getMainHandItem()));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> tooltip, TooltipFlag type) {
        super.appendHoverText(stack, context, displayComponent, tooltip, type);

        SimpleContainer inventory = getInventory(stack);
        ItemStack teleporterMatrixItemStack = inventory.getItem(0);

        DimensionalPositionComponent dimPos = teleporterMatrixItemStack.get(EPDataComponentTypes.DIMENSIONAL_POSITION);
        boolean linked = TeleporterMatrixItem.isLinked(teleporterMatrixItemStack) && dimPos != null;

        tooltip.accept(Component.translatable("tooltip.energizedpower.teleporter_matrix.status").withStyle(ChatFormatting.GRAY).
                append(Component.translatable("tooltip.energizedpower.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).withStyle(linked?ChatFormatting.GREEN:ChatFormatting.RED)));

        if(linked) {
            tooltip.accept(Component.empty());

            tooltip.accept(Component.translatable("tooltip.energizedpower.teleporter_matrix.location").
                    append(Component.literal(dimPos.x() + " " + dimPos.y() + " " + dimPos.z())));
            tooltip.accept(Component.translatable("tooltip.energizedpower.teleporter_matrix.dimension").
                    append(Component.literal(dimPos.dimensionId().toString())));
        }

        tooltip.accept(Component.empty());

        if(Minecraft.getInstance().hasShiftDown()) {
            tooltip.accept(Component.translatable("tooltip.energizedpower.inventory_teleporter.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            tooltip.accept(Component.translatable("tooltip.energizedpower.inventory_teleporter.txt.shift.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            tooltip.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    public static SimpleContainer getInventory(ItemStack itemStack) {
        InventoryComponent inventory = itemStack.get(EPDataComponentTypes.INVENTORY);

        if(inventory != null) {
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            for(int i = 0;i < items.size();i++) {
                if(inventory.size() <= i)
                    break;

                items.set(i, inventory.get(i));
            }
            return new SimpleContainer(items.toArray(new ItemStack[0])) {
                @Override
                public void setChanged() {
                    super.setChanged();

                    itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(items));
                }

                @Override
                public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                    if(slot >= 0 && slot < getContainerSize()) {
                        return stack.is(EPItems.TELEPORTER_MATRIX);
                    }

                    return super.canPlaceItem(slot, stack);
                }

                @Override
                public boolean stillValid(Player player) {
                    return super.stillValid(player) && player.getMainHandItem() == itemStack;
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            };
        }

        return new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();

                itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(items));
            }

            @Override
            public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < getContainerSize()) {
                    return stack.is(EPItems.TELEPORTER_MATRIX);
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public boolean stillValid(Player player) {
                return super.stillValid(player) && player.getMainHandItem() == itemStack;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    public static void teleportPlayer(ItemStack itemStack, ServerPlayer player) {
        Level level = player.level();

        EnergyStorage energyStorage = EnergyStorage.ITEM.find(itemStack, ContainerItemContext.ofPlayerHand(player,
                InteractionHand.MAIN_HAND));
        if(energyStorage == null)
            return;

        SimpleContainer inventory = getInventory(itemStack);
        ItemStack teleporterMatrixItemStack = inventory.getItem(0);

        TeleporterBlockEntity.teleportPlayer(player, energyStorage, () -> setStoredEnergyUnchecked(itemStack, 0),
                teleporterMatrixItemStack, level, null);
    }
}
