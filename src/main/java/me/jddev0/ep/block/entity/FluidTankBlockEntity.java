package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.screen.FluidTankMenu;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity
        extends AbstractFluidTankBlockEntity<SimpleFluidStorage>
        implements CheckboxUpdate {
    private final FluidTankBlock.Tier tier;

    private boolean ignoreNBT;
    private FluidStack fluidFilter = new FluidStack(FluidVariant.blank(), 1);

    public static BlockEntityType<FluidTankBlockEntity> getEntityTypeFromTier(FluidTankBlock.Tier tier) {
        return switch(tier) {
            case SMALL -> ModBlockEntities.FLUID_TANK_SMALL_ENTITY;
            case MEDIUM -> ModBlockEntities.FLUID_TANK_MEDIUM_ENTITY;
            case LARGE -> ModBlockEntities.FLUID_TANK_LARGE_ENTITY;
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
    protected SimpleFluidStorage initFluidStorage() {
        return new SimpleFluidStorage(baseTankCapacity) {
            @Override
            protected void onFinalCommit() {
                markDirty();
                syncFluidToPlayers(64);
            }

            private boolean isFluidValid(FluidVariant variant) {
                return fluidFilter.isEmpty() || (ignoreNBT?(
                        fluidFilter.getFluidVariant().isOf(variant.getFluid()) &&
                                fluidFilter.getFluidVariant().nbtMatches(variant.getNbt())):
                        fluidFilter.getFluidVariant().isOf(variant.getFluid()));
            }

            @Override
            protected boolean canInsert(FluidVariant variant) {
                return isFluidValid(variant);
            }

            @Override
            protected boolean canExtract(FluidVariant variant) {
                return isFluidValid(variant);
            }
        };
    }

    @Override
    protected PropertyDelegate initContainerData() {
        return new PropertyDelegate() {
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
            public int size() {
                return 1;
            }
        };
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        syncFluidToPlayer(player);
        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player,
                new FluidSyncS2CPacket(1, fluidFilter, 0, getPos()));

        return new FluidTankMenu(id, inventory, this, this.data);
    }

    public FluidTankBlock.Tier getTier() {
        return tier;
    }

    @Override
    protected void writeNbt(@NotNull NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putBoolean("ignore_nbt", ignoreNBT);

        nbt.put("fluid_filter", fluidFilter.toNBT(new NbtCompound()));
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        ignoreNBT = nbt.getBoolean("ignore_nbt");

        fluidFilter = FluidStack.fromNbt(nbt.getCompound("fluid_filter"));
    }

    public void setIgnoreNBT(boolean ignoreNBT) {
        this.ignoreNBT = ignoreNBT;
        markDirty(world, getPos(), getCachedState());
    }

    @Override
    public void setCheckbox(int checkboxId, boolean checked) {
        switch(checkboxId) {
            //Ignore NBT
            case 0 -> setIgnoreNBT(checked);
        }
    }

    public void setFluidFilter(FluidStack fluidFilter) {
        this.fluidFilter = new FluidStack(fluidFilter.getFluid(), fluidFilter.getFluidVariant().copyNbt(),
                fluidFilter.getDropletsAmount());
        markDirty(world, getPos(), getCachedState());

        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                getPos(), (ServerWorld)world, 32,
                new FluidSyncS2CPacket(1, fluidFilter, 0, getPos())
        );
    }

    public FluidStack getFluid(int tank) {
        return switch(tank) {
            case 0 -> super.getFluid(tank);
            case 1 -> fluidFilter;
            default -> null;
        };
    }

    public long getTankCapacity(int tank) {
        if(tank == 0)
            return super.getTankCapacity(tank);

        return 0;
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        switch(tank) {
            case 0 -> super.setFluid(tank, fluidStack);
            case 1 -> fluidFilter = new FluidStack(fluidStack.getFluid(), fluidStack.getFluidVariant().copyNbt(),
                    fluidStack.getDropletsAmount());
        }
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        if(tank == 0)
            super.setTankCapacity(tank, capacity);
    }
}