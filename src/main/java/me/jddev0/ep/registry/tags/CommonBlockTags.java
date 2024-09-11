package me.jddev0.ep.registry.tags;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class CommonBlockTags {
    private CommonBlockTags() {}

    public static final TagKey<Block> ORES_IN_GROUND_STONE = TagKey.of(RegistryKeys.BLOCK,
            Identifier.of("c", "ores_in_ground/stone"));
    public static final TagKey<Block> ORES_IN_GROUND_DEEPSLATE = TagKey.of(RegistryKeys.BLOCK,
            Identifier.of("c", "ores_in_ground/deepslate"));

    public static final TagKey<Block> ORES_TIN = TagKey.of(RegistryKeys.BLOCK,
            Identifier.of("c", "ores/tin"));

    public static final TagKey<Block> STORAGE_BLOCKS_SILICON = TagKey.of(RegistryKeys.BLOCK,
            Identifier.of("c", "storage_blocks/silicon"));
    public static final TagKey<Block> STORAGE_BLOCKS_RAW_TIN = TagKey.of(RegistryKeys.BLOCK,
            Identifier.of("c", "storage_blocks/raw_tin"));
    public static final TagKey<Block> STORAGE_BLOCKS_TIN = TagKey.of(RegistryKeys.BLOCK,
            Identifier.of("c", "storage_blocks/tin"));
}
