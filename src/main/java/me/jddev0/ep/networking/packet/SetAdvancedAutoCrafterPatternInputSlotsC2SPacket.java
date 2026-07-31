package me.jddev0.ep.networking.packet;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.entity.AdvancedAutoCrafterBlockEntity;
import me.jddev0.ep.screen.AdvancedAutoCrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class SetAdvancedAutoCrafterPatternInputSlotsC2SPacket implements CustomPacketPayload {
    public static final Type<SetAdvancedAutoCrafterPatternInputSlotsC2SPacket> ID =
            new Type<>(EPAPI.id("set_advanced_auto_crafter_pattern_input_slots"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetAdvancedAutoCrafterPatternInputSlotsC2SPacket> STREAM_CODEC =
            StreamCodec.ofMember(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::write, SetAdvancedAutoCrafterPatternInputSlotsC2SPacket::new);

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

    public SetAdvancedAutoCrafterPatternInputSlotsC2SPacket(RegistryFriendlyByteBuf buffer) {
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
    @NotNull
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handle(SetAdvancedAutoCrafterPatternInputSlotsC2SPacket data, IPayloadContext context) {
        context.enqueueWork(() -> {
            if(!(context.player().level() instanceof ServerLevel level) || !(context.player() instanceof ServerPlayer player))
                return;

            if(!level.hasChunk(SectionPos.blockToSectionCoord(data.pos.getX()), SectionPos.blockToSectionCoord(data.pos.getZ())))
                return;

            BlockEntity blockEntity = level.getBlockEntity(data.pos);
            if(!(blockEntity instanceof AdvancedAutoCrafterBlockEntity advancedAutoCrafterBlockEntity))
                return;

            AbstractContainerMenu menu = player.containerMenu;

            if(!(menu instanceof AdvancedAutoCrafterMenu advancedAutoCrafterMenu))
                return;

            int recipeIndex = advancedAutoCrafterBlockEntity.getCurrentRecipeIndex();

            for(int i = 0;i < data.itemStacks.size();i++)
                advancedAutoCrafterMenu.getPatternSlots()[recipeIndex].setItem(i, data.itemStacks.get(i));

            advancedAutoCrafterBlockEntity.setRecipeIdForSetRecipe(ResourceKey.create(Registries.RECIPE, data.recipeId));

            advancedAutoCrafterBlockEntity.resetProgressAndMarkAsChanged(recipeIndex);
        });
    }
}
