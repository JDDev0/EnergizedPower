package me.jddev0.ep.integration.curios;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    //TODO FIX CURIOS INTEGRATION
    public static List<ItemStack> getCuriosItemStacks(Inventory inventory) {
        List<ItemStack> itemStacks = new ArrayList<>();

        if(!isCuriosAvailable())
            return itemStacks;

        Optional<IItemHandlerModifiable> itemHandlerModifiableLazyOptional = CuriosApi.getCuriosInventory(inventory.player)
                .map(ICuriosItemHandler::getEquippedCurios);

        if(itemHandlerModifiableLazyOptional.isEmpty())
            return itemStacks;

        IItemHandlerModifiable itemHandlerModifiable = itemHandlerModifiableLazyOptional.orElseGet(null);
        for(int i = 0;i < itemHandlerModifiable.getSlots();i++) {
            ItemStack itemStack = itemHandlerModifiable.getStackInSlot(i);

            if(!itemStack.isEmpty())
                itemStacks.add(itemStack);
        }

        return itemStacks;
    }
}
