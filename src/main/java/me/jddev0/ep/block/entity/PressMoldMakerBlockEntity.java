package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.entity.base.MenuInventoryStorageBlockEntity;
import me.jddev0.ep.inventory.InputOutputItemHandler;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncPressMoldMakerRecipeListS2CPacket;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.screen.PressMoldMakerMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class PressMoldMakerBlockEntity
        extends MenuInventoryStorageBlockEntity<SimpleInventory> {
    private List<Pair<PressMoldMakerRecipe, Boolean>> recipeList = new ArrayList<>();

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
                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            new SyncPressMoldMakerRecipeListS2CPacket(getPos(), createRecipeList())
                    );
                }
            }
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new SyncPressMoldMakerRecipeListS2CPacket(getPos(), createRecipeList()));

        return new PressMoldMakerMenu(id, this, inventory, itemHandler);
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(itemHandler);
    }

    public void craftItem(Identifier recipeId) {
        Optional<?> recipe = world.getRecipeManager().values().stream().
                filter(recipeHolder -> recipeHolder.getId().equals(recipeId)).findFirst();

        if(recipe.isEmpty() || !(recipe.get() instanceof PressMoldMakerRecipe pressMoldMakerRecipe))
            return;

        if(!pressMoldMakerRecipe.matches(itemHandler, world) ||
                !InventoryUtils.canInsertItemIntoSlot(itemHandler, 1, pressMoldMakerRecipe.getOutput(world.getRegistryManager())))
            return;

        itemHandler.removeStack(0, pressMoldMakerRecipe.getClayCount());
        itemHandler.setStack(1, pressMoldMakerRecipe.getOutput(world.getRegistryManager()).copyWithCount(
                itemHandler.getStack(1).getCount() + pressMoldMakerRecipe.getOutput(world.getRegistryManager()).getCount()));
    }

    private List<Pair<PressMoldMakerRecipe, Boolean>> createRecipeList() {
        List<PressMoldMakerRecipe> recipes = world.getRecipeManager().listAllOfType(PressMoldMakerRecipe.Type.INSTANCE);
        return recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.getOutput(world.getRegistryManager()).getTranslationKey())).
                map(recipe -> Pair.of(recipe, recipe.matches(itemHandler, world))).
                collect(Collectors.toList());
    }

    public List<Pair<PressMoldMakerRecipe, Boolean>> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Pair<PressMoldMakerRecipe, Boolean>> recipeList) {
        this.recipeList = recipeList;
    }
}