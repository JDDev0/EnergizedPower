package me.jddev0.ep.client.item.property.bool;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.component.EPDataComponentTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class ActiveProperty implements ConditionalItemModelProperty {
    public static final MapCodec<ActiveProperty> CODEC = MapCodec.unit(new ActiveProperty());

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity user, int seed, ItemDisplayContext itemDisplayContext) {
        return stack.getOrDefault(EPDataComponentTypes.ACTIVE, false);
    }

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return CODEC;
    }
}
