package me.jddev0.ep.client.item.property.bool;

import com.mojang.serialization.MapCodec;
import me.jddev0.ep.component.EPDataComponentTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class WorkingProperty implements ConditionalItemModelProperty {
    public static final MapCodec<WorkingProperty> CODEC = MapCodec.unit(new WorkingProperty());

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity user, int seed, ItemDisplayContext itemDisplayContext) {
        return stack.getOrDefault(EPDataComponentTypes.WORKING, false);
    }

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return CODEC;
    }
}
