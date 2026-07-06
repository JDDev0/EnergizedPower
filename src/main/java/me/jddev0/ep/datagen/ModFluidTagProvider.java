package me.jddev0.ep.datagen;

import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.registry.tags.CommonFluidTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ModFluidTagProvider extends FabricTagProvider.FluidTagProvider {
    public ModFluidTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        buildTag(CommonFluidTags.DIRTY_WATER).
                add(EPFluids.DIRTY_WATER,
                        EPFluids.FLOWING_DIRTY_WATER);

        buildTag(ConventionalFluidTags.EXPERIENCE).
                add(EPFluids.LIQUID_XP,
                        EPFluids.FLOWING_LIQUID_XP);
    }

    private TagBuilderFix buildTag(final TagKey<Fluid> tagKey) {
        return new TagBuilderFix(getOrCreateTagBuilder(tagKey));
    }

    public final static class TagBuilderFix {
        private final FabricTagProvider<Fluid>.FabricTagBuilder tagAppender;

        public TagBuilderFix(FabricTagProvider<Fluid>.FabricTagBuilder tagAppender) {
            this.tagAppender = tagAppender;
        }

        public TagBuilderFix add(final Fluid element) {
            tagAppender.add(element);

            return this;
        }

        public TagBuilderFix add(final Fluid... element) {
            Arrays.stream(element).forEach(this::add);

            return this;
        }

        public TagBuilderFix addTag(final TagKey<Fluid> tag) {
            tagAppender.addTag(tag);

            return this;
        }

        public TagBuilderFix addOptionalTag(final TagKey<Fluid> tag) {
            tagAppender.addOptionalTag(tag);

            return this;
        }
    }
}
