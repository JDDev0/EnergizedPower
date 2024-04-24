package me.jddev0.ep.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public final class CurrentItemStackComponent {
    public static final Codec<CurrentItemStackComponent> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ItemStack.OPTIONAL_CODEC.fieldOf("currentItem").forGetter((component) -> {
            return component.currentItem;
        })).apply(instance, CurrentItemStackComponent::new);
    });

    public static final PacketCodec<RegistryByteBuf, CurrentItemStackComponent> PACKET_CODEC = PacketCodec.of(
            CurrentItemStackComponent::write, CurrentItemStackComponent::new);

    private final ItemStack currentItem;

    public CurrentItemStackComponent(ItemStack currentItem) {
        this.currentItem = currentItem.copy();
    }

    public CurrentItemStackComponent(RegistryByteBuf buffer) {
        this(ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer));
    }

    public void write(RegistryByteBuf buffer) {
        ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, currentItem);
    }

    public ItemStack getCurrentItem() {
        return currentItem.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentItemStackComponent that = (CurrentItemStackComponent) o;
        return ItemStack.areEqual(currentItem, that.currentItem);
    }

    @Override
    public int hashCode() {
        return ItemStack.hashCode(currentItem);
    }

    @Override
    public String toString() {
        return "CurrentItemStackComponent{" +
                "currentItem=" + currentItem +
                '}';
    }
}
