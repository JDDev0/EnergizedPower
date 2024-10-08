package me.jddev0.ep.item;

import me.jddev0.ep.block.entity.TeleporterBlockEntity;
import me.jddev0.ep.config.ModConfigs;
import me.jddev0.ep.item.energy.EnergizedPowerEnergyItem;
import me.jddev0.ep.screen.InventoryTeleporterMenu;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyItem;

import java.util.List;

public class InventoryTeleporterItem extends EnergizedPowerEnergyItem implements NamedScreenHandlerFactory {
    public static final long CAPACITY = ModConfigs.COMMON_INVENTORY_TELEPORTER_CAPACITY.getValue();
    public static final long MAX_RECEIVE = ModConfigs.COMMON_INVENTORY_TELEPORTER_TRANSFER_RATE.getValue();

    public InventoryTeleporterItem(FabricItemSettings props) {
        super(props, CAPACITY, MAX_RECEIVE, 0);
    }

    @Override
    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if(hand == Hand.OFF_HAND)
            return TypedActionResult.pass(itemStack);

        if(level.isClient() || !(player instanceof ServerPlayerEntity serverPlayer))
            return TypedActionResult.success(itemStack);

        if(player.isSneaking())
            player.openHandledScreen(this);
        else
            teleportPlayer(itemStack, serverPlayer);

        return TypedActionResult.success(itemStack);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.inventory_teleporter");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new InventoryTeleporterMenu(id, inventory, getInventory(inventory.getMainHandStack()));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        SimpleInventory inventory = getInventory(stack);
        ItemStack teleporterMatrixItemStack = inventory.getStack(0);

        NbtCompound nbt = teleporterMatrixItemStack.getNbt();
        boolean linked = TeleporterMatrixItem.isLinked(teleporterMatrixItemStack);

        tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.status").formatted(Formatting.GRAY).
                append(Text.translatable("tooltip.energizedpower.teleporter_matrix.status." +
                        (linked?"linked":"unlinked")).formatted(linked?Formatting.GREEN:Formatting.RED)));

        if(linked) {
            tooltip.add(Text.empty());

            tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.location").
                    append(Text.literal(nbt.getInt("x") + " " + nbt.getInt("y") +
                            " " + nbt.getInt("z"))));
            tooltip.add(Text.translatable("tooltip.energizedpower.teleporter_matrix.dimension").
                    append(Text.literal(nbt.getString("dim"))));
        }

        tooltip.add(Text.empty());

        if(Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("tooltip.energizedpower.inventory_teleporter.txt.shift.1").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
            tooltip.add(Text.translatable("tooltip.energizedpower.inventory_teleporter.txt.shift.2").
                    formatted(Formatting.GRAY, Formatting.ITALIC));
        }else {
            tooltip.add(Text.translatable("tooltip.energizedpower.shift_details.txt").formatted(Formatting.YELLOW));
        }
    }

    public static SimpleInventory getInventory(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getOrCreateNbt();

        if(nbt.contains("inventory")) {
            DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);
            Inventories.readNbt(nbt.getCompound("inventory"), items);
            return new SimpleInventory(items.toArray(new ItemStack[0])) {
                @Override
                public void markDirty() {
                    super.markDirty();

                    itemStack.getOrCreateNbt().put("inventory", Inventories.writeNbt(new NbtCompound(), this.stacks));
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
                    return super.canPlayerUse(player) && player.getInventory().getMainHandStack() == itemStack;
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

                itemStack.getOrCreateNbt().put("inventory", Inventories.writeNbt(new NbtCompound(), this.stacks));
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
                return super.canPlayerUse(player) && player.getInventory().getMainHandStack() == itemStack;
            }

            @Override
            public int getMaxCountPerStack() {
                return 1;
            }
        };
    }

    public static void teleportPlayer(ItemStack itemStack, ServerPlayerEntity player) {
        World level = player.getWorld();

        EnergyStorage energyStorage = EnergyStorage.ITEM.find(itemStack, ContainerItemContext.ofPlayerHand(player,
                Hand.MAIN_HAND));
        if(energyStorage == null)
            return;

        SimpleInventory inventory = getInventory(itemStack);
        ItemStack teleporterMatrixItemStack = inventory.getStack(0);

        TeleporterBlockEntity.teleportPlayer(player, energyStorage, () -> SimpleEnergyItem.setStoredEnergyUnchecked(itemStack, 0),
                teleporterMatrixItemStack, level, null);
    }
}
