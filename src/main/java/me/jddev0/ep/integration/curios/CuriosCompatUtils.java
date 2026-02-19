package me.jddev0.ep.integration.curios;

import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.transfer.EmptyResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import top.theillusivec4.curios.api.CuriosCapability;

public final class CuriosCompatUtils {
    private CuriosCompatUtils() {}

    public static boolean isCuriosAvailable() {
        try {
            Class.forName("top.theillusivec4.curios.api.CuriosApi");

            return true;
        }catch(ClassNotFoundException e) {
            return false;
        }
    }

    public static ResourceHandler<ItemResource> getCuriosItemStacks(Inventory inventory) {
        if(!isCuriosAvailable())
            return EmptyResourceHandler.instance();

        ResourceHandler<ItemResource> handler = inventory.player.getCapability(CuriosCapability.ITEM_HANDLER);
        if(handler == null)
            return EmptyResourceHandler.instance();

        return handler;
    }
}
