package me.jddev0.ep.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public final class InventoryComponent {
    public static final Codec<InventoryComponent> CODEC = ItemStack.OPTIONAL_CODEC.listOf().
            xmap(InventoryComponent::new, (component) -> {
                return component.stacks;
            });

    public static final PacketCodec<RegistryByteBuf, InventoryComponent> PACKET_CODEC = ItemStack.OPTIONAL_PACKET_CODEC.
            collect(PacketCodecs.toList()).xmap(InventoryComponent::new, (component) -> {
                return component.stacks;
            });

    private final List<ItemStack> stacks;

    public InventoryComponent(List<ItemStack> stacks) {
        this.stacks = stacks.stream().map(ItemStack::copy).toList();
    }

    public ItemStack get(int index) {
        return stacks.get(index).copy();
    }

    public Stream<ItemStack> stream() {
        return stacks.stream().map(ItemStack::copy);
    }

    public Iterable<ItemStack> iterate() {
        return Lists.transform(stacks, ItemStack::copy);
    }

    public int size() {
        return stacks.size();
    }

    public boolean isEmpty() {
        return stacks.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryComponent that = (InventoryComponent) o;

        if(stacks.size() != that.stacks.size())
            return false;

        for(int i = 0;i < stacks.size();i++)
            if(!ItemStack.areEqual(stacks.get(i), that.stacks.get(i)))
                return false;

        return true;
    }

    @Override
    public int hashCode() {
        int i = 0;

        ItemStack itemStack;
        for(Iterator<ItemStack> iter = stacks.iterator();iter.hasNext();i = i * 31 + ItemStack.hashCode(itemStack))
            itemStack = iter.next();

        return i;
    }

    @Override
    public String toString() {
        return "InventoryComponent{" +
                "stacks=" + stacks +
                '}';
    }
}
