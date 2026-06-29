package me.jddev0.ep.mixin.client.multiplayer;

import me.jddev0.ep.item.IEPItemExtension;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Inject(method = "performUseItemOn", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/player/LocalPlayer;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private static void performUseItemOnBeforeBlockInteraction(
            final LocalPlayer player, final InteractionHand hand, final BlockHitResult blockHit,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        ItemStack itemStack = player.getItemInHand(hand);

        if(itemStack.getItem() instanceof IEPItemExtension itemExtension) {
            UseOnContext context = new UseOnContext(player, hand, blockHit);
            InteractionResult ret = itemExtension.onItemUseFirst(context);
            if(ret != InteractionResult.PASS) {
                cir.setReturnValue(ret);
            }
        }
    }
}
