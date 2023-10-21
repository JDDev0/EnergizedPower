package me.jddev0.ep.block.entity;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.block.entity.handler.CachedSidedInventoryStorage;
import me.jddev0.ep.block.entity.handler.InputOutputItemHandler;
import me.jddev0.ep.block.entity.handler.SidedInventoryWrapper;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.recipe.PressMoldMakerRecipe;
import me.jddev0.ep.screen.PressMoldMakerMenu;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PressMoldMakerBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {
    private List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> recipeList = new ArrayList<>();

    final CachedSidedInventoryStorage<PoweredFurnaceBlockEntity> cachedSidedInventoryStorage;
    final InputOutputItemHandler inventory;
    private final SimpleInventory internalInventory;

    public PressMoldMakerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.PRESS_MOLD_MAKER_ENTITY, blockPos, blockState);

        internalInventory = new SimpleInventory(2) {
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

                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeBlockPos(getPos());
                    buffer.writeInt(recipeList.size());
                    recipeList.forEach(entry -> {
                        buffer.writeIdentifier(entry.getFirst().id());
                        PressMoldMakerRecipe.Serializer.INSTANCE.write(buffer, entry.getFirst().value());
                        buffer.writeBoolean(entry.getSecond());
                    });

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.SYNC_PRESS_MOLD_MAKER_RECIPE_LIST_ID, buffer
                    );
                }
            }
        };
        inventory = new InputOutputItemHandler(new SidedInventoryWrapper(internalInventory) {
            @Override
            public int[] getAvailableSlots(Direction side) {
                return IntStream.range(0, 2).toArray();
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction dir) {
                return true;
            }
        }, (i, stack) -> i == 0, i -> i == 1);
        cachedSidedInventoryStorage = new CachedSidedInventoryStorage<>(inventory);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.press_mold_maker");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> recipeList = createRecipeList();

        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBlockPos(getPos());
        buffer.writeInt(recipeList.size());
        recipeList.forEach(entry -> {
            buffer.writeIdentifier(entry.getFirst().id());
            PressMoldMakerRecipe.Serializer.INSTANCE.write(buffer, entry.getFirst().value());
            buffer.writeBoolean(entry.getSecond());
        });
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.SYNC_PRESS_MOLD_MAKER_RECIPE_LIST_ID, buffer);

        return new PressMoldMakerMenu(id, this, inventory, internalInventory);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public int getRedstoneOutput() {
        return ScreenHandler.calculateComparatorOutput(internalInventory);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("inventory", Inventories.writeNbt(new NbtCompound(), internalInventory.stacks));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        Inventories.readNbt(nbt.getCompound("inventory"), internalInventory.stacks);
    }

    public void drops(World level, BlockPos worldPosition) {
        ItemScatterer.spawn(level, worldPosition, internalInventory.stacks);
    }

    public void craftItem(Identifier recipeId) {
        Optional<RecipeEntry<?>> recipe = world.getRecipeManager().values().stream().
                filter(recipeHolder -> recipeHolder.id().equals(recipeId)).findFirst();

        if(recipe.isEmpty() || !(recipe.get().value() instanceof PressMoldMakerRecipe pressMoldMakerRecipe))
            return;

        if(!pressMoldMakerRecipe.matches(inventory, world) ||
                !canInsertItemIntoOutputSlot(inventory, pressMoldMakerRecipe.getResult(world.getRegistryManager())))
            return;

        internalInventory.removeStack(0, pressMoldMakerRecipe.getClayCount());
        internalInventory.setStack(1, new ItemStack(pressMoldMakerRecipe.getResult(world.getRegistryManager()).getItem(),
                internalInventory.getStack(1).getCount() + pressMoldMakerRecipe.getResult(world.getRegistryManager()).getCount()));
    }

    private static boolean canInsertItemIntoOutputSlot(Inventory inventory, ItemStack itemStack) {
        ItemStack inventoryItemStack = inventory.getStack(1);

        return (inventoryItemStack.isEmpty() || ItemStack.canCombine(inventoryItemStack, itemStack)) &&
                inventoryItemStack.getMaxCount() >= inventoryItemStack.getCount() + itemStack.getCount();
    }

    private List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> createRecipeList() {
        List<RecipeEntry<PressMoldMakerRecipe>> recipes = world.getRecipeManager().listAllOfType(PressMoldMakerRecipe.Type.INSTANCE);
        return recipes.stream().
                sorted(Comparator.comparing(recipe -> recipe.value().getResult(world.getRegistryManager()).getTranslationKey())).
                map(recipe -> Pair.of(recipe, recipe.value().matches(inventory, world))).
                collect(Collectors.toList());
    }

    public List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> getRecipeList() {
        return recipeList;
    }

    public void setRecipeList(List<Pair<RecipeEntry<PressMoldMakerRecipe>, Boolean>> recipeList) {
        this.recipeList = recipeList;
    }
}