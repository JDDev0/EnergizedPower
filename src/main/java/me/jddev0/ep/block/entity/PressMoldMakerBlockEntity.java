package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.SyncPressMoldMakerRecipeListS2CPacket;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.screen.PressMoldMakerMenu;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PressMoldMakerBlockEntity extends BlockEntity implements MenuProvider {
    private List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList = new ArrayList<>();

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
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
    private final IItemHandler itemHandlerSided = new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1);

    public PressMoldMakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.PRESS_MOLD_MAKER_ENTITY.get(), blockPos, blockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.press_mold_maker");
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

    @Override
    protected void saveAdditional(CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        nbt.put("inventory", itemHandler.serializeNBT(registries));

        super.saveAdditional(nbt, registries);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        itemHandler.deserializeNBT(registries, nbt.getCompound("inventory"));
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
    }

    public void craftItem(ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipe = level.getRecipeManager().getRecipes().stream().
                filter(recipeHolder -> recipeHolder.id().equals(recipeId)).findFirst();

        if(recipe.isEmpty() || !(recipe.get().value() instanceof PressMoldMakerRecipe pressMoldMakerRecipe))
            return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        if(!pressMoldMakerRecipe.matches(inventory, level) ||
                !InventoryUtils.canInsertItemIntoSlot(inventory, 1, pressMoldMakerRecipe.getResultItem(level.registryAccess())))
            return;

        itemHandler.extractItem(0, pressMoldMakerRecipe.getClayCount(), false);
        itemHandler.setStackInSlot(1, pressMoldMakerRecipe.getResultItem(level.registryAccess()).copyWithCount(
                itemHandler.getStackInSlot(1).getCount() + pressMoldMakerRecipe.getResultItem(level.registryAccess()).getCount()));
    }

    private List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> createRecipeList() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        List<RecipeHolder<PressMoldMakerRecipe>> recipes = level.getRecipeManager().getAllRecipesFor(PressMoldMakerRecipe.Type.INSTANCE);
        return recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.value().getResultItem(level.registryAccess()).getDescriptionId())).
                map(recipe -> Pair.of(recipe, recipe.value().matches(inventory, level))).
                collect(Collectors.toList());
    }

    public List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Pair<RecipeHolder<PressMoldMakerRecipe>, Boolean>> recipeList) {
        this.recipeList = recipeList;
    }
}