package me.jddev0.ep.world;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.BlockReplacement;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import java.util.List;

public final class ModConfiguredFeatures {
    private ModConfiguredFeatures() {}

    public static final ResourceKey<Feature> TIN_ORE_KEY = registerKey("tin_ore");

    public static void bootstrap(BootstrapContext<Feature> context) {
        RuleTest STONE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest DEEPSLATE_ORE_REPLACEABLES = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        register(context, TIN_ORE_KEY, new OreFeature(List.of(
                BlockReplacement.replace(STONE_ORE_REPLACEABLES, EPBlocks.TIN_ORE.get().defaultBlockState()),
                BlockReplacement.replace(DEEPSLATE_ORE_REPLACEABLES, EPBlocks.DEEPSLATE_TIN_ORE.get().defaultBlockState())
        ), 8));
    }

    public static ResourceKey<Feature> registerKey(String name) {
        return ResourceKey.create(Registries.FEATURE, EPAPI.id(name));
    }

    private static void register(
            BootstrapContext<Feature> context, ResourceKey<Feature> key,
            Feature feature) {
        context.register(key, feature);
    }
}
