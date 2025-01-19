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
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class PressMoldMakerBlockEntity
        extends MenuInventoryStorageBlockEntity<SimpleInventory> {
    private List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> recipeList = new ArrayList<>();

    final InputOutputItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public PressMoldMakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(
                EPBlockEntities.PRESS_MOLD_MAKER_ENTITY, blockPos, blockState,

                "press_mold_maker",

                2
        );
    }

    @Override
    protected SimpleInventory initInventoryStorage() {
        return new SimpleInventory(slotCount) {
            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return switch(slot) {
                    case 0 -> world == null || stack.isOf(Items.CLAY_BALL);
                    case 1 -> false;
                    default -> super.isValid(slot, stack);
                };
            }

            @Override
            public void markDirty() {
                super.markDirty();

                PressMoldMakerBlockEntity.this.markDirty();

                if(world != null && !world.isClient()) {
                    List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> recipeList = createRecipeList();

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            new SyncPressMoldMakerRecipeListS2CPacket(getPos(), recipeList)
                    );
                }
            }
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> recipeList = createRecipeList();

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, new SyncPressMoldMakerRecipeListS2CPacket(
                getPos(), recipeList));

        return new PressMoldMakerMenu(id, this, inventory, itemHandler);
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(itemHandler);
    }

    public void craftItem(Identifier recipeId) {
        if(!(world instanceof ServerWorld serverWorld))
            return;

        Optional<RecipeEntry<?>> recipe = serverWorld.getRecipeManager().values().stream().
                filter(recipeHolder -> recipeHolder.id().getValue().equals(recipeId)).findFirst();

        if(recipe.isEmpty() || !(recipe.get().value() instanceof PressMoldMakerRecipe pressMoldMakerRecipe))
            return;

        if(!pressMoldMakerRecipe.matches(new ContainerRecipeInputWrapper(itemHandler), world) ||
                !InventoryUtils.canInsertItemIntoSlot(itemHandler, 1, pressMoldMakerRecipe.craft(null, world.getRegistryManager())))
            return;

        itemHandler.removeStack(0, pressMoldMakerRecipe.getClayCount());
        itemHandler.setStack(1, pressMoldMakerRecipe.craft(null, world.getRegistryManager()).copyWithCount(
                itemHandler.getStack(1).getCount() + pressMoldMakerRecipe.craft(null, world.getRegistryManager()).getCount()));
    }

    private List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> createRecipeList() {
        if(!(world instanceof ServerWorld serverWorld))
            return List.of();

        Collection<RecipeEntry<PressMoldMakerRecipe>> recipes = RecipeUtils.getAllRecipesFor(serverWorld, PressMoldMakerRecipe.Type.INSTANCE);
        return recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.id().getValue())).
                map(recipe -> Pair.of(recipe, recipe.value().matches(new ContainerRecipeInputWrapper(itemHandler), world))).
                collect(Collectors.toList());
    }

    public List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> recipeList) {
        this.recipeList = recipeList;
    }
}