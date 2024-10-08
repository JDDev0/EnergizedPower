package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.screen.FluidTankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity
        extends AbstractFluidTankBlockEntity<FluidTank>
        implements CheckboxUpdate {
    private final FluidTankBlock.Tier tier;

    private boolean ignoreNBT;
    private FluidStack fluidFilter = FluidStack.EMPTY;

    public static BlockEntityType<FluidTankBlockEntity> getEntityTypeFromTier(FluidTankBlock.Tier tier) {
        return switch(tier) {
            case SMALL -> EPBlockEntities.FLUID_TANK_SMALL_ENTITY.get();
            case MEDIUM -> EPBlockEntities.FLUID_TANK_MEDIUM_ENTITY.get();
            case LARGE -> EPBlockEntities.FLUID_TANK_LARGE_ENTITY.get();
        };
    }

    public FluidTankBlockEntity(BlockPos blockPos, BlockState blockState, FluidTankBlock.Tier tier) {
        super(
                getEntityTypeFromTier(tier), blockPos, blockState,

                tier.getResourceId(),

                FluidStorageSingleTankMethods.INSTANCE,
                tier.getTankCapacity()
        );

        this.tier = tier;
    }

    @Override
    protected FluidTank initFluidStorage() {
        return new FluidTank(baseTankCapacity) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                syncFluidToPlayers(64);
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                if(!super.isFluidValid(stack))
                    return false;

                return fluidFilter.isEmpty() || (ignoreNBT?fluidFilter.getFluid().isSame(stack.getFluid()):
                        FluidStack.isSameFluidSameComponents(fluidFilter, stack));
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch(index) {
                    case 0 -> ignoreNBT?1:0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch(index) {
                    case 0 -> FluidTankBlockEntity.this.ignoreNBT = value != 0;
                }
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncFluidToPlayer(player);
        ModMessages.sendToPlayer(new FluidSyncS2CPacket(1, fluidFilter, 0, worldPosition), (ServerPlayer)player);

        return new FluidTankMenu(id, inventory, this, data);
    }

    public FluidTankBlock.Tier getTier() {
        return tier;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);

        nbt.putBoolean("ignore_nbt", ignoreNBT);

        nbt.put("fluid_filter", fluidFilter.saveOptional(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);

        ignoreNBT = nbt.getBoolean("ignore_nbt");

        fluidFilter = FluidStack.parseOptional(registries, nbt.getCompound("fluid_filter"));
    }

    public void setIgnoreNBT(boolean ignoreNBT) {
        this.ignoreNBT = ignoreNBT;
        setChanged(level, getBlockPos(), getBlockState());
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Ignore NBT
            case 0 -> setIgnoreNBT(checked);
        }
    }

    public void setFluidFilter(FluidStack fluidFilter) {
        this.fluidFilter = fluidFilter.copy();
        setChanged(level, getBlockPos(), getBlockState());

        ModMessages.sendToPlayersWithinXBlocks(
                new FluidSyncS2CPacket(1, fluidFilter, 0, getBlockPos()),
                getBlockPos(), (ServerLevel)level, 64
        );
    }

    public FluidStack getFluid(int tank) {
        return switch(tank) {
            case 0 -> super.getFluid(tank);
            case 1 -> fluidFilter;
            default -> null;
        };
    }

    public int getTankCapacity(int tank) {
        if(tank == 0)
            return super.getTankCapacity(tank);

        return 0;
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        switch(tank) {
            case 0 -> super.setFluid(tank, fluidStack);
            case 1 -> fluidFilter = fluidStack.copy();
        }
    }

    @Override
    public void setTankCapacity(int tank, int capacity) {
        if(tank == 0)
            super.setTankCapacity(tank, capacity);
    }
}