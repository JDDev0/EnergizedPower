package me.jddev0.ep.world;

import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.util.List;

public final class ModConfiguredFeatures {
    private ModConfiguredFeatures() {}

    public static final RegistryKey<ConfiguredFeature<?, ?>> TIN_ORE_KEY = registerKey("tin_ore");

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> context) {
        RuleTest STONE_ORE_REPLACEABLES = new TagMatchRuleTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest DEEPSLATE_ORE_REPLACEABLES = new TagMatchRuleTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        register(context, TIN_ORE_KEY, Feature.ORE, new OreFeatureConfig(List.of(
                OreFeatureConfig.createTarget(STONE_ORE_REPLACEABLES, EPBlocks.TIN_ORE.getDefaultState()),
                OreFeatureConfig.createTarget(DEEPSLATE_ORE_REPLACEABLES, EPBlocks.DEEPSLATE_TIN_ORE.getDefaultState())
        ), 8));
    }

    public static RegistryKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE,
                EPAPI.id(name));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void register(
            Registerable<ConfiguredFeature<?, ?>> context, RegistryKey<ConfiguredFeature<?, ?>> key,
            F feature, FC featureConfiguration) {
        context.register(key, new ConfiguredFeature<>(feature, featureConfiguration));
    }
}
