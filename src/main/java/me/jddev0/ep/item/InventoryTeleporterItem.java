package me.jddev0.ep.item;

import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.component.DimensionalPositionComponent;
import me.jddev0.ep.component.InventoryComponent;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.screen.InventoryTeleporterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class InventoryTeleporterItem extends EnergizedPowerEnergyItem implements MenuProvider {
    public static final int CAPACITY = ModConfigs.COMMON_INVENTORY_TELEPORTER_CAPACITY.getValue();
    public static final int MAX_RECEIVE = ModConfigs.COMMON_INVENTORY_TELEPORTER_TRANSFER_RATE.getValue();

    public InventoryTeleporterItem(Properties props) {
        super(props, () -> new ReceiveOnlyEnergyStorage(0, CAPACITY, MAX_RECEIVE));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(interactionHand == InteractionHand.OFF_HAND)
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
        return new InventoryTeleporterMenu(id, inventory, getInventory(inventory.getSelectedItem()));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, components, tooltipFlag);

        SimpleContainer inventory = getInventory(itemStack);
        ItemStack teleporterMatrixItemStack = inventory.getItem(0);

        DimensionalPositionComponent dimPos = teleporterMatrixItemStack.get(EPDataComponentTypes.DIMENSIONAL_POSITION);
        boolean linked = TeleporterMatrixItem.isLinked(teleporterMatrixItemStack) && dimPos != null;

        components.accept(Component.translatable("tooltip.energizedpower.teleporter_matrix.status").withStyle(ChatFormatting.GRAY).
                append(Component.translatable("tooltip.energizedpower.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).withStyle(linked?ChatFormatting.GREEN:ChatFormatting.RED)));

        if(linked) {
            components.accept(Component.empty());

            components.accept(Component.translatable("tooltip.energizedpower.teleporter_matrix.location").
                    append(Component.literal(dimPos.x() + " " + dimPos.y() + " " + dimPos.z())));
            components.accept(Component.translatable("tooltip.energizedpower.teleporter_matrix.dimension").
                    append(Component.literal(dimPos.dimensionId().toString())));
        }

        components.accept(Component.empty());

        if(Minecraft.getInstance().hasShiftDown()) {
            components.accept(Component.translatable("tooltip.energizedpower.inventory_teleporter.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            components.accept(Component.translatable("tooltip.energizedpower.inventory_teleporter.txt.shift.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            components.accept(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
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

                    NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
                    for(int i = 0;i < getContainerSize();i++)
                        items.set(i, getItem(i));

                    itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(items));
                }

                @Override
                public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                    if(slot >= 0 && slot < getContainerSize()) {
                        return stack.is(EPItems.TELEPORTER_MATRIX.get());
                    }

                    return super.canPlaceItem(slot, stack);
                }

                @Override
                public boolean stillValid(Player player) {
                    return super.stillValid(player) && player.getInventory().getSelectedItem() == itemStack;
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

                NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
                for(int i = 0;i < getContainerSize();i++)
                    items.set(i, getItem(i));

                itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(items));
            }

            @Override
            public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < getContainerSize()) {
                    return stack.is(EPItems.TELEPORTER_MATRIX.get());
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public boolean stillValid(Player player) {
                return super.stillValid(player) && player.getInventory().getSelectedItem() == itemStack;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    public static void teleportPlayer(ItemStack itemStack, ServerPlayer player) {
        Level level = player.level();

        IEnergyStorage energyStorage = itemStack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyStorage == null)
            return;

        SimpleContainer inventory = getInventory(itemStack);
        ItemStack teleporterMatrixItemStack = inventory.getItem(0);

        TeleporterBlockEntity.teleportPlayer(player, energyStorage, () -> setEnergy(itemStack, 0),
                teleporterMatrixItemStack, level, null);
    }
}
