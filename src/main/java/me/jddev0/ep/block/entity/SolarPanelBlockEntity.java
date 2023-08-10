package me.jddev0.ep.block.entity;

import me.jddev0.ep.block.SolarPanelBlock;
import me.jddev0.ep.energy.EnergyStoragePacketUpdate;
import me.jddev0.ep.energy.ExtractOnlyEnergyStorage;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.EnergySyncS2CPacket;
import me.jddev0.ep.screen.SolarPanelMenu;
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
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SolarPanelBlockEntity extends BlockEntity implements MenuProvider, EnergyStoragePacketUpdate {
    private final SolarPanelBlock.Tier tier;
    private final int maxTransfer;

    private final ExtractOnlyEnergyStorage energyStorage;
    private LazyOptional<IEnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    public static BlockEntityType<SolarPanelBlockEntity> getEntityTypeFromTier(SolarPanelBlock.Tier tier) {
        return switch(tier) {
            case TIER_1 -> ModBlockEntities.SOLAR_PANEL_ENTITY_1.get();
            case TIER_2 -> ModBlockEntities.SOLAR_PANEL_ENTITY_2.get();
            case TIER_3 -> ModBlockEntities.SOLAR_PANEL_ENTITY_3.get();
            case TIER_4 -> ModBlockEntities.SOLAR_PANEL_ENTITY_4.get();
            case TIER_5 -> ModBlockEntities.SOLAR_PANEL_ENTITY_5.get();
        };
    }

    public SolarPanelBlockEntity(BlockPos blockPos, BlockState blockState, SolarPanelBlock.Tier tier) {
        super(getEntityTypeFromTier(tier), blockPos, blockState);

        this.tier = tier;

        maxTransfer = tier.getMaxTransfer();
        int capacity = tier.getCapacity();
        energyStorage = new ExtractOnlyEnergyStorage(0, capacity, maxTransfer) {
            @Override
            protected void onChange() {
                setChanged();

                if(level != null && !level.isClientSide())
                    ModMessages.sendToAllPlayers(new EnergySyncS2CPacket(energy, capacity, getBlockPos()));
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.energizedpower." + tier.getResourceId());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        ModMessages.sendToPlayer(new EnergySyncS2CPacket(energyStorage.getEnergy(), energyStorage.getCapacity(),
                getBlockPos()), (ServerPlayer)player);

        return new SolarPanelMenu(id, inventory, this);
    }

    public SolarPanelBlock.Tier getTier() {
        return tier;
    }

    public static void tick(Level level, BlockPos blockPos, BlockState state, SolarPanelBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        int i = 4 * (level.getBrightness(LightLayer.SKY, blockPos) - level.getSkyDarken()); //(0 - 15) * 4 => (0 - 60)
        float f = level.getSunAngle(1.0F);
        if(i > 0) {
            float f1 = f < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);

            f += (f1 - f) * 0.2F;

            i = Math.round((float)i * Mth.cos(f));
        }

        i = Mth.clamp(i, 0, 60);

        blockEntity.energyStorage.setEnergy(Math.min(blockEntity.energyStorage.getCapacity(),
                blockEntity.energyStorage.getEnergy() + (int)(i/60.f * blockEntity.getTier().getPeakFePerTick())));

        transferEnergy(level, blockPos, state, blockEntity);
    }

    private static void transferEnergy(Level level, BlockPos blockPos, BlockState state, SolarPanelBlockEntity blockEntity) {
        if(level.isClientSide)
            return;

        BlockPos testPos = blockPos.relative(Direction.DOWN);

        BlockEntity testBlockEntity = level.getBlockEntity(testPos);
        if(testBlockEntity == null)
            return;

        LazyOptional<IEnergyStorage> energyStorageLazyOptional = testBlockEntity.getCapability(ForgeCapabilities.ENERGY, Direction.DOWN.getOpposite());
        if(!energyStorageLazyOptional.isPresent())
            return;

        IEnergyStorage energyStorage = energyStorageLazyOptional.orElse(null);
        if(!energyStorage.canReceive())
            return;

        int amount = energyStorage.receiveEnergy(Math.min(blockEntity.energyStorage.getEnergy(), blockEntity.maxTransfer), false);
        if(amount > 0)
            blockEntity.energyStorage.extractEnergy(amount, false);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ENERGY) {
            if(side == null || side == Direction.DOWN)
                return lazyEnergyStorage.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        lazyEnergyStorage.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("energy", energyStorage.saveNBT());

        super.saveAdditional(nbt);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        energyStorage.loadNBT(nbt.get("energy"));
    }

    public int getEnergy() {
        return energyStorage.getEnergy();
    }

    public int getCapacity() {
        return energyStorage.getCapacity();
    }

    @Override
    public void setEnergy(int energy) {
        energyStorage.setEnergyWithoutUpdate(energy);
    }

    @Override
    public void setCapacity(int capacity) {
        energyStorage.setCapacityWithoutUpdate(capacity);
    }
}
