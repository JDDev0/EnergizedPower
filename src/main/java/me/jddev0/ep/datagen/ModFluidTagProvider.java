package me.jddev0.ep.datagen;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.fluid.EPFluids;
import me.jddev0.ep.registry.tags.CommonFluidTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ModFluidTagProvider extends FluidTagsProvider {
    public ModFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, EPAPI.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        buildTag(CommonFluidTags.DIRTY_WATER).
                add(EPFluids.DIRTY_WATER,
                        EPFluids.FLOWING_DIRTY_WATER);

        buildTag(Tags.Fluids.EXPERIENCE).
                add(EPFluids.LIQUID_XP,
                        EPFluids.FLOWING_LIQUID_XP);
    }

    private TagBuilderFix buildTag(final TagKey<Fluid> tagKey) {
        return new TagBuilderFix(tag(tagKey));
    }

    public final static class TagBuilderFix {
        private final TagAppender<Fluid, Fluid> tagAppender;

        public TagBuilderFix(TagAppender<Fluid, Fluid> tagAppender) {
            this.tagAppender = tagAppender;
        }

        public TagBuilderFix add(final Fluid element) {
            tagAppender.add(element);

            return this;
        }

        public TagBuilderFix add(final Supplier<? extends Fluid> element) {
            add(element.get());

            return this;
        }

        public TagBuilderFix add(final Fluid... element) {
            Arrays.stream(element).forEach(this::add);

            return this;
        }

        @SafeVarargs
        public final TagBuilderFix add(final Supplier<? extends Fluid>... element) {
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
