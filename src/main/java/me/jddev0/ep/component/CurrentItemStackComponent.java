package me.jddev0.ep.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public final class CurrentItemStackComponent {
    public static final Codec<CurrentItemStackComponent> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ItemStack.OPTIONAL_CODEC.fieldOf("currentItem").forGetter((component) -> {
            return component.currentItem;
        })).apply(instance, CurrentItemStackComponent::new);
    });

    public static final StreamCodec<RegistryFriendlyByteBuf, CurrentItemStackComponent> STREAM_CODEC = StreamCodec.ofMember(
            CurrentItemStackComponent::write, CurrentItemStackComponent::new);

    private final ItemStack currentItem;

    public CurrentItemStackComponent(ItemStack currentItem) {
        this.currentItem = currentItem.copy();
    }

    public CurrentItemStackComponent(RegistryFriendlyByteBuf buffer) {
        this(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, currentItem);
    }

    public ItemStack getCurrentItem() {
        return currentItem.copy();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentItemStackComponent that = (CurrentItemStackComponent) o;
        return ItemStack.matches(currentItem, that.currentItem);
    }

    @Override
    public int hashCode() {
        return ItemStack.hashItemAndComponents(currentItem);
    }

    @Override
    public String toString() {
        return "CurrentItemStackComponent{" +
                "currentItem=" + currentItem +
                '}';
    }
}
