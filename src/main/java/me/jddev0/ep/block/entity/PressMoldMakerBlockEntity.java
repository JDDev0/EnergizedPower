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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PressMoldMakerBlockEntity extends BlockEntity implements MenuProvider {
    private List<Pair<PressMoldMakerRecipe, Boolean>> recipeList = new ArrayList<>();

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if(slot == 0) {
                if(level != null && !level.isClientSide()) {
                    List<Pair<PressMoldMakerRecipe, Boolean>> recipeList = createRecipeList();
                    ModMessages.sendToPlayersWithinXBlocks(
                            new SyncPressMoldMakerRecipeListS2CPacket(getBlockPos(), recipeList),
                            getBlockPos(), level.dimension(), 32
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
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final LazyOptional<IItemHandler> lazyItemHandlerSided = LazyOptional.of(
            () -> new InputOutputItemHandler(itemHandler, (i, stack) -> i == 0, i -> i == 1));

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
        List<Pair<PressMoldMakerRecipe, Boolean>> recipeList = createRecipeList();
        ModMessages.sendToPlayer(new SyncPressMoldMakerRecipeListS2CPacket(getBlockPos(), recipeList), (ServerPlayer)player);

        return new PressMoldMakerMenu(id, inventory, this);
    }

    public int getRedstoneOutput() {
        return InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            if(side == null)
                return lazyItemHandler.cast();

            return lazyItemHandlerSided.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    public void drops(Level level, BlockPos worldPosition) {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        Containers.dropContents(level, worldPosition, inventory);
    }

    public void craftItem(ResourceLocation recipeId) {
        Optional<?> recipe = level.getRecipeManager().getRecipes().stream().
                filter(recipeHolder -> recipeHolder.getId().equals(recipeId)).findFirst();

        if(recipe.isEmpty() || !(recipe.get() instanceof PressMoldMakerRecipe pressMoldMakerRecipe))
            return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        if(!pressMoldMakerRecipe.matches(inventory, level) ||
                !canInsertItemIntoOutputSlot(inventory, pressMoldMakerRecipe.getResultItem(level.registryAccess())))
            return;

        itemHandler.extractItem(0, pressMoldMakerRecipe.getClayCount(), false);
        itemHandler.setStackInSlot(1, new ItemStack(pressMoldMakerRecipe.getResultItem(level.registryAccess()).getItem(),
                itemHandler.getStackInSlot(1).getCount() + pressMoldMakerRecipe.getResultItem(level.registryAccess()).getCount()));
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getItem(1);

        return (inventoryItemStack.isEmpty() || ItemStack.isSameItemSameTags(inventoryItemStack, itemStack)) &&
                inventoryItemStack.getMaxStackSize() >= inventoryItemStack.getCount() + itemStack.getCount();
    }

    private List<Pair<PressMoldMakerRecipe, Boolean>> createRecipeList() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0;i < itemHandler.getSlots();i++)
            inventory.setItem(i, itemHandler.getStackInSlot(i));

        List<PressMoldMakerRecipe> recipes = level.getRecipeManager().getAllRecipesFor(PressMoldMakerRecipe.Type.INSTANCE);
        return recipes.stream().map(recipe -> Pair.of(recipe, recipe.matches(inventory, level))).
                collect(Collectors.toList());
    }

    public List<Pair<PressMoldMakerRecipe, Boolean>> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Pair<PressMoldMakerRecipe, Boolean>> recipeList) {
        this.recipeList = recipeList;
    }
}