package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.screen.AutoCrafterMenu;
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

public final class SetAutoCrafterPatternInputSlotsC2SPacket implements CustomPayload {
    public static final CustomPayload.Id<SetAutoCrafterPatternInputSlotsC2SPacket> ID =
            new CustomPayload.Id<>(EPAPI.id("set_auto_crafter_pattern_input_slots"));
    public static final PacketCodec<RegistryByteBuf, SetAutoCrafterPatternInputSlotsC2SPacket> PACKET_CODEC =
            PacketCodec.of(SetAutoCrafterPatternInputSlotsC2SPacket::write, SetAutoCrafterPatternInputSlotsC2SPacket::new);

    private final BlockPos pos;
    private final List<ItemStack> itemStacks;
    private final Identifier recipeId;

    public SetAutoCrafterPatternInputSlotsC2SPacket(BlockPos pos, List<ItemStack> itemStacks, Identifier recipeId) {
        this.pos = pos;

        this.itemStacks = new ArrayList<>(itemStacks);

        while(this.itemStacks.size() < 9)
            this.itemStacks.add(ItemStack.EMPTY);

        this.recipeId = recipeId;
    }

    public SetAutoCrafterPatternInputSlotsC2SPacket(RegistryByteBuf buffer) {
        pos = buffer.readBlockPos();

        itemStacks = new ArrayList<>(9);
        for(int i = 0;i < 9;i++)
            itemStacks.add(ItemStack.OPTIONAL_PACKET_CODEC.decode(buffer));

        recipeId = buffer.readIdentifier();
    }

    public void write(RegistryByteBuf buffer) {
        buffer.writeBlockPos(pos);

        for(ItemStack itemStack:itemStacks)
            ItemStack.OPTIONAL_PACKET_CODEC.encode(buffer, itemStack);

        buffer.writeIdentifier(recipeId);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receive(SetAutoCrafterPatternInputSlotsC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().canModifyBlocks())
                return;

            World level = context.player().getEntityWorld();
            if(!level.isChunkLoaded(ChunkSectionPos.getSectionCoord(data.pos.getX()), ChunkSectionPos.getSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            ScreenHandler menu = context.player().currentScreenHandler;

            if(!(menu instanceof AutoCrafterMenu autoCrafterMenu))
                return;

            for(int i = 0;i < data.itemStacks.size();i++)
                autoCrafterMenu.getPatternSlots().setStack(i, data.itemStacks.get(i));

            autoCrafterBlockEntity.setRecipeIdForSetRecipe(RegistryKey.of(RegistryKeys.RECIPE, data.recipeId));

            autoCrafterBlockEntity.resetProgressAndMarkAsChanged();
        });
    }
}
