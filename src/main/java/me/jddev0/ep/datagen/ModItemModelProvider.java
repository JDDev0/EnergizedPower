package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, EnergizedPowerMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.SILICON);
    }

    private ItemModelBuilder basicItem(Holder<Item> item) {
        ResourceLocation itemID = Objects.requireNonNull(item.unwrapKey().orElseThrow()).location();

        return withExistingParent(itemID.getPath(), "generated")
                .texture("layer0", new ResourceLocation(itemID.getNamespace(), "item/" + itemID.getPath()));
    }
}
