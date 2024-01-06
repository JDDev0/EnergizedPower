package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import me.jddev0.ep.screen.FluidTankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

public class FluidTankBlockEntity extends BlockEntity implements MenuProvider, FluidStoragePacketUpdate {
    private final FluidTankBlock.Tier tier;
    private final FluidTank fluidStorage;
    private LazyOptional<IFluidHandler> lazyFluidStorage = LazyOptional.empty();

    public static BlockEntityType<FluidTankBlockEntity> getEntityTypeFromTier(FluidTankBlock.Tier tier) {
        return switch(tier) {
            case SMALL -> ModBlockEntities.FLUID_TANK_SMALL_ENTITY.get();
        };
    }

    public FluidTankBlockEntity(BlockPos blockPos, BlockState blockState, FluidTankBlock.Tier tier) {
        super(getEntityTypeFromTier(tier), blockPos, blockState);

        this.tier = tier;

        fluidStorage = new FluidTank(tier.getTankCapacity()) {
            @Override
            protected void onContentsChanged() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToPlayersWithinXBlocks(
                            new FluidSyncS2CPacket(0, fluid, capacity, getBlockPos()),
                            getBlockPos(), level.dimension(), 64
                    );
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower.fluid_tank_small");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToPlayer(new FluidSyncS2CPacket(0, fluidStorage.getFluid(), fluidStorage.getCapacity(), worldPosition), (ServerPlayer)player);

        return new FluidTankMenu(id, inventory, this);
    }

    public FluidTankBlock.Tier getTier() {
        return tier;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, FluidTankBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        //Sync item stacks to client every 5 seconds
        if(level.getGameTime() % 100 == 0) //TODO improve
            ModMessages.sendToPlayersWithinXBlocks(
                    new FluidSyncS2CPacket(0, blockEntity.getFluid(0), blockEntity.getTankCapacity(0), blockPos),
                    blockPos, level.dimension(), 64
            );
    }

    public int getRedstoneOutput() {
        float fullnessPercent = 0;
        boolean isEmptyFlag = true;

        FluidStack fluid = fluidStorage.getFluid();
        if(!fluid.isEmpty()) {
            fullnessPercent = (float)fluid.getAmount() / fluidStorage.getCapacity();
            isEmptyFlag = false;
        }

        return Math.min(Mth.floor(fullnessPercent * 14.f) + (isEmptyFlag?0:1), 15);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyFluidStorage = LazyOptional.of(() -> fluidStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyFluidStorage.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("fluid", fluidStorage.writeToNBT(new CompoundTag()));

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        fluidStorage.readFromNBT(nbt.getCompound("fluid"));
    }

    public FluidStack getFluid(int tank) {
        return fluidStorage.getFluid();
    }

    public int getTankCapacity(int tank) {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, int capacity) {
        fluidStorage.setCapacity(capacity);
    }
}