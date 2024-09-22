package me.jddev0.ep.datagen;

import com.google.common.collect.ImmutableList;
import me.jddev0.ep.datagen.adavancement.ModBasicsAdvancements;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, existingFileHelper);
    }

    @Override
    protected void registerAdvancements(Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
        ImmutableList.of(
            new ModBasicsAdvancements()
        ).forEach(generator -> generator.accept(consumer, existingFileHelper));
    }
}
