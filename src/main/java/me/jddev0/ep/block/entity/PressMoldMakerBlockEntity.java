package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncPressMoldMakerRecipeListS2CPacket;
import me.jddev0.ep.recipe.ContainerRecipeInputWrapper;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.screen.PressMoldMakerMenu;
import me.jddev0.ep.util.InventoryUtils;
import me.jddev0.ep.util.RecipeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class PressMoldMakerBlockEntity
        extends MenuInventoryStorageBlockEntity<ItemStackHandler> {
    private List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList = new ArrayList<>();

    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public PressMoldMakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.PRESS_MOLD_MAKER_ENTITY.get(), blockPos, blockState,

                "press_mold_maker",

                2
        );
    }

    @Override
    protected ItemStackHandler initInventoryStorage() {
        return new ItemStackHandler(slotCount) {
            @Override
            protected void onContentsChanged(int slot) {
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

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch(slot) {
                    case 0 -> level == null || stack.is(Items.CLAY_BALL);
                    case 1 -> false;
                    default -> super.isItemValid(slot, stack);
                };
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

    public @Nullable IItemHandler getItemHandlerCapability(@Nullable Direction side) {
        if(side == null)
            return itemHandler;

        return itemHandlerSided;
    }

    public void craftItem(ResourceLocation recipeId) {
        if(!(level instanceof ServerLevel serverLevel))
            return;

        Optional<RecipeHolder<?>> recipe = serverLevel.recipeAccess().getRecipes().stream().
                filter(recipeHolder -> recipeHolder.id().location().equals(recipeId)).findFirst();

        if(recipe.isEmpty() || !(recipe.get().value() instanceof PressMoldMakerRecipe pressMoldMakerRecipe))
            return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        if(!pressMoldMakerRecipe.matches(new ContainerRecipeInputWrapper(inventory), level) ||
                !InventoryUtils.canInsertItemIntoSlot(inventory, 1, pressMoldMakerRecipe.assemble(null, level.registryAccess())))
            return;

        itemHandler.extractItem(0, pressMoldMakerRecipe.getClayCount(), false);
        itemHandler.setStackInSlot(1, pressMoldMakerRecipe.assemble(null, level.registryAccess()).copyWithCount(
                itemHandler.getStackInSlot(1).getCount() + pressMoldMakerRecipe.assemble(null, level.registryAccess()).getCount()));
    }

    private List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> createRecipeList() {
        if(!(level instanceof ServerLevel serverLevel))
            return List.of();

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Collection<RecipeHolder<PressMoldMakerRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverLevel, PressMoldMakerRecipe.Type.INSTANCE);
        return recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.id().location())).
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