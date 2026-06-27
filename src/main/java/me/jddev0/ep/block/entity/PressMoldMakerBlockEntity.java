package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.inventory.EnergizedPowerItemStackHandler;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncPressMoldMakerRecipeListS2CPacket;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.screen.PressMoldMakerMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class PressMoldMakerBlockEntity
        extends MenuInventoryStorageBlockEntity<EnergizedPowerItemStackHandler> {
    private List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList = new ArrayList<>();

    private final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public PressMoldMakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.PRESS_MOLD_MAKER_ENTITY, blockPos, blockState,

                "press_mold_maker",

                2
        );
    }

    @Override
    protected EnergizedPowerItemStackHandler initInventoryStorage() {
        return new EnergizedPowerItemStackHandler(slotCount) {
            @Override
            public boolean isValid(int slot, @NotNull ItemVariant stack) {
                return switch(slot) {
                    case 0 -> level == null || stack.isOf(Items.CLAY_BALL);
                    case 1 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            protected void onFinalCommit(int slot, @NotNull ItemStack previousItemStack) {
                setChanged();

                if(slot == 0) {
                    if(level != null && !level.isClientSide()) {
                        List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList = createRecipeList();
                        ModMessages.sendToPlayersWithinXBlocks(
                                new SyncPressMoldMakerRecipeListS2CPacket(getBlockPos(), recipeList),
                                getBlockPos(), (ServerLevel)level, 32
                        );
                    }
                }
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList = createRecipeList();
        ModMessages.sendToPlayer(new SyncPressMoldMakerRecipeListS2CPacket(getBlockPos(), recipeList), (ServerPlayer)player);

        return new PressMoldMakerMenu(id, inventory, this);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
    }

    public @Nullable Storage<ItemVariant> getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public void craftItem(ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipe = level.getRecipeManager().getRecipes().stream().
                filter(recipeHolder -> recipeHolder.id().equals(recipeId)).findFirst();

        if(recipe.isEmpty() || !(recipe.get().value() instanceof PressMoldMakerRecipe pressMoldMakerRecipe))
            return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for(int i = 0;i < itemHandler.size();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        if(!pressMoldMakerRecipe.matches(new ContainerRecipeInputWrapper(inventory), level) ||
                !InventoryUtils.canInsertItemIntoSlot(inventory, 1, pressMoldMakerRecipe.getResultItem(level.registryAccess())))
            return;

        itemHandler.extractItem(0, pressMoldMakerRecipe.getClayCount());
        itemHandler.setStackInSlot(1, pressMoldMakerRecipe.getResultItem(level.registryAccess()).copyWithCount(
                itemHandler.getStackInSlot(1).getCount() + pressMoldMakerRecipe.getResultItem(level.registryAccess()).getCount()));
    }

    private List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> createRecipeList() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.size());
        for(int i = 0;i < itemHandler.size();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Collection<RecipeHolder<PressMoldMakerRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(PressMoldMakerRecipe.Type.INSTANCE);
        return recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.value().getResultItem(level.registryAccess()).getDescriptionId())).
                map(recipe -> Pair.of(recipe, recipe.value().matches(new ContainerRecipeInputWrapper(inventory), level))).
                collect(Collectors.toList());
    }

    public List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList) {
        this.recipeList = recipeList;
    }
}