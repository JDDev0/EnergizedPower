package me.jddev0.ep.item;

import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.component.DimensionalPositionComponent;
import me.jddev0.ep.component.InventoryComponent;
import me.jddev0.ep.component.EPDataComponentTypes;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.screen.InventoryTeleporterMenu;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.function.Consumer;

public class InventoryTeleporterItem extends EnergizedPowerEnergyItem implements NamedScreenHandlerFactory {
    public static final long CAPACITY = ModConfigs.COMMON_INVENTORY_TELEPORTER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_INVENTORY_TELEPORTER_TRANSFER_RATE.getValue();

    public InventoryTeleporterItem(Item.Settings props) {
        super(props, CAPACITY, MAX_RECEIVE, 0);
    }

    @Override
    public ActionResult use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if(hand == Hand.OFF_HAND)
            return ActionResult.PASS;

        if(level.isClient() || !(player instanceof ServerPlayerEntity serverPlayer))
            return ActionResult.SUCCESS.withNewHandStack(itemStack);

        if(player.isSneaking())
            player.openHandledScreen(this);
        else
            teleportPlayer(itemStack, serverPlayer);

        return ActionResult.SUCCESS.withNewHandStack(itemStack);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.inventory_teleporter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new InventoryTeleporterMenu(id, inventory, getInventory(player.getMainHandStack()));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, tooltip, type);

        SimpleInventory inventory = getInventory(stack);
        ItemStack teleporterMatrixItemStack = inventory.getStack(0);

        DimensionalPositionComponent dimPos = teleporterMatrixItemStack.get(EPDataComponentTypes.DIMENSIONAL_POSITION);
        boolean linked = TeleporterMatrixItem.isLinked(teleporterMatrixItemStack) && dimPos != null;

        tooltip.accept(Text.translatable("tooltip.energizedpower.teleporter_matrix.status").formatted(Formatting.GRAY).
                append(Text.translatable("tooltip.energizedpower.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).formatted(linked?Formatting.GREEN:Formatting.RED)));

        if(linked) {
            tooltip.accept(Text.empty());

            tooltip.accept(Text.translatable("tooltip.energizedpower.teleporter_matrix.location").
                    append(Text.literal(dimPos.x() + " " + dimPos.y() + " " + dimPos.z())));
            tooltip.accept(Text.translatable("tooltip.energizedpower.teleporter_matrix.dimension").
                    append(Text.literal(dimPos.dimensionId().toString())));
        }

        tooltip.accept(Text.empty());

        if(MinecraftClient.getInstance().isShiftPressed()) {
            tooltip.accept(Text.translatable("tooltip.energizedpower.inventory_teleporter.txt.shift.1").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
            tooltip.accept(Text.translatable("tooltip.energizedpower.inventory_teleporter.txt.shift.2").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
        }else {
            tooltip.accept(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    public static SimpleInventory getInventory(ItemStack itemStack) {
        InventoryComponent inventory = itemStack.get(EPDataComponentTypes.INVENTORY);

        if(inventory != null) {
            DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
            for(int i = 0;i < items.size();i++) {
                if(inventory.size() <= i)
                    break;

                items.set(i, inventory.get(i));
            }
            return new SimpleInventory(items.toArray(new ItemStack[0])) {
                @Override
                public void markDirty() {
                    super.markDirty();

                    itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(heldStacks));
                }

                @Override
                public boolean isValid(int slot, @NotNull ItemStack stack) {
                    if(slot >= 0 && slot < size()) {
                        return stack.isOf(EPItems.TELEPORTER_MATRIX);
                    }

                    return super.isValid(slot, stack);
                }

                @Override
                public boolean canPlayerUse(PlayerEntity player) {
                    return super.canPlayerUse(player) && player.getMainHandStack() == itemStack;
                }

                @Override
                public int getMaxCountPerStack() {
                    return 1;
                }
            };
        }

        return new SimpleInventory(1) {
            @Override
            public void markDirty() {
                super.markDirty();

                itemStack.set(EPDataComponentTypes.INVENTORY, new InventoryComponent(heldStacks));
            }

            @Override
            public boolean isValid(int slot, @NotNull ItemStack stack) {
                if(slot >= 0 && slot < size()) {
                    return stack.isOf(EPItems.TELEPORTER_MATRIX);
                }

                return super.isValid(slot, stack);
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return super.canPlayerUse(player) && player.getMainHandStack() == itemStack;
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }
        };
    }

    public static void teleportPlayer(ItemStack itemStack, ServerPlayerEntity player) {
        World level = player.getEntityWorld();

        EnergyStorage energyStorage = EnergyStorage.ITEM.find(itemStack, ContainerItemContext.ofPlayerHand(player,
                Hand.MAIN_HAND));
        if(energyStorage == null)
            return;

        SimpleInventory inventory = getInventory(itemStack);
        ItemStack teleporterMatrixItemStack = inventory.getStack(0);

        TeleporterBlockEntity.teleportPlayer(player, energyStorage, () -> setStoredEnergyUnchecked(itemStack, 0),
                teleporterMatrixItemStack, level, null);
    }
}
