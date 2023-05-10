package me.jddev0.ep.integration.curios;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.util.ICuriosHelper;

import java.util.LinkedList;
import java.util.List;

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

    public static List<ItemStack> getCuriosItemStacks(Inventory inventory) {
        List<ItemStack> itemStacks = new LinkedList<>();

        if(!isCuriosAvailable())
            return itemStacks;

        ICuriosHelper curiosHelper = CuriosApi.getCuriosHelper();
        if(curiosHelper == null)
            return itemStacks;

        LazyOptional<IItemHandlerModifiable> itemHandlerModifiableLazyOptional = curiosHelper.
                getEquippedCurios(inventory.player);

        if(itemHandlerModifiableLazyOptional == null || !itemHandlerModifiableLazyOptional.isPresent())
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
