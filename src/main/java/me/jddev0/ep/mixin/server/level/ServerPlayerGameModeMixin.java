package me.jddev0.ep.mixin.server.level;

import me.jddev0.ep.item.IEPItemExtension;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Inject(method = "useItemOn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private static void useItemOnBeforeBlockInteraction(
            final ServerPlayer player, final Level level, final ItemStack itemStack, final InteractionHand hand, final BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        if(itemStack != null && itemStack.getItem() instanceof IEPItemExtension itemExtension) {
            UseOnContext context = new UseOnContext(player, hand, hitResult);
            InteractionResult ret = itemExtension.onItemUseFirst(context);
            if(ret != InteractionResult.PASS) {
                cir.setReturnValue(ret);
            }
        }
    }
}
