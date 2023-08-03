package me.jddev0.ep.event;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.item.EnergizedPowerBookItem;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.networking.ModMessages;
import me.jddev0.ep.networking.packet.OpenEnergizedPowerBookS2CPacket;
import me.jddev0.ep.villager.ModVillager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.IntTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = EnergizedPowerMod.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void addCustomTrades(VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        if(event.getType() == ModVillager.ELECTRICIAN_PROFESSION.get()) {
            //Level 1
            addOffer(trades, 1,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(Items.BOOK),
                    new ItemStack(ModItems.ENERGIZED_POWER_BOOK.get()),
                    3, 3, .02f);
            addOffer(trades, 1,
                    new ItemStack(Items.COPPER_INGOT, 2),
                    new ItemStack(Items.EMERALD, 1),
                    25, 1, .02f);
            addOffer(trades, 1,
                    new ItemStack(ModItems.SILICON.get(), 3),
                    new ItemStack(Items.EMERALD, 2),
                    15, 2, .02f);
            addOffer(trades, 1,
                    new ItemStack(Items.EMERALD, 6),
                    new ItemStack(ModItems.CABLE_INSULATOR.get(), 16),
                    5, 3, .02f);
            addOffer(trades, 1,
                    new ItemStack(Items.EMERALD, 9),
                    new ItemStack(ModItems.IRON_HAMMER.get()),
                    2, 3, .02f);

            //Level 2
            addOffer(trades, 2,
                    new ItemStack(Items.EMERALD, 35),
                    new ItemStack(ModBlocks.COPPER_CABLE_ITEM.get(), 6),
                    3, 5, .02f);
            {

                ItemStack energyItem = new ItemStack(ModItems.BATTERY_2.get());
                energyItem.getOrCreateTag().put("energy", IntTag.valueOf(128));

                addOffer(trades, 2,
                        new ItemStack(Items.EMERALD, 6),
                        new ItemStack(Items.COPPER_INGOT, 4),
                        energyItem,
                        3, 7, .02f);
            }
            {

                ItemStack energyItem = new ItemStack(ModItems.ENERGY_ANALYZER.get());
                energyItem.getOrCreateTag().put("energy", IntTag.valueOf(128));

                addOffer(trades, 2,
                        new ItemStack(Items.EMERALD, 6),
                        new ItemStack(Items.COPPER_INGOT, 12),
                        energyItem,
                        2, 8, .02f);
            }
            addOffer(trades, 2,
                    new ItemStack(ModItems.COPPER_PLATE.get(), 3),
                    new ItemStack(Items.EMERALD, 8),
                    15, 6, .02f);

            //Level 3
            addOffer(trades, 3,
                    new ItemStack(Items.EMERALD, 21),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(ModBlocks.COAL_ENGINE_ITEM.get()),
                    3, 10, .02f);
            addOffer(trades, 3,
                    new ItemStack(Items.EMERALD, 31),
                    new ItemStack(ModItems.BASIC_SOLAR_CELL.get(), 2),
                    new ItemStack(ModBlocks.SOLAR_PANEL_ITEM_1.get()),
                    3, 10, .02f);
            addOffer(trades, 3,
                    new ItemStack(Items.EMERALD, 33),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(ModBlocks.FLUID_FILLER_ITEM.get()),
                    3, 10, .02f);
            addOffer(trades, 3,
                    new ItemStack(Items.EMERALD, 38),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(ModBlocks.AUTO_CRAFTER_ITEM.get()),
                    3, 10, .02f);
            addOffer(trades, 3,
                    new ItemStack(Items.EMERALD, 46),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(ModBlocks.CHARGER_ITEM.get()),
                    3, 10, .02f);
            addOffer(trades, 3,
                    new ItemStack(ModItems.BASIC_SOLAR_CELL.get(), 3),
                    new ItemStack(Items.EMERALD, 9),
                    15, 9, .02f);

            //Level 4
            addOffer(trades, 4,
                    new ItemStack(Items.EMERALD, 34),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(ModBlocks.SAWMILL_ITEM.get()),
                    3, 20, .02f);
            addOffer(trades, 4,
                    new ItemStack(Items.EMERALD, 39),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(ModBlocks.CRUSHER_ITEM.get()),
                    3, 20, .02f);
            addOffer(trades, 4,
                    new ItemStack(Items.EMERALD, 52),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(ModBlocks.COMPRESSOR_ITEM.get()),
                    3, 20, .02f);
            {

                ItemStack energyItem = new ItemStack(ModItems.BATTERY_4.get());
                energyItem.getOrCreateTag().put("energy", IntTag.valueOf(512));

                addOffer(trades, 4,
                        new ItemStack(Items.EMERALD, 29),
                        new ItemStack(Items.COPPER_INGOT, 9),
                        energyItem,
                        2, 19, .02f);
            }
            addOffer(trades, 4,
                    new ItemStack(ModItems.SAWDUST.get(), 17),
                    new ItemStack(Items.EMERALD, 4),
                    20, 18, .02f);

            //Level 5
            addOffer(trades, 5,
                    new ItemStack(Items.EMERALD, 32),
                    new ItemStack(ModBlocks.BASIC_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(ModBlocks.THERMAL_GENERATOR_ITEM.get()),
                    1, 30, .02f);
            addOffer(trades, 5,
                    new ItemStack(Items.EMERALD_BLOCK, 9),
                    new ItemStack(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(ModBlocks.ENERGIZER_ITEM.get()),
                    1, 30, .02f);
            addOffer(trades, 5,
                    new ItemStack(Items.EMERALD_BLOCK, 12),
                    new ItemStack(ModBlocks.ADVANCED_MACHINE_FRAME_ITEM.get()),
                    new ItemStack(ModBlocks.LIGHTNING_GENERATOR_ITEM.get()),
                    1, 30, .02f);
            addOffer(trades, 5,
                    new ItemStack(ModItems.ENERGIZED_COPPER_INGOT.get()),
                    new ItemStack(Items.EMERALD, 23),
                    15, 30, .02f);
        }
    }

    private static void addOffer(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int level,
                                 ItemStack cost, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        trades.get(level).add((trader, rand) -> new MerchantOffer(cost, result, maxUses, xp, priceMultiplier));
    }

    private static void addOffer(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, int level,
                                 ItemStack costA, ItemStack costB, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        trades.get(level).add((trader, rand) -> new MerchantOffer(costA, costB, result, maxUses, xp, priceMultiplier));
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        handlePlayerLecternInteraction(event);
        if(event.isCanceled())
            return;

        handlePlayerInWorldCraftingInteract(event);
    }

    private static void handlePlayerLecternInteraction(PlayerInteractEvent.RightClickBlock event) {
        BlockPos blockPos = event.getPos();
        BlockEntity blockEntity = event.getLevel().getBlockEntity(blockPos);

        if(!(blockEntity instanceof LecternBlockEntity))
            return;

        LecternBlockEntity lecternBlockEntity = (LecternBlockEntity)blockEntity;

        BlockState blockState = event.getLevel().getBlockState(blockPos);
        if(!blockState.getValue(LecternBlock.HAS_BOOK))
            return;

        ItemStack bookItemStack = lecternBlockEntity.getBook();
        if(!bookItemStack.is(ModItems.ENERGIZED_POWER_BOOK.get()))
            return;

        Item bookItem = bookItemStack.getItem();
        if(!(bookItem instanceof EnergizedPowerBookItem))
            return;

        Player player = event.getEntity();

        if(!event.getLevel().isClientSide)
            ModMessages.sendToPlayer(new OpenEnergizedPowerBookS2CPacket(blockPos), (ServerPlayer)player);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static void handlePlayerInWorldCraftingInteract(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        if(!itemStack.is(Tags.Items.SHEARS))
            return;

        Level level = event.getLevel();

        BlockPos blockPos = event.getPos();
        BlockState blockState = level.getBlockState(blockPos);
        if(!blockState.is(BlockTags.WOOL))
            return;

        Player player = event.getEntity();

        if(!event.getLevel().isClientSide) {
            if(!player.isCreative())
                itemStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(event.getHand()));

            level.destroyBlock(blockPos, false, player);

            ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5,
                    new ItemStack(ModItems.CABLE_INSULATOR.get(), 18), 0, 0, 0);
            itemEntity.setPickUpDelay(20);
            level.addFreshEntity(itemEntity);

            level.playSound(null, blockPos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.f, 1.f);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
