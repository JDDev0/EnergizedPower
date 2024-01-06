package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.FluidTankBlock;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.fluid.FluidStoragePacketUpdate;
import me.jddev0.ep.fluid.SimpleFluidStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.screen.FluidTankMenu;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, FluidStoragePacketUpdate {
    private final FluidTankBlock.Tier tier;
    final SimpleFluidStorage fluidStorage;

    public static BlockEntityType<FluidTankBlockEntity> getEntityTypeFromTier(FluidTankBlock.Tier tier) {
        return switch(tier) {
            case SMALL -> ModBlockEntities.FLUID_TANK_SMALL_ENTITY;
        };
    }

    public FluidTankBlockEntity(BlockPos blockPos, BlockState blockState, FluidTankBlock.Tier tier) {
        super(getEntityTypeFromTier(tier), blockPos, blockState);

        this.tier = tier;

        fluidStorage = new SimpleFluidStorage(tier.getTankCapacity()) {
            @Override
            protected void onFinalCommit() {
                markDirty();

                if(world != null && !world.isClient()) {
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeInt(0);
                    getFluid().toPacket(buffer);
                    buffer.writeLong(capacity);
                    buffer.writeBlockPos(getPos());

                    ModMessages.sendServerPacketToPlayersWithinXBlocks(
                            getPos(), (ServerWorld)world, 32,
                            ModMessages.FLUID_SYNC_ID, buffer
                    );
                }
            }
        };
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.energizedpower.fluid_tank_small");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeInt(0);
        fluidStorage.getFluid().toPacket(buffer);
        buffer.writeLong(fluidStorage.getCapacity());
        buffer.writeBlockPos(getPos());

        ModMessages.sendServerPacketToPlayer((ServerPlayerEntity)player, ModMessages.FLUID_SYNC_ID, buffer);

        return new FluidTankMenu(id, inventory, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public FluidTankBlock.Tier getTier() {
        return tier;
    }

    public static void tick(World level, BlockPos blockPos, BlockState state, FluidTankBlockEntity blockEntity) {
        if(level.isClient())
            return;

        //Sync item stacks to client every 5 seconds
        if(level.getTime() % 100 == 0) { //TODO improve
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeInt(0);
            blockEntity.fluidStorage.getFluid().toPacket(buffer);
            buffer.writeLong(blockEntity.fluidStorage.getCapacity());
            buffer.writeBlockPos(blockPos);

            ModMessages.sendServerPacketToPlayersWithinXBlocks(
                    blockPos, (ServerWorld)level, 32,
                    ModMessages.FLUID_SYNC_ID, buffer
            );
        }
    }

    public int getRedstoneOutput() {
        float fullnessPercent = 0;
        boolean isEmptyFlag = true;

        FluidStack fluid = fluidStorage.getFluid();
        if(!fluidStorage.isEmpty()) {
            fullnessPercent = (float)fluid.getDropletsAmount() / fluidStorage.getCapacity();
            isEmptyFlag = false;
        }

        return Math.min(MathHelper.floor(fullnessPercent * 14.f) + (isEmptyFlag?0:1), 15);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("fluid", fluidStorage.toNBT(new NbtCompound()));

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(@NotNull NbtCompound nbt) {
        super.readNbt(nbt);

        fluidStorage.fromNBT(nbt.getCompound("fluid"));
    }

    public FluidStack getFluid(int tank) {
        return fluidStorage.getFluid();
    }

    public long getTankCapacity(int tank) {
        return fluidStorage.getCapacity();
    }

    @Override
    public void setFluid(int tank, FluidStack fluidStack) {
        fluidStorage.setFluid(fluidStack);
    }

    @Override
    public void setTankCapacity(int tank, long capacity) {
        //Does nothing (capacity is final)
    }
}