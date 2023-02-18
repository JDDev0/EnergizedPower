package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.screen.AutoCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SetAutoCrafterPatternInputSlotsC2SPacket {
    private final BlockPos pos;
    private final List<ItemStack> itemStacks;

    public SetAutoCrafterPatternInputSlotsC2SPacket(BlockPos pos, List<ItemStack> itemStacks) {
        this.pos = pos;

        this.itemStacks = new ArrayList<>(itemStacks);

        while(this.itemStacks.size() < 9)
            this.itemStacks.add(ItemStack.EMPTY);
    }

    public SetAutoCrafterPatternInputSlotsC2SPacket(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        itemStacks = new ArrayList<>(9);
        for(int i = 0;i < 9;i++)
            itemStacks.add(buffer.readItem());
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        for(ItemStack itemStack:itemStacks)
            buffer.writeItemStack(itemStack, false);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            BlockEntity blockEntity = context.getSender().getLevel().getBlockEntity(pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            AbstractContainerMenu menu = context.getSender().containerMenu;

            if(!(menu instanceof AutoCrafterMenu autoCrafterMenu))
                return;

            for(int i = 0;i < itemStacks.size();i++)
                autoCrafterMenu.getPatternSlots().setItem(i, itemStacks.get(i));

            autoCrafterBlockEntity.resetProgressAndMarkAsChanged();
        });

        return true;
    }
}
