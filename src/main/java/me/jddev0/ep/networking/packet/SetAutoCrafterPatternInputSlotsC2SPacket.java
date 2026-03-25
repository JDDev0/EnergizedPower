package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.screen.AutoCrafterMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.ArrayList;
import java.util.List;

public final class SetAutoCrafterPatternInputSlotsC2SPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetAutoCrafterPatternInputSlotsC2SPacket> ID =
            new CustomPacketPayload.Type<>(EPAPI.id("set_auto_crafter_pattern_input_slots"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetAutoCrafterPatternInputSlotsC2SPacket> PACKET_CODEC =
            StreamCodec.ofMember(SetAutoCrafterPatternInputSlotsC2SPacket::write, SetAutoCrafterPatternInputSlotsC2SPacket::new);

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

    public SetAutoCrafterPatternInputSlotsC2SPacket(RegistryFriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();

        itemStacks = new ArrayList<>(9);
        for(int i = 0;i < 9;i++)
            itemStacks.add(ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer));

        recipeId = buffer.readIdentifier();
    }

    public void write(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);

        for(ItemStack itemStack:itemStacks)
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, itemStack);

        buffer.writeIdentifier(recipeId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void receive(SetAutoCrafterPatternInputSlotsC2SPacket data, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if(!context.player().mayBuild())
                return;

            Level level = context.player().level();
            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            AbstractContainerMenu menu = context.player().containerMenu;

            if(!(menu instanceof AutoCrafterMenu autoCrafterMenu))
                return;

            for(int i = 0;i < data.itemStacks.size();i++)
                autoCrafterMenu.getPatternSlots().setItem(i, data.itemStacks.get(i));

            autoCrafterBlockEntity.setRecipeIdForSetRecipe(ResourceKey.create(Registries.RECIPE, data.recipeId));

            autoCrafterBlockEntity.resetProgressAndMarkAsChanged();
        });
    }
}
