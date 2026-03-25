package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.entity.base.FluidStorageSingleTankMethods;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.inventory.CombinedContainerData;
import me.jddev0.ep.inventory.data.BooleanValueContainerData;
import me.jddev0.ep.machine.CheckboxUpdate;
import me.jddev0.ep.machine.tier.FluidTankTier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.screen.FluidTankMenu;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity
        extends AbstractFluidTankBlockEntity<SimpleFluidStorage>
        implements CheckboxUpdate {
    private final FluidTankTier tier;

    private boolean ignoreNBT;
    private FluidStack fluidFilter = new FluidStack(FluidVariant.blank(), 1);

    public FluidTankBlockEntity(BlockPos blockPos, BlockState blockState, FluidTankTier tier) {
        super(
                tier.getEntityTypeFromTier(), blockPos, blockState,

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
                setChanged();
                syncFluidToPlayers(64);
            }

            private boolean isFluidValid(FluidVariant variant) {
                return fluidFilter.isEmpty() || (ignoreNBT?(
                        fluidFilter.getFluidVariant().isOf(variant.getFluid()) &&
                                fluidFilter.getFluidVariant().componentsMatch(variant.getComponents())):
                        fluidFilter.getFluidVariant().isOf(variant.getFluid()));
            }

            @Override
            protected boolean canInsert(FluidVariant variant) {
                return isFluidValid(variant);
            }
        };
    }

    @Override
    protected ContainerData initContainerData() {
        return new CombinedContainerData(
                new BooleanValueContainerData(() -> ignoreNBT, value -> ignoreNBT = value)
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        syncFluidToPlayer(player);
        ModMessages.sendServerPacketToPlayer((ServerPlayer)player,
                new FluidSyncS2CPacket(1, fluidFilter, 0, getBlockPos()));

        return new FluidTankMenu(id, inventory, this, this.data);
    }

    public FluidTankTier getTier() {
        return tier;
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putBoolean("ignore_nbt", ignoreNBT);

        view.storeNullable("fluid_filter", FluidStack.CODEC_MILLIBUCKETS, fluidFilter.isEmpty()?null:fluidFilter);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        ignoreNBT = view.getBooleanOr("ignore_nbt", false);

        fluidFilter = view.read("fluid_filter", FluidStack.CODEC_MILLIBUCKETS).
                orElseGet(() -> new FluidStack(FluidVariant.blank(), 1));
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

    public void setFluidFilter(FluidStack fluidFilter, RegistryAccess registries) {
        this.fluidFilter = new FluidStack(fluidFilter.getFluid(), fluidFilter.getFluidVariant().getComponents(),
                fluidFilter.getDropletsAmount());
        setChanged(level, getBlockPos(), getBlockState());

        ModMessages.sendServerPacketToPlayersWithinXBlocks(
                getBlockPos(), (ServerLevel)level, 32,
                new FluidSyncS2CPacket(1, fluidFilter, 0, getBlockPos())
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
            case 1 -> fluidFilter = new FluidStack(fluidStack.getFluid(), fluidStack.getFluidVariant().getComponents(),
                    fluidStack.getDropletsAmount());
        }
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        if(tank == 0)
            super.setTankCapacity(tank, capacity);
    }
}