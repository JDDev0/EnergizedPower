package me.jddev0.ep.block.entity.renderer.state;

import me.jddev0.ep.block.EPBlockStateProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;

@Environment(EnvType.CLIENT)
public class ItemConveyorBeltBlockEntityRenderState extends BlockEntityRenderState {
    public ItemRenderState[] itemRenderStates;
    public EPBlockStateProperties.ConveyorBeltDirection facing;
}
