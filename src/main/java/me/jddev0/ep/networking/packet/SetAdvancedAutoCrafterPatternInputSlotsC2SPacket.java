package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public final class SetAdvancedAutoCrafterPatternInputSlotsC2SPacket implements CustomPayload {
    public static final CustomPayload.Id<SetAdvancedAutoCrafterPatternInputSlotsC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("set_advanced_auto_crafter_pattern_input_slots"));
    public static final PacketCodec<RegistryByteBuf, SetAdvancedAutoCrafterPatternInputSlotsC2SPacket> PACKET_CODEC =
            PacketCodec.of(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::write, SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::new);

    private final BlockPos pos;
    private final List<ItemStack> itemStacks;
    private final Identifier recipeId;

    public SetAdvancedAutoCrafterPatternInputSlotsC2SPacket(BlockPos pos, List<ItemStack> itemStacks, Identifier recipeId) {
        this.pos = pos;

        this.itemStacks = new ArrayList<>(itemStacks);

        while(this.itemStacks.size() < 9)
            this.itemStacks.add(ItemStack.EMPTY);

        this.recipeId = recipeId;
    }

    public SetAdvancedAutoCrafterPatternInputSlotsC2SPacket(RegistryByteBuf buffer) {
        pos = buffer.readBlockPos();

        itemStacks = new ArrayList<>(9);
        for(int i = 0;i < 9;i++)
            itemStacks.add(ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer));

        recipeId = buffer.readIdentifier();
    }

    public void write(final RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);

        for(ItemStack itemStack:itemStacks)
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, itemStack);

        buffer.writeIdentifier(recipeId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AdvancedAutoCrafterBlockEntity advancedAutoCrafterBlockEntity))
                return;

            ScreenHandler menu = context.player().currentScreenHandler;

            if(!(menu instanceof AdvancedAutoCrafterMenu advancedAutoCrafterMenu))
                return;

            int recipeIndex = advancedAutoCrafterBlockEntity.getCurrentRecipeIndex();

            for(int i = 0;i < data.itemStacks.size();i++)
                advancedAutoCrafterMenu.getPatternSlots()[recipeIndex].setStack(i, data.itemStacks.get(i));

            advancedAutoCrafterBlockEntity.setRecipeIdForSetRecipe(RegistryKey.of(RegistryKeys.RECIPE, data.recipeId));

            advancedAutoCrafterBlockEntity.resetProgressAndMarkAsChanged(recipeIndex);
        });
    }
}
