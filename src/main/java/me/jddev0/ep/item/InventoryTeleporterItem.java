package me.jddev0.ep.item;

import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.energy.ReceiveOnlyEnergyStorage;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.screen.InventoryTeleporterMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InventoryTeleporterItem extends EnergizedPowerEnergyItem implements MenuProvider {
    public static final int CAPACITY = ModConfigs.COMMON_INVENTORY_TELEPORTER_CAPACITY.getValue();
    public static final int MAX_RECEIVE = ModConfigs.COMMON_INVENTORY_TELEPORTER_TRANSFER_RATE.getValue();

    public InventoryTeleporterItem(Properties props) {
        super(props, () -> new ReceiveOnlyEnergyStorage(0, CAPACITY, MAX_RECEIVE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        if(interactionHand == InteractionHand.OFF_HAND)
            return InteractionResultHolder.pass(itemStack);

        if(level.isClientSide || !(player instanceof ServerPlayer serverPlayer))
            return InteractionResultHolder.success(itemStack);

        if(player.isShiftKeyDown())
            player.openMenu(this);
        else
            teleportPlayer(itemStack, serverPlayer);

        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.inventory_teleporter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new InventoryTeleporterMenu(id, inventory, getInventory(inventory.getSelected()));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, components, tooltipFlag);

        SimpleContainer inventory = getInventory(itemStack);
        ItemStack teleporterMatrixItemStack = inventory.getItem(0);

        boolean linked = TeleporterMatrixItem.isLinked(teleporterMatrixItemStack);
        CompoundTag nbt = teleporterMatrixItemStack.getTag();

        components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.status").withStyle(ChatFormatting.GRAY).
                append(Component.translatable("tooltip.energizedpower.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).withStyle(linked?ChatFormatting.GREEN:ChatFormatting.RED)));

        if(linked) {
            components.add(Component.empty());

            components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.location").
                    append(Component.literal(nbt.getInt("x") + " " + nbt.getInt("y") +
                            " " + nbt.getInt("z"))));
            components.add(Component.translatable("tooltip.energizedpower.teleporter_matrix.dimension").
                    append(Component.literal(nbt.getString("dim"))));
        }

        components.add(Component.empty());

        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.energizedpower.inventory_teleporter.txt.shift.1").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            components.add(Component.translatable("tooltip.energizedpower.inventory_teleporter.txt.shift.2").
                    withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }else {
            components.add(Component.translatable("tooltip.energizedpower.shift_details.txt").withStyle(ChatFormatting.YELLOW));
        }
    }

    public static SimpleContainer getInventory(ItemStack itemStack) {
        CompoundTag nbt = itemStack.getOrCreateTag();

        if(nbt.contains("inventory")) {
            NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(nbt.getCompound("inventory"), items);

            return new SimpleContainer(items.toArray(new ItemStack[0])) {
                @Override
                public void setChanged() {
                    super.setChanged();

                    NonNullList<ItemStack> items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
                    for(int i = 0;i < getContainerSize();i++)
                        items.set(i, getItem(i));

                    itemStack.getOrCreateTag().put("inventory", ContainerHelper.saveAllItems(new CompoundTag(), items));
                }

                @Override
                public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                    if(slot >= 0 && slot < getContainerSize()) {
                        return stack.is(ModItems.TELEPORTER_MATRIX.get());
                    }

                    return super.canPlaceItem(slot, stack);
                }

                @Override
                public boolean stillValid(Player player) {
                    return super.stillValid(player) && player.getInventory().getSelected() == itemStack;
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

                itemStack.getOrCreateTag().put("inventory", ContainerHelper.saveAllItems(new CompoundTag(), items));
            }

            @Override
            public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < getContainerSize()) {
                    return stack.is(ModItems.TELEPORTER_MATRIX.get());
                }

                return super.canPlaceItem(slot, stack);
            }

            @Override
            public boolean stillValid(Player player) {
                return super.stillValid(player) && player.getInventory().getSelected() == itemStack;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    public static void teleportPlayer(ItemStack itemStack, ServerPlayer player) {
        Level level = player.level;

        LazyOptional<IEnergyStorage> energyStorageLazyOptional = itemStack.getCapability(ForgeCapabilities.ENERGY);
        if(!energyStorageLazyOptional.isPresent())
            return;

        IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);

        SimpleContainer inventory = getInventory(itemStack);
        ItemStack teleporterMatrixItemStack = inventory.getItem(0);

        TeleporterBlockEntity.teleportPlayer(player, energyStorage, () -> setEnergy(itemStack, 0),
                teleporterMatrixItemStack, level, null);
    }
}
