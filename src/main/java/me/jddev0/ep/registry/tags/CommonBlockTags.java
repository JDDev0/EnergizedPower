package me.jddev0.ep.registry.tags;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class CommonBlockTags {
    private CommonBlockTags() {}

    public static final TagKey<Block> TIN_ORES = TagKey.of(Registry.BLOCK_KEY,
            Identifier.of("c", "tin_ores"));

    public static final TagKey<Block> SILICON_BLOCKS = TagKey.of(Registry.BLOCK_KEY,
            Identifier.of("c", "silicon_blocks"));
    public static final TagKey<Block> RAW_TIN_BLOCKS = TagKey.of(Registry.BLOCK_KEY,
            Identifier.of("c", "raw_tin_blocks"));
    public static final TagKey<Block> TIN_BLOCKS = TagKey.of(Registry.BLOCK_KEY,
            Identifier.of("c", "tin_blocks"));
}
