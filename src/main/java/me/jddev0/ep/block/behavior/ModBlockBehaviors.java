package me.jddev0.ep.block.behavior;

import me.jddev0.ep.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ShearsDispenserBehavior;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ModBlockBehaviors {
    private ModBlockBehaviors() {}

    public static void register() {
        DispenserBlock.registerBehavior(Items.SHEARS, new ShearsDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer blockSource, ItemStack itemStack) {
                ServerWorld level = blockSource.getWorld();
                if(!level.isClient()) {
                    BlockPos blockPos = blockSource.getPos().offset(blockSource.getBlockState().get(DispenserBlock.FACING));
                    if(tryCraftCableInsulator(level, blockPos)) {
                        if(itemStack.damage(1, level.getRandom(), null))
                            itemStack.setCount(0);

                        setSuccess(true);

                        return itemStack;
                    }
                }

                return super.dispenseSilently(blockSource, itemStack);
            }

            private static boolean tryCraftCableInsulator(ServerWorld level, BlockPos blockPos) {
                BlockState blockstate = level.getBlockState(blockPos);
                if(blockstate.isIn(BlockTags.WOOL)) {
                    level.breakBlock(blockPos, false, null);

                    ItemEntity itemEntity = new ItemEntity(level, blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5,
                            new ItemStack(ModItems.CABLE_INSULATOR, 18), 0, 0, 0);
                    itemEntity.setPickupDelay(20);
                    level.spawnEntity(itemEntity);

                    level.playSound(null, blockPos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.f, 1.f);

                    return true;
                }

                return false;
            }
        });
    }
}
