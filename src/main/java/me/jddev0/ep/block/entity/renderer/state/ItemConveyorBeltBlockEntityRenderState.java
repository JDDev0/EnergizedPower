package me.jddev0.ep.block.entity.renderer.state;

import me.jddev0.ep.block.EPBlockStateProperties;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ItemConveyorBeltBlockEntityRenderState extends BlockEntityRenderState {
    public ItemStackRenderState[] itemRenderStates;
    public EPBlockStateProperties.ConveyorBeltDirection facing;
}
