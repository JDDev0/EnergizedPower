package me.jddev0.ep.block.entity.base;

import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.FluidSyncS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public final class FluidStorageSingleTankMethods implements FluidStorageMethods<SimpleFluidStorage> {
    public static final FluidStorageSingleTankMethods INSTANCE = new FluidStorageSingleTankMethods();

    private FluidStorageSingleTankMethods() {}

    @Override
    public void saveFluidStorage(@NotNull SimpleFluidStorage fluidStorage, ValueOutput view) {
        FluidStack fluid = fluidStorage.getFluid();
        view.child("fluid").storeNullable("Fluid", FluidStack.CODEC, fluid.isEmpty()?null:fluid);
    }

    @Override
    public void loadFluidStorage(@NotNull SimpleFluidStorage fluidStorage, ValueInput view) {
        FluidStack fluid = view.child("fluid").flatMap(subView -> subView.read("Fluid", FluidStack.CODEC)).
                orElse(FluidStack.EMPTY);
        fluidStorage.setFluid(fluid);
    }

    @Override
    public void syncFluidToPlayer(SimpleFluidStorage fluidStorage, Player player, BlockPos pos) {
        ModMessages.sendToPlayer(new FluidSyncS2CPacket(0, fluidStorage.getFluid(), fluidStorage.getCapacity(),
                pos), (ServerPlayer)player);
    }

    @Override
    public void syncFluidToPlayers(SimpleFluidStorage fluidStorage, Level level, BlockPos pos, int distance) {
        ModMessages.sendToPlayersWithinXBlocks(
                new FluidSyncS2CPacket(0, fluidStorage.getFluid(), fluidStorage.getCapacity(), pos),
                pos, (ServerLevel)level, distance
        );
    }

    @Override
    public FluidStack getFluid(SimpleFluidStorage fluidStorage, int tank) {
        return fluidStorage.getFluid();
    }

    @Override
    public int getTankCapacity(SimpleFluidStorage fluidStorage, int tank) {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setFluid(SimpleFluidStorage fluidStorage, int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(SimpleFluidStorage fluidStorage, int tank, int capacity) {
        //Does nothing (capacity is final)
    }
}
