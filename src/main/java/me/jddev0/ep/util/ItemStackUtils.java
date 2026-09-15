package me.jddev0.ep.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SlotProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.CookingFuel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemStackUtils {
    private ItemStackUtils() {}

    public static List<ItemStack> combineItemStacks(List<ItemStack> itemStacks) {
        List<ItemStack> combinedItemStacks = new ArrayList<>();
        for(ItemStack itemStack:itemStacks) {
            boolean inserted = false;
            int amountLeft = itemStack.getCount();
            for(ItemStack combinedItemStack:combinedItemStacks) {
                if(ItemStack.isSameItemSameComponents(itemStack, combinedItemStack) &&
                        combinedItemStack.getMaxStackSize() > combinedItemStack.getCount()) {
                    int amount = Math.min(amountLeft, combinedItemStack.getMaxStackSize() - combinedItemStack.getCount());
                    amountLeft -= amount;

                    combinedItemStack.grow(amount);

                    if(amountLeft == 0) {
                        inserted = true;
                        break;
                    }
                }
            }

            if(!inserted)
                combinedItemStacks.add(itemStack.copyWithCount(amountLeft));
        }

        return combinedItemStacks;
    }

    public static @NotNull ItemStack fromNullableItemStackTemplate(@Nullable ItemStackTemplate itemStack) {
        if(itemStack == null)
            return ItemStack.EMPTY;

        return itemStack.create();
    }

    public static boolean isSameItemSameComponents(ItemStackTemplate a, ItemStackTemplate b) {
        if(a == null && b == null)
            return true;

        if(a == null || b == null || !a.is(b.item()))
            return false;

        return Objects.equals(a.components(), b.components());
    }

    public static boolean isItemCookingFuel(ItemStack fuelItem) {
        return fuelItem.has(DataComponents.COOKING_FUEL);
    }

    private static final SlotProvider DUMMY_SLOT_PROVIDER_FOR_LOOT_CONTEXT = new SlotProvider() {
        @Override
        public @Nullable SlotAccess getSlot(int slot) {
            return null;
        }
    };

    public static int getItemBurnDuration(ItemStack fuelItem, ServerLevel level, BlockEntity blockEntity) {
        LootContext lootContext = new LootContext.Builder(
                new LootParams.Builder(level)
                        .withParameter(LootContextParams.BLOCK_STATE, blockEntity.getBlockState())
                        .withParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockEntity.getBlockPos()))
                        .withParameter(LootContextParams.CONTAINER, DUMMY_SLOT_PROVIDER_FOR_LOOT_CONTEXT)
                        .create(LootContextParamSets.CONTAINER_PROCESS)
        ).create(Optional.empty());

        return ResolvableInt.getFromItem(fuelItem, DataComponents.COOKING_FUEL, CookingFuel::burnTime, lootContext, 0);
    }

    public static float getItemBurnSpeedMultiplier(ItemStack fuelItem, ServerLevel level, BlockEntity blockEntity) {
        LootContext lootContext = new LootContext.Builder(
                new LootParams.Builder(level)
                        .withParameter(LootContextParams.BLOCK_STATE, blockEntity.getBlockState())
                        .withParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockEntity.getBlockPos()))
                        .withParameter(LootContextParams.CONTAINER, DUMMY_SLOT_PROVIDER_FOR_LOOT_CONTEXT)
                        .create(LootContextParamSets.CONTAINER_PROCESS)
        ).create(Optional.empty());

        return ResolvableFloat.getFromItem(fuelItem, DataComponents.COOKING_FUEL, CookingFuel::speedMultiplier, lootContext, 1.f);
    }

    public static int getItemBurnDuration(ItemStack fuelItem, ServerLevel level, Entity entity, ItemStack toolItemStack) {
        LootContext lootContext = new LootContext.Builder(
                new LootParams.Builder(level)
                        .withParameter(LootContextParams.TARGET_ENTITY, entity)
                        .withParameter(LootContextParams.INTERACTING_ENTITY, entity)
                        .withParameter(LootContextParams.TOOL, toolItemStack)
                        .create(LootContextParamSets.ENTITY_INTERACT)
        ).create(Optional.empty());

        return ResolvableInt.getFromItem(fuelItem, DataComponents.COOKING_FUEL, CookingFuel::burnTime, lootContext, 0);
    }

    public static float getItemBurnSpeedMultiplier(ItemStack fuelItem, ServerLevel level, Entity entity, ItemStack toolItemStack) {
        LootContext lootContext = new LootContext.Builder(
                new LootParams.Builder(level)
                        .withParameter(LootContextParams.TARGET_ENTITY, entity)
                        .withParameter(LootContextParams.INTERACTING_ENTITY, entity)
                        .withParameter(LootContextParams.TOOL, toolItemStack)
                        .create(LootContextParamSets.ENTITY_INTERACT)
        ).create(Optional.empty());

        return ResolvableFloat.getFromItem(fuelItem, DataComponents.COOKING_FUEL, CookingFuel::speedMultiplier, lootContext, 1.f);
    }
}
