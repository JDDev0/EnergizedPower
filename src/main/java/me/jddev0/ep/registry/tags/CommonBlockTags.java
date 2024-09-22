package me.jddev0.ep.registry.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class CommonBlockTags {
    private CommonBlockTags() {}

    public static final TagKey<Block> ORES_TIN = TagKey.create(Registry.BLOCK_REGISTRY,
            new ResourceLocation("forge", "ores/tin"));

    public static final TagKey<Block> STORAGE_BLOCKS_SILICON = TagKey.create(Registry.BLOCK_REGISTRY,
            new ResourceLocation("forge", "storage_blocks/silicon"));
    public static final TagKey<Block> STORAGE_BLOCKS_RAW_TIN = TagKey.create(Registry.BLOCK_REGISTRY,
            new ResourceLocation("forge", "storage_blocks/raw_tin"));
    public static final TagKey<Block> STORAGE_BLOCKS_TIN = TagKey.create(Registry.BLOCK_REGISTRY,
            new ResourceLocation("forge", "storage_blocks/tin"));
}