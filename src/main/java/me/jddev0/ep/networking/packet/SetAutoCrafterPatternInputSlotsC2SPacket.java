package me.jddev0.ep.networking.packet;

import me.jddev0.ep.block.entity.AutoCrafterBlockEntity;
import me.jddev0.ep.screen.AutoCrafterMenu;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public final class SetAutoCrafterPatternInputSlotsC2SPacket {
    private SetAutoCrafterPatternInputSlotsC2SPacket() {}

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        BlockPos pos = buf.readBlockPos();
        List<ItemStack> itemStacks = new ArrayList<>(9);
        for(int i = 0;i < 9;i++)
            itemStacks.add(buf.readItemStack());
        Identifier recipeId = buf.readIdentifier();

        server.execute(() -> {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if(!(blockEntity instanceof AutoCrafterBlockEntity autoCrafterBlockEntity))
                return;

            ScreenHandler menu = player.currentScreenHandler;

            if(!(menu instanceof AutoCrafterMenu autoCrafterMenu))
                return;

            for(int i = 0;i < itemStacks.size();i++)
                autoCrafterMenu.getPatternSlots().setStack(i, itemStacks.get(i));

            autoCrafterBlockEntity.setRecipeIdForSetRecipe(recipeId);

            autoCrafterBlockEntity.resetProgressAndMarkAsChanged();
        });
    }
}
