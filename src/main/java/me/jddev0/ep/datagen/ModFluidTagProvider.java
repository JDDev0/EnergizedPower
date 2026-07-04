package me.jddev0.ep.datagen;

import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.registry.tags.CommonFluidTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ModFluidTagProvider extends FabricTagsProvider.FluidTagsProvider {
    public ModFluidTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
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
        return new TagBuilderFix(tag(tagKey));
    }

    public final static class TagBuilderFix {
        private final TagAppender<Fluid> tagAppender;

        public TagBuilderFix(TagAppender<Fluid> tagAppender) {
            this.tagAppender = tagAppender;
        }

        public TagBuilderFix add(final Fluid element) {
            tagAppender.add(BuiltInRegistries.FLUID.getResourceKey(element).orElseThrow());

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
