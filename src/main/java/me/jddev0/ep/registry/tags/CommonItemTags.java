package me.jddev0.ep.registry.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class CommonItemTags {
    private CommonItemTags() {}

    public static final TagKey<Item> ORES_TIN = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "ores/tin"));

    public static final TagKey<Item> STORAGE_BLOCKS_SILICON = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "storage_blocks/silicon"));
    public static final TagKey<Item> STORAGE_BLOCKS_RAW_TIN = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "storage_blocks/raw_tin"));
    public static final TagKey<Item> STORAGE_BLOCKS_TIN = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "storage_blocks/tin"));

    public static final TagKey<Item> RAW_MATERIALS_TIN = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "raw_materials/tin"));

    public static final TagKey<Item> DUSTS_WOOD = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "dusts/wood"));
    public static final TagKey<Item> DUSTS_CHARCOAL = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "dusts/charcoal"));
    public static final TagKey<Item> DUSTS_TIN = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "dusts/tin"));
    public static final TagKey<Item> DUSTS_COPPER = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "dusts/copper"));
    public static final TagKey<Item> DUSTS_IRON = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "dusts/iron"));
    public static final TagKey<Item> DUSTS_GOLD = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "dusts/gold"));

    public static final TagKey<Item> NUGGETS_TIN = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "nuggets/tin"));

    public static final TagKey<Item> SILICON = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "silicon"));

    public static final TagKey<Item> INGOTS_TIN = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "ingots/tin"));
    public static final TagKey<Item> INGOTS_STEEL = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "ingots/steel"));
    public static final TagKey<Item> INGOTS_REDSTONE_ALLOY = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "ingots/redstone_alloy"));
    public static final TagKey<Item> INGOTS_ADVANCED_ALLOY = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "ingots/advanced_alloy"));
    public static final TagKey<Item> INGOTS_ENERGIZED_COPPER = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "ingots/energized_copper"));
    public static final TagKey<Item> INGOTS_ENERGIZED_GOLD = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "ingots/energized_gold"));

    public static final TagKey<Item> PLATES = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "plates"));
    public static final TagKey<Item> PLATES_TIN = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "plates/tin"));
    public static final TagKey<Item> PLATES_COPPER = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "plates/copper"));
    public static final TagKey<Item> PLATES_IRON = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "plates/iron"));
    public static final TagKey<Item> PLATES_GOLD = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "plates/gold"));
    public static final TagKey<Item> PLATES_ADVANCED_ALLOY = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "plates/advanced_alloy"));
    public static final TagKey<Item> PLATES_ENERGIZED_COPPER = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "plates/energized_copper"));
    public static final TagKey<Item> PLATES_ENERGIZED_GOLD = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "plates/energized_gold"));

    public static final TagKey<Item> GEARS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "gears"));
    public static final TagKey<Item> GEARS_IRON = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "gears/iron"));

    public static final TagKey<Item> RODS_IRON = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "rods/iron"));

    public static final TagKey<Item> WIRES = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "wires"));
    public static final TagKey<Item> WIRES_TIN = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "wires/tin"));
    public static final TagKey<Item> WIRES_COPPER = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "wires/copper"));
    public static final TagKey<Item> WIRES_GOLD = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "wires/gold"));
    public static final TagKey<Item> WIRES_ENERGIZED_COPPER = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "wires/energized_copper"));
    public static final TagKey<Item> WIRES_ENERGIZED_GOLD = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "wires/energized_gold"));

    public static final TagKey<Item> TOOLS_HAMMERS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "tools/hammers"));
    public static final TagKey<Item> TOOLS_CUTTERS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("c", "tools/cutters"));
}
