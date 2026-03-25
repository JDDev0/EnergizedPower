package me.jddev0.ep.block.entity.renderer.state;

import me.jddev0.ep.block.EPBlockStateProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

@Environment(EnvType.CLIENT)
public class ItemConveyorBeltBlockEntityRenderState extends BlockEntityRenderState {
    public ItemStackRenderState[] itemRenderStates;
    public EPBlockStateProperties.ConveyorBeltDirection facing;
}
