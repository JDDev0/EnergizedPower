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
import net.minecraft.resources.Identifier;
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
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class PressMoldMakerBlockEntity
        extends MenuInventoryStorageBlockEntity<SimpleContainer> {
    private List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList = new ArrayList<>();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public PressMoldMakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.PRESS_MOLD_MAKER_ENTITY, blockPos, blockState,

                "press_mold_maker",

                2
        );
    }

    @Override
    protected SimpleContainer initInventoryStorage() {
        return new SimpleContainer(slotCount) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> level == null || stack.is(Items.CLAY_BALL);
                    case 1 -> false;
                    default -> super.canPlaceItem(slot, stack);
                };
            }

            @Override
            public void setChanged() {
                super.setChanged();

                PressMoldMakerBlockEntity.this.setChanged();

                if(level != null && !level.isClientSide()) {
                    List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList = createRecipeList();

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getBlockPos(), (ServerLevel)level, 32,
                            new SyncPressMoldMakerRecipeListS2CPacket(getBlockPos(), recipeList)
                    );
                }
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList = createRecipeList();

        ModMessages.sendServerPacketToPlayer((ServerPlayer)player, new SyncPressMoldMakerRecipeListS2CPacket(
                getBlockPos(), recipeList));

        return new PressMoldMakerMenu(id, this, inventory, itemHandler);
    }

    public int getRedstoneOutput() {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(itemHandler);
    }

    public void craftItem(Identifier recipeId) {
        if(!(level instanceof ServerLevel serverWorld))
            return;

        Optional<RecipeHolder<?>> recipe = serverWorld.recipeAccess().getRecipes().stream().
                filter(recipeHolder -> recipeHolder.id().identifier().equals(recipeId)).findFirst();

        if(recipe.isEmpty() || !(recipe.get().value() instanceof PressMoldMakerRecipe pressMoldMakerRecipe))
            return;

        if(!pressMoldMakerRecipe.matches(new ContainerRecipeInputWrapper(itemHandler), level) ||
                !InventoryUtils.canInsertItemIntoSlot(itemHandler, 1, pressMoldMakerRecipe.assemble(null, level.registryAccess())))
            return;

        itemHandler.removeItem(0, pressMoldMakerRecipe.getClayCount());
        itemHandler.setItem(1, pressMoldMakerRecipe.assemble(null, level.registryAccess()).copyWithCount(
                itemHandler.getItem(1).getCount() + pressMoldMakerRecipe.assemble(null, level.registryAccess()).getCount()));
    }

    private List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> createRecipeList() {
        if(!(level instanceof ServerLevel serverWorld))
            return List.of();

        Collection<RecipeHolder<PressMoldMakerRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverWorld, PressMoldMakerRecipe.Type.INSTANCE);
        return recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.id().identifier())).
                map(recipe -> Pair.of(recipe, recipe.value().matches(new ContainerRecipeInputWrapper(itemHandler), level))).
                collect(Collectors.toList());
    }

    public List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList) {
        this.recipeList = recipeList;
    }
}