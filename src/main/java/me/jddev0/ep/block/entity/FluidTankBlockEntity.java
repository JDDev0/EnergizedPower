package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.block.entity.base.MenuFluidStorageBlockEntity;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.screen.FluidTankMenu;
import me.jddev0.ep.util.FluidUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity
        extends MenuFluidStorageBlockEntity<FluidTank>
        implements CheckboxUpdate {
    private final FluidTankBlock.Tier tier;

    private boolean ignoreNBT;
    private FluidStack fluidFilter = FluidStack.EMPTY;

    public static BlockEntityType<FluidTankBlockEntity> getEntityTypeFromTier(FluidTankBlock.Tier tier) {
        return switch(tier) {
            case SMALL -> ModBlockEntities.FLUID_TANK_SMALL_ENTITY.get();
            case MEDIUM -> ModBlockEntities.FLUID_TANK_MEDIUM_ENTITY.get();
            case LARGE -> ModBlockEntities.FLUID_TANK_LARGE_ENTITY.get();
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
                        fluidFilter.isFluidEqual(stack));
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

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidTankBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        //Sync item stacks to client every 5 seconds
        if(level.getGameTime() % 100 == 0) //TODO improve
            blockEntity.syncFluidToPlayers(64);
    }

    public int getRedstoneOutput() {
        return FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.putBoolean("ignore_nbt", ignoreNBT);

        nbt.put("fluid_filter", fluidFilter.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        ignoreNBT = nbt.getBoolean("ignore_nbt");

        fluidFilter = FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid_filter"));
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
                getBlockPos(), level.dimension(), 64
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