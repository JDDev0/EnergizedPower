package me.jddev0.ep.block.entity.base;

import com.mojang.logging.LogUtils;
import me.jddev0.ep.energy.IEnergizedPowerEnergyStorage;
import me.jddev0.ep.fluid.IEnergizedPowerFluidStorage;
import me.jddev0.ep.inventory.IEnergizedPowerItemStackHandler;
import me.jddev0.ep.machine.RedstoneOutput;
import me.jddev0.ep.machine.configuration.*;
import me.jddev0.ep.machine.upgrade.UpgradeModuleModifier;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.IOConfigurationSyncS2CPacket;
import me.jddev0.ep.util.EnergyUtils;
import me.jddev0.ep.util.FluidUtils;
import me.jddev0.ep.util.InventoryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity
        <E extends IEnergizedPowerEnergyStorage, I extends IEnergizedPowerItemStackHandler, F extends IEnergizedPowerFluidStorage>
        extends UpgradableInventoryFluidEnergyStorageBlockEntity<E, I, F>
        implements RedstoneModeUpdate, IRedstoneModeHandler, ComparatorModeUpdate, IComparatorModeHandler,
        IOConfigurationUpdate, SetIOConfigurationUpdate, IConfigurableIOMachine,
        RedstoneOutput {
    private static final Logger LOGGER = LogUtils.getLogger();

    protected @NotNull RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    protected @NotNull ComparatorMode comparatorMode = ComparatorMode.ITEM;

    private final Map<SlotType, List<SlotGroup>> slotGroups;
    private final Map<SlotType, IOConfiguration> ioConfigurations;

    public ConfigurableUpgradableInventoryFluidEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState,
                                                                        String machineName,
                                                                        long baseEnergyCapacity, long baseEnergyTransferRate,
                                                                        int slotCount,
                                                                        long baseTankCapacity,
                                                                        UpgradeModuleModifier... upgradeModifierSlots) {
        super(type, blockPos, blockState, machineName, baseEnergyCapacity, baseEnergyTransferRate, slotCount, baseTankCapacity,
                upgradeModifierSlots);

        HashMap<SlotType, List<SlotGroup>> slotGroups = new HashMap<>();
        HashMap<SlotType, IOConfiguration> ioConfigurations = new HashMap<>();
        for(SlotType slotType:getSupportedSlotTypes()) {
            slotGroups.put(slotType, List.copyOf(initSlotGroups(slotType)));
            ioConfigurations.put(slotType, initDefaultSlotConfiguration(slotType));
        }

        this.slotGroups = Map.copyOf(slotGroups);
        this.ioConfigurations = ioConfigurations;
    }

    protected List<SlotGroup> initSlotGroups(SlotType slotType) {
        return List.of();
    }

    protected IOConfiguration initDefaultSlotConfiguration(SlotType slotType) {
        return new IOConfiguration();
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.putInt("configuration.redstone_mode", redstoneMode.ordinal());
        view.putInt("configuration.comparator_mode", comparatorMode.ordinal());

        for(SlotType slotType:getSupportedSlotTypes()) {
            view.store("configuration." + slotType.getSerializedName() + "_io", IOConfiguration.CODEC, ioConfigurations.get(slotType));
        }
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);

        redstoneMode = RedstoneMode.fromIndex(view.getIntOr("configuration.redstone_mode", 0));
        comparatorMode = ComparatorMode.fromIndex(view.getIntOr("configuration.comparator_mode", 0));

        for(SlotType slotType:getSupportedSlotTypes()) {
            Optional<IOConfiguration> slotConfiguration = view.read("configuration." + slotType.getSerializedName() + "_io", IOConfiguration.CODEC);
            slotConfiguration.ifPresent(sc -> {
                if(sc.validate(getSlotGroups(slotType).size())) {
                    ioConfigurations.put(slotType, sc);
                }else {
                    LOGGER.warn("Invalid IO configuration for slot type \"" +  slotType.name() + "\" during load: Use default config");
                }
            });
        }
    }

    protected final void syncIOConfigurationToPlayer(Player player) {
        for(SlotType slotType:getSupportedSlotTypes())
            ModMessages.sendToPlayer(new IOConfigurationSyncS2CPacket(slotType, ioConfigurations.get(slotType), getBlockPos()), (ServerPlayer)player);
    }

    protected final void syncIOConfigurationToPlayers(int distance) {
        if(level != null && !level.isClientSide())
            for(SlotType slotType:getSupportedSlotTypes())
                ModMessages.sendToPlayersWithinXBlocks(
                        new IOConfigurationSyncS2CPacket(slotType, ioConfigurations.get(slotType), getBlockPos()),
                        getBlockPos(), (ServerLevel)level, distance
                );
    }

    @Override
    public int getRedstoneOutput() {
        return switch(comparatorMode) {
            case ITEM -> InventoryUtils.getRedstoneSignalFromItemStackHandler(itemHandler);
            case FLUID -> FluidUtils.getRedstoneSignalFromFluidHandler(fluidStorage);
            case ENERGY -> EnergyUtils.getRedstoneSignalFromEnergyStorage(energyStorage);
        };
    }

    @Override
    public void setNextRedstoneMode() {
        redstoneMode = RedstoneMode.fromIndex(redstoneMode.ordinal() + 1);
        setChanged();
    }

    @Override
    @NotNull
    public RedstoneMode @NotNull [] getAvailableRedstoneModes() {
        return RedstoneMode.values();
    }

    @Override
    @NotNull
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public boolean setRedstoneMode(@NotNull RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;
        setChanged();

        return true;
    }

    @Override
    public void setNextComparatorMode() {
        comparatorMode = ComparatorMode.fromIndex(comparatorMode.ordinal() + 1);
        setChanged();
    }

    @Override
    @NotNull
    public ComparatorMode @NotNull [] getAvailableComparatorModes() {
        return new ComparatorMode[] {
                ComparatorMode.ENERGY,
                ComparatorMode.ITEM,
                ComparatorMode.FLUID
        };
    }

    @Override
    @NotNull
    public ComparatorMode getComparatorMode() {
        return comparatorMode;
    }

    @Override
    public boolean setComparatorMode(@NotNull ComparatorMode comparatorMode) {
        this.comparatorMode = comparatorMode;
        setChanged();

        return true;
    }

    @Override
    public void setIOConfiguration(SlotType slotType, IOConfiguration ioConfiguration) {
        this.ioConfigurations.put(slotType, ioConfiguration);
    }

    @Override
    public void setIOConfigurationByPlayer(SlotType slotType, RelativeDirection direction, int slotGroupId, ServerPlayer player) {
        IOConfiguration ioConfiguration = this.ioConfigurations.get(slotType);
        if(ioConfiguration != null && slotGroupId >= -1 && slotGroupId < this.slotGroups.get(slotType).size()) {
            ioConfiguration.setSlotGroupId(direction, slotGroupId);

            setChanged();
            syncIOConfigurationToPlayers(32);

            //Important: On Fabric no invalidation is required, because the Storage<...> is not cached,
            // instead the lookup provider from blockEntity + side => Storage<...> is cached, because the provider doesn't change no invalidation is required
            level.setBlock(getBlockPos(), getBlockState(), 3);
            for(Direction dir:Direction.values()) {
                //TODO only update affect direction, add method to get facing block property (Horizontally or Complete)
                BlockPos neighborPos = getBlockPos().relative(dir);
                level.neighborChanged(level.getBlockState(neighborPos), neighborPos, getBlockState().getBlock(), null, false);
            }
        }else {
            LOGGER.warn("Invalid IO configuration packet received from player {}: (Slot Type: {}, Direction: {}, Slot Group ID: {})",
                    player, slotType.name(), direction, slotGroupId);
        }
    }

    @Override
    public @NotNull SlotType @NotNull [] getSupportedSlotTypes() {
        return new SlotType[] {
                SlotType.ITEM,
                SlotType.FLUID
        };
    }

    @Override
    public List<SlotGroup> getSlotGroups(@NotNull SlotType slotType) {
        return slotGroups.get(slotType);
    }

    @Override
    public IOConfiguration getIOConfiguration(@NotNull SlotType slotType) {
        return ioConfigurations.get(slotType);
    }
}