package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.fluid.FluidStack;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.registry.tags.CommonItemTags;
import me.jddev0.ep.util.FluidUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    public void generate(RecipeExporter output) {
        buildCookingRecipes(output);
        buildSmithingRecipes(output);
        buildPressMoldMakerRecipes(output);
        buildAlloyFurnaceRecipes(output);
        buildCompressorRecipes(output);
        buildCrusherRecipes(output);
        buildPulverizerRecipes(output);
        buildSawmillRecipes(output);
        buildPlantGrowthChamberRecipes(output);
        buildPlantGrowthChamberFertilizerRecipes(output);
        buildMetalPressRecipes(output);
        buildHeatGeneratorRecipes(output);
        buildThermalGeneratorRecipes(output);
        buildStoneSolidifierRecipes(output);
        buildAssemblingMachineRecipes(output);
        buildFiltrationPlantRecipes(output);
        buildFluidTransposerRecipes(output);
        buildChargerRecipes(output);
        buildEnergizerRecipes(output);
        buildCrystalGrowthChamberRecipes(output);
    }

    private void buildCookingRecipes(RecipeExporter output) {
        addBlastingAndSmeltingRecipes(output, CommonItemTags.RAW_MATERIALS_TIN, new ItemStack(ModItems.TIN_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "tin_ingot", "raw_tin");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.ORES_TIN, new ItemStack(ModItems.TIN_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "tin_ingot", "tin_ores");

        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_TIN, new ItemStack(ModItems.TIN_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "tin_ingot", "tin_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_COPPER, new ItemStack(Items.COPPER_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "copper_ingot", "copper_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_IRON, new ItemStack(Items.IRON_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "iron_ingot", "iron_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_GOLD, new ItemStack(Items.GOLD_INGOT), CookingRecipeCategory.MISC,
                100, .7f, "gold_ingot", "gold_dust");

        addBlastingAndSmeltingRecipes(output, ModItems.IRON_HAMMER, new ItemStack(Items.IRON_NUGGET), CookingRecipeCategory.MISC,
                100, .1f, "iron_nugget", "iron_hammer");
        addBlastingAndSmeltingRecipes(output, ModItems.GOLDEN_HAMMER, new ItemStack(Items.GOLD_NUGGET), CookingRecipeCategory.MISC,
                100, .1f, "gold_nugget", "golden_hammer");

        addBlastingAndSmeltingRecipes(output, ConventionalItemTags.QUARTZ_GEMS, new ItemStack(ModItems.SILICON), CookingRecipeCategory.MISC,
                250, 4.1f, "silicon", "nether_quartz");

        addSmeltingRecipe(output, ModBlocks.SAWDUST_BLOCK_ITEM, new ItemStack(Items.CHARCOAL), CookingRecipeCategory.MISC,
                200, .15f, null, "sawdust_block");

        addSmeltingRecipe(output, ModItems.RAW_GEAR_PRESS_MOLD, new ItemStack(ModItems.GEAR_PRESS_MOLD), CookingRecipeCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_ROD_PRESS_MOLD, new ItemStack(ModItems.ROD_PRESS_MOLD), CookingRecipeCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_WIRE_PRESS_MOLD, new ItemStack(ModItems.WIRE_PRESS_MOLD), CookingRecipeCategory.MISC,
                200, .3f, null);
    }

    private void buildSmithingRecipes(RecipeExporter output) {
        addNetheriteSmithingUpgradeRecipe(output, Ingredient.ofItems(ModItems.DIAMOND_HAMMER),
                new ItemStack(ModItems.NETHERITE_HAMMER));
    }

    private void buildPressMoldMakerRecipes(RecipeExporter output) {
        addPressMoldMakerRecipe(output, 4, new ItemStack(ModItems.RAW_GEAR_PRESS_MOLD));
        addPressMoldMakerRecipe(output, 9, new ItemStack(ModItems.RAW_ROD_PRESS_MOLD));
        addPressMoldMakerRecipe(output, 6, new ItemStack(ModItems.RAW_WIRE_PRESS_MOLD));
    }

    private void buildAlloyFurnaceRecipes(RecipeExporter output) {
        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS)),
                new IngredientWithCount(Ingredient.fromTag(ItemTags.COALS), 3)
        }, new ItemStack(ModItems.STEEL_INGOT), 500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.INGOTS_TIN)),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON)),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(ModItems.REDSTONE_ALLOY_INGOT), 2500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.INGOTS_STEEL), 3),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS), 3),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.INGOTS_TIN), 3)
        }, new ItemStack(ModItems.ADVANCED_ALLOY_INGOT), 10000);
    }

    private void buildCompressorRecipes(RecipeExporter output) {
        addCompressorRecipe(output, Ingredient.ofItems(ModItems.STONE_PEBBLE), new ItemStack(Items.COBBLESTONE),
                16, "stone_pebbles");

        addPlateCompressorRecipes(output, Ingredient.fromTag(CommonItemTags.INGOTS_TIN),
                Ingredient.fromTag(CommonItemTags.STORAGE_BLOCKS_TIN), new ItemStack(ModItems.TIN_PLATE),
                "tin");
        addPlateCompressorRecipes(output, Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                Ingredient.fromTag(ConventionalItemTags.STORAGE_BLOCKS_COPPER), new ItemStack(ModItems.COPPER_PLATE),
                "copper");
        addPlateCompressorRecipes(output, Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS),
                Ingredient.fromTag(ConventionalItemTags.STORAGE_BLOCKS_IRON), new ItemStack(ModItems.IRON_PLATE),
                "iron");
        addPlateCompressorRecipes(output, Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                Ingredient.fromTag(ConventionalItemTags.STORAGE_BLOCKS_GOLD), new ItemStack(ModItems.GOLD_PLATE),
                "gold");

        addPlateCompressorIngotRecipe(output, Ingredient.fromTag(CommonItemTags.INGOTS_ADVANCED_ALLOY),
                new ItemStack(ModItems.ADVANCED_ALLOY_PLATE), "advanced_alloy");
        addPlateCompressorIngotRecipe(output, Ingredient.fromTag(CommonItemTags.INGOTS_ENERGIZED_COPPER),
                new ItemStack(ModItems.ENERGIZED_COPPER_PLATE), "energized_copper");
        addPlateCompressorIngotRecipe(output, Ingredient.fromTag(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                new ItemStack(ModItems.ENERGIZED_GOLD_PLATE), "energized_gold");
    }

    private void buildCrusherRecipes(RecipeExporter output) {
        addCrusherRecipe(output, Ingredient.ofItems(Items.STONE), new ItemStack(Items.COBBLESTONE),
                "stone");
        addCrusherRecipe(output, Ingredient.ofItems(Items.STONE_BRICKS, Items.CHISELED_STONE_BRICKS, Items.CRACKED_STONE_BRICKS,
                        Items.SMOOTH_STONE), new ItemStack(Items.COBBLESTONE),
                "stone_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.MOSSY_STONE_BRICKS), new ItemStack(Items.MOSSY_COBBLESTONE),
                "mossy_stone_bricks");

        addCrusherRecipe(output, Ingredient.ofItems(Items.TUFF_BRICKS, Items.CHISELED_TUFF_BRICKS, Items.CHISELED_TUFF,
                        Items.POLISHED_TUFF), new ItemStack(Items.TUFF),
                "tuff_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate");
        addCrusherRecipe(output, Ingredient.ofItems(Items.DEEPSLATE_BRICKS, Items.CHISELED_DEEPSLATE, Items.CRACKED_DEEPSLATE_BRICKS,
                        Items.DEEPSLATE_TILES, Items.CRACKED_DEEPSLATE_TILES, Items.POLISHED_DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.POLISHED_GRANITE), new ItemStack(Items.GRANITE),
                "polished_granite");
        addCrusherRecipe(output, Ingredient.ofItems(Items.POLISHED_DIORITE), new ItemStack(Items.DIORITE),
                "polished_diorite");
        addCrusherRecipe(output, Ingredient.ofItems(Items.POLISHED_ANDESITE), new ItemStack(Items.ANDESITE),
                "polished_andesite");

        addCrusherRecipe(output, Ingredient.fromTag(CommonItemTags.COBBLESTONES_NORMAL), new ItemStack(Items.GRAVEL),
                "cobblestone");

        addCrusherRecipe(output, Ingredient.fromTag(CommonItemTags.GRAVELS), new ItemStack(Items.SAND),
                "gravel");

        addCrusherRecipe(output, Ingredient.ofItems(Items.SANDSTONE), new ItemStack(Items.SAND),
                "sandstone");
        addCrusherRecipe(output, Ingredient.ofItems(Items.SMOOTH_SANDSTONE, Items.CUT_SANDSTONE,
                        Items.CHISELED_SANDSTONE), new ItemStack(Items.SAND),
                "sandstone_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone");
        addCrusherRecipe(output, Ingredient.ofItems(Items.SMOOTH_RED_SANDSTONE, Items.CUT_RED_SANDSTONE,
                        Items.CHISELED_RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.POLISHED_BLACKSTONE, Items.POLISHED_BLACKSTONE_BRICKS,
                        Items.CHISELED_POLISHED_BLACKSTONE, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                        Items.GILDED_BLACKSTONE), new ItemStack(Items.BLACKSTONE),
                "blackstone_variants");

        addCrusherRecipe(output, Ingredient.ofItems(Items.SMOOTH_BASALT, Items.POLISHED_BASALT), new ItemStack(Items.BASALT),
                "basalt_variants");
    }

    private void buildPulverizerRecipes(RecipeExporter output) {
        addBasicMetalPulverizerRecipes(output,
                Ingredient.fromTag(CommonItemTags.ORES_TIN), Ingredient.fromTag(CommonItemTags.RAW_MATERIALS_TIN),
                Ingredient.fromTag(CommonItemTags.STORAGE_BLOCKS_RAW_TIN), Ingredient.fromTag(CommonItemTags.INGOTS_TIN),
                new ItemStack(ModItems.TIN_DUST), "tin");
        addBasicMetalPulverizerRecipes(output,
                Ingredient.fromTag(CommonItemTags.ORES_IRON), Ingredient.fromTag(ConventionalItemTags.IRON_RAW_MATERIALS),
                Ingredient.fromTag(ConventionalItemTags.STORAGE_BLOCKS_RAW_IRON), Ingredient.fromTag(ConventionalItemTags.IRON_INGOTS),
                new ItemStack(ModItems.IRON_DUST), "iron");
        addBasicMetalPulverizerRecipes(output,
                Ingredient.fromTag(CommonItemTags.ORES_GOLD), Ingredient.fromTag(ConventionalItemTags.GOLD_RAW_MATERIALS),
                Ingredient.fromTag(ConventionalItemTags.STORAGE_BLOCKS_RAW_GOLD), Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                new ItemStack(ModItems.GOLD_DUST), "gold");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.ORES_COPPER),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(ModItems.COPPER_DUST), new double[] {
                        1., 1., 1., 1., .5, .5
                }, new double[] {
                        1., 1., 1., 1., .75, .5, .25
                }),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(ModItems.GOLD_DUST),
                        .1, .2), "copper_ores");
        addRawMetalAndIngotPulverizerRecipes(output,
                Ingredient.fromTag(ConventionalItemTags.COPPER_RAW_MATERIALS),
                Ingredient.fromTag(ConventionalItemTags.STORAGE_BLOCKS_RAW_COPPER), Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                new ItemStack(ModItems.COPPER_DUST), "copper");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.ORES_COAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.COAL), new double[] {
                        1., 1., .25
                }, new double[] {
                        1., 1., .5, .25
                }), "coal_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.ORES_REDSTONE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.REDSTONE), new double[] {
                        1., 1., 1., 1., 1., .67, .33, .33, .17
                }, new double[] {
                        1., 1., 1., 1., 1., .67, .67, .33, .33, .17
                }), "redstone_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.ORES_LAPIS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.LAPIS_LAZULI), new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25, .125
                }, new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .5, .25, .125
                }), "lapis_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.ORES_EMERALD),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.EMERALD), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "emerald_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(CommonItemTags.ORES_DIAMOND),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.DIAMOND), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "diamond_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(ConventionalItemTags.QUARTZ_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.QUARTZ), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "nether_quartz_ores");

        addPulverizerRecipe(output, Ingredient.fromTag(ConventionalItemTags.NETHERITE_SCRAP_ORES),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.NETHERITE_SCRAP), new double[] {
                        1., .125, .125
                }, new double[] {
                        1., .25, .25, .125
                }), "ancient_debris");

        addPulverizerRecipe(output, Ingredient.ofItems(Items.CHARCOAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(ModItems.CHARCOAL_DUST),
                        1., 1.), "charcoal");
    }

    private void buildSawmillRecipes(RecipeExporter output) {
        addBasicWoodSawmillRecipe(output, new ItemStack(Items.OAK_PLANKS),
                Ingredient.fromTag(ItemTags.OAK_LOGS), Ingredient.ofItems(Items.OAK_FENCE),
                Ingredient.ofItems(Items.OAK_FENCE_GATE), Ingredient.ofItems(Items.OAK_DOOR),
                Ingredient.ofItems(Items.OAK_TRAPDOOR), Ingredient.ofItems(Items.OAK_PRESSURE_PLATE),
                Ingredient.ofItems(Items.OAK_SIGN), Ingredient.ofItems(Items.OAK_BOAT), Ingredient.ofItems(Items.OAK_CHEST_BOAT),
                false, "oak");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.SPRUCE_PLANKS),
                Ingredient.fromTag(ItemTags.SPRUCE_LOGS), Ingredient.ofItems(Items.SPRUCE_FENCE),
                Ingredient.ofItems(Items.SPRUCE_FENCE_GATE), Ingredient.ofItems(Items.SPRUCE_DOOR),
                Ingredient.ofItems(Items.SPRUCE_TRAPDOOR), Ingredient.ofItems(Items.SPRUCE_PRESSURE_PLATE),
                Ingredient.ofItems(Items.SPRUCE_SIGN), Ingredient.ofItems(Items.SPRUCE_BOAT), Ingredient.ofItems(Items.SPRUCE_CHEST_BOAT),
                false, "spruce");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.BIRCH_PLANKS),
                Ingredient.fromTag(ItemTags.BIRCH_LOGS), Ingredient.ofItems(Items.BIRCH_FENCE),
                Ingredient.ofItems(Items.BIRCH_FENCE_GATE), Ingredient.ofItems(Items.BIRCH_DOOR),
                Ingredient.ofItems(Items.BIRCH_TRAPDOOR), Ingredient.ofItems(Items.BIRCH_PRESSURE_PLATE),
                Ingredient.ofItems(Items.BIRCH_SIGN), Ingredient.ofItems(Items.BIRCH_BOAT), Ingredient.ofItems(Items.BIRCH_CHEST_BOAT),
                false, "birch");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.JUNGLE_PLANKS),
                Ingredient.fromTag(ItemTags.JUNGLE_LOGS), Ingredient.ofItems(Items.JUNGLE_FENCE),
                Ingredient.ofItems(Items.JUNGLE_FENCE_GATE), Ingredient.ofItems(Items.JUNGLE_DOOR),
                Ingredient.ofItems(Items.JUNGLE_TRAPDOOR), Ingredient.ofItems(Items.JUNGLE_PRESSURE_PLATE),
                Ingredient.ofItems(Items.JUNGLE_SIGN), Ingredient.ofItems(Items.JUNGLE_BOAT), Ingredient.ofItems(Items.JUNGLE_CHEST_BOAT),
                false, "jungle");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.ACACIA_PLANKS),
                Ingredient.fromTag(ItemTags.ACACIA_LOGS), Ingredient.ofItems(Items.ACACIA_FENCE),
                Ingredient.ofItems(Items.ACACIA_FENCE_GATE), Ingredient.ofItems(Items.ACACIA_DOOR),
                Ingredient.ofItems(Items.ACACIA_TRAPDOOR), Ingredient.ofItems(Items.ACACIA_PRESSURE_PLATE),
                Ingredient.ofItems(Items.ACACIA_SIGN), Ingredient.ofItems(Items.ACACIA_BOAT), Ingredient.ofItems(Items.ACACIA_CHEST_BOAT),
                false, "acacia");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.DARK_OAK_PLANKS),
                Ingredient.fromTag(ItemTags.DARK_OAK_LOGS), Ingredient.ofItems(Items.DARK_OAK_FENCE),
                Ingredient.ofItems(Items.DARK_OAK_FENCE_GATE), Ingredient.ofItems(Items.DARK_OAK_DOOR),
                Ingredient.ofItems(Items.DARK_OAK_TRAPDOOR), Ingredient.ofItems(Items.DARK_OAK_PRESSURE_PLATE),
                Ingredient.ofItems(Items.DARK_OAK_SIGN), Ingredient.ofItems(Items.DARK_OAK_BOAT), Ingredient.ofItems(Items.DARK_OAK_CHEST_BOAT),
                false, "dark_oak");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.MANGROVE_PLANKS),
                Ingredient.fromTag(ItemTags.MANGROVE_LOGS), Ingredient.ofItems(Items.MANGROVE_FENCE),
                Ingredient.ofItems(Items.MANGROVE_FENCE_GATE), Ingredient.ofItems(Items.MANGROVE_DOOR),
                Ingredient.ofItems(Items.MANGROVE_TRAPDOOR), Ingredient.ofItems(Items.MANGROVE_PRESSURE_PLATE),
                Ingredient.ofItems(Items.MANGROVE_SIGN), Ingredient.ofItems(Items.MANGROVE_BOAT), Ingredient.ofItems(Items.MANGROVE_CHEST_BOAT),
                false, "mangrove");

        addBasicWoodSawmillRecipe(output, new ItemStack(Items.CHERRY_PLANKS),
                Ingredient.fromTag(ItemTags.CHERRY_LOGS), Ingredient.ofItems(Items.CHERRY_FENCE),
                Ingredient.ofItems(Items.CHERRY_FENCE_GATE), Ingredient.ofItems(Items.CHERRY_DOOR),
                Ingredient.ofItems(Items.CHERRY_TRAPDOOR), Ingredient.ofItems(Items.CHERRY_PRESSURE_PLATE),
                Ingredient.ofItems(Items.CHERRY_SIGN), Ingredient.ofItems(Items.CHERRY_BOAT), Ingredient.ofItems(Items.CHERRY_CHEST_BOAT),
                false, "cherry");

        addSawmillRecipe(output, Ingredient.fromTag(ItemTags.BAMBOO_BLOCKS), new ItemStack(Items.BAMBOO_PLANKS, 3),
                1, "bamboo_planks", "bamboo_blocks");
        addBasicWoodWithoutLogsSawmillRecipe(output, new ItemStack(Items.BAMBOO_PLANKS),
                Ingredient.ofItems(Items.BAMBOO_FENCE), Ingredient.ofItems(Items.BAMBOO_FENCE_GATE), Ingredient.ofItems(Items.BAMBOO_DOOR),
                Ingredient.ofItems(Items.BAMBOO_TRAPDOOR), Ingredient.ofItems(Items.BAMBOO_PRESSURE_PLATE),
                Ingredient.ofItems(Items.BAMBOO_SIGN), Ingredient.ofItems(Items.BAMBOO_RAFT), Ingredient.ofItems(Items.BAMBOO_CHEST_RAFT),
                true, "bamboo");

        addSawmillRecipe(output, Ingredient.fromTag(ItemTags.CRIMSON_STEMS), new ItemStack(Items.CRIMSON_PLANKS, 6),
                1, "crimson_planks", "crimson_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(output, new ItemStack(Items.CRIMSON_PLANKS),
                Ingredient.ofItems(Items.CRIMSON_FENCE), Ingredient.ofItems(Items.CRIMSON_FENCE_GATE), Ingredient.ofItems(Items.CRIMSON_DOOR),
                Ingredient.ofItems(Items.CRIMSON_TRAPDOOR), Ingredient.ofItems(Items.CRIMSON_PRESSURE_PLATE),
                Ingredient.ofItems(Items.CRIMSON_SIGN), "crimson");

        addSawmillRecipe(output, Ingredient.fromTag(ItemTags.WARPED_STEMS), new ItemStack(Items.WARPED_PLANKS, 6),
                1, "warped_planks", "warped_stems");
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(output, new ItemStack(Items.WARPED_PLANKS),
                Ingredient.ofItems(Items.WARPED_FENCE), Ingredient.ofItems(Items.WARPED_FENCE_GATE), Ingredient.ofItems(Items.WARPED_DOOR),
                Ingredient.ofItems(Items.WARPED_TRAPDOOR), Ingredient.ofItems(Items.WARPED_PRESSURE_PLATE),
                Ingredient.ofItems(Items.WARPED_SIGN), "warped");

        addSawmillRecipe(output, Ingredient.ofItems(Items.CRAFTING_TABLE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "crafting_table");
        addSawmillRecipe(output, Ingredient.ofItems(Items.CARTOGRAPHY_TABLE), new ItemStack(Items.OAK_PLANKS, 4),
                new ItemStack(Items.PAPER, 2), "oak_planks", "cartography_table");
        addSawmillRecipe(output, Ingredient.ofItems(Items.FLETCHING_TABLE), new ItemStack(Items.OAK_PLANKS, 4),
                new ItemStack(Items.FLINT, 2), "oak_planks", "fletching_table");
        addSawmillRecipe(output, Ingredient.ofItems(Items.LOOM), new ItemStack(Items.OAK_PLANKS, 2),
                new ItemStack(Items.STRING, 2), "oak_planks", "loom");
        addSawmillRecipe(output, Ingredient.ofItems(Items.COMPOSTER), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "composter");
        addSawmillRecipe(output, Ingredient.ofItems(Items.NOTE_BLOCK), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.REDSTONE), "oak_planks", "note_block");
        addSawmillRecipe(output, Ingredient.ofItems(Items.JUKEBOX), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.DIAMOND), "oak_planks", "jukebox");

        addSawmillRecipe(output, Ingredient.ofItems(Items.BOOKSHELF), new ItemStack(Items.OAK_PLANKS, 6),
                new ItemStack(Items.BOOK, 3), "oak_planks", "bookshelf");
        addSawmillRecipe(output, Ingredient.ofItems(Items.CHISELED_BOOKSHELF), new ItemStack(Items.OAK_PLANKS, 6),
                5, "oak_planks", "chiseled_bookshelf");
        addSawmillRecipe(output, Ingredient.ofItems(Items.LECTERN), new ItemStack(Items.OAK_PLANKS, 8),
                new ItemStack(Items.BOOK, 3), "oak_planks", "lectern");

        addSawmillRecipe(output, Ingredient.ofItems(Items.CHEST), new ItemStack(Items.OAK_PLANKS, 7),
                3, "oak_planks", "chest");
        addSawmillRecipe(output, Ingredient.ofItems(Items.BARREL), new ItemStack(Items.OAK_PLANKS, 6),
                5, "oak_planks", "barrel");

        addSawmillRecipe(output, Ingredient.ofItems(Items.WOODEN_SWORD), new ItemStack(Items.OAK_PLANKS, 2),
                1, "oak_planks", "wooden_sword");
        addSawmillRecipe(output, Ingredient.ofItems(Items.WOODEN_SHOVEL), new ItemStack(Items.OAK_PLANKS),
                2, "oak_planks", "wooden_shovel");
        addSawmillRecipe(output, Ingredient.ofItems(Items.WOODEN_PICKAXE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_pickaxe");
        addSawmillRecipe(output, Ingredient.ofItems(Items.WOODEN_AXE), new ItemStack(Items.OAK_PLANKS, 3),
                2, "oak_planks", "wooden_axe");
        addSawmillRecipe(output, Ingredient.ofItems(Items.WOODEN_HOE), new ItemStack(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hoe");
        addSawmillRecipe(output, Ingredient.ofItems(ModItems.WOODEN_HAMMER), new ItemStack(Items.OAK_PLANKS, 2),
                2, "oak_planks", "wooden_hammer");

        addSawmillRecipe(output, Ingredient.fromTag(ItemTags.PLANKS), new ItemStack(Items.STICK, 3),
                1, "sticks", "planks");
        addSawmillRecipe(output, Ingredient.ofItems(Items.BAMBOO_MOSAIC), new ItemStack(Items.STICK, 3),
                3, "sticks", "bamboo_mosaic");

        addSawmillRecipe(output, Ingredient.ofEntries(Stream.of(new Ingredient.TagEntry(ItemTags.WOODEN_STAIRS),
                        new Ingredient.StackEntry(new ItemStack(Items.BAMBOO_MOSAIC_STAIRS)))),
                new ItemStack(Items.STICK, 3), 1, "sticks", "stairs");
        addSawmillRecipe(output, Ingredient.ofEntries(Stream.of(new Ingredient.TagEntry(ItemTags.WOODEN_SLABS),
                        new Ingredient.StackEntry(new ItemStack(Items.BAMBOO_MOSAIC_SLAB)))),
                new ItemStack(Items.STICK, 1), 1, "sticks", "slabs");
        addSawmillRecipe(output, Ingredient.fromTag(ItemTags.WOODEN_BUTTONS), new ItemStack(Items.STICK, 3),
                1, "sticks", "buttons");

        addSawmillRecipe(output, Ingredient.ofItems(Items.LADDER), new ItemStack(Items.STICK, 2),
                1, "sticks", "ladder");

        addSawmillRecipe(output, Ingredient.ofItems(Items.BOWL), new ItemStack(Items.STICK),
                2, "sticks", "bowl");
        addSawmillRecipe(output, Ingredient.ofItems(Items.BOW), new ItemStack(Items.STICK, 3),
                new ItemStack(Items.STRING, 3), "sticks", "bow");
        addSawmillRecipe(output, Ingredient.ofItems(Items.FISHING_ROD), new ItemStack(Items.STICK, 3),
                new ItemStack(Items.STRING, 2), "sticks", "fishing_rod");

        addSawmillRecipe(output, Ingredient.ofItems(Items.STICK), new ItemStack(ModItems.SAWDUST),
                0, "sawdust", "sticks");
    }

    private void buildPlantGrowthChamberRecipes(RecipeExporter output) {
        addBasicFlowerGrowingRecipe(output, Items.DANDELION, "dandelions");
        addBasicFlowerGrowingRecipe(output, Items.POPPY, "poppies");
        addBasicFlowerGrowingRecipe(output, Items.BLUE_ORCHID, "blue_orchids");
        addBasicFlowerGrowingRecipe(output, Items.ALLIUM, "alliums");
        addBasicFlowerGrowingRecipe(output, Items.AZURE_BLUET, "azure_bluets");
        addBasicFlowerGrowingRecipe(output, Items.RED_TULIP, "red_tulips");
        addBasicFlowerGrowingRecipe(output, Items.ORANGE_TULIP, "orange_tulips");
        addBasicFlowerGrowingRecipe(output, Items.WHITE_TULIP, "white_tulips");
        addBasicFlowerGrowingRecipe(output, Items.PINK_TULIP, "pink_tulips");
        addBasicFlowerGrowingRecipe(output, Items.OXEYE_DAISY, "oxeye_daisies");
        addBasicFlowerGrowingRecipe(output, Items.CORNFLOWER, "cornflowers");
        addBasicFlowerGrowingRecipe(output, Items.LILY_OF_THE_VALLEY, "lily_of_the_valley");

        addBasicFlowerGrowingRecipe(output, Items.SUNFLOWER, "sunflowers");
        addBasicFlowerGrowingRecipe(output, Items.LILAC, "lilacs");
        addBasicFlowerGrowingRecipe(output, Items.ROSE_BUSH, "rose_bushes");
        addBasicFlowerGrowingRecipe(output, Items.PEONY, "peonies");

        addBasicMushroomsGrowingRecipe(output, Items.BROWN_MUSHROOM, "brown_mushrooms");
        addBasicMushroomsGrowingRecipe(output, Items.RED_MUSHROOM, "red_mushrooms");

        addBasicAncientFlowerGrowingRecipe(output, Items.TORCHFLOWER_SEEDS, Items.TORCHFLOWER, "torchflowers");
        addBasicAncientFlowerGrowingRecipe(output, Items.PITCHER_POD, Items.PITCHER_PLANT, "pitcher_plants");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.PINK_PETALS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PINK_PETALS), new double[] {
                        1., 1., 1., .67, .33, .33, .15
                })
        }, 16000, "pink_petals", "pink_petals");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.SWEET_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SWEET_BERRIES), new double[] {
                        1., 1., .33, .17
                })
        }, 16000, "sweet_berries", "sweet_berries");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.GLOW_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.GLOW_BERRIES), new double[] {
                        1., 1., .67, .33, .17, .17
                })
        }, 16000, "glow_berries", "glow_berries");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.WHEAT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT), new double[] {
                        1., .75, .25
                })
        }, 16000, "wheat", "wheat_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.BEETROOT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "beetroots", "beetroot_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.POTATO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.POTATO), new double[] {
                        1., .75, .25, .25
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.POISONOUS_POTATO), new double[] {
                        .125
                })
        }, 16000, "potatoes", "potato");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.CARROT), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.CARROT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "carrots", "carrot");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.MELON_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.MELON_SLICE), new double[] {
                        1., 1., .75, .25, .25
                })
        }, 16000, "melon_slices", "melon_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.PUMPKIN_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PUMPKIN), new double[] {
                        1.
                })
        }, 16000, "pumpkin", "pumpkin_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.SUGAR_CANE), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SUGAR_CANE), new double[] {
                        1., 1., .67, .67, .33, .17, .17
                })
        }, 16000, "sugar_canes", "sugar_cane");
        addPlantGrowthChamberRecipe(output, Ingredient.ofItems(Items.BAMBOO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BAMBOO), new double[] {
                        1., 1., .67, .17
                })
        }, 16000, "bamboo", "bamboo");
    }

    private void buildPlantGrowthChamberFertilizerRecipes(RecipeExporter output) {
        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.ofItems(Items.BONE_MEAL),
                1.5, 3., "bone_meal");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.ofItems(ModItems.BASIC_FERTILIZER),
                2.5, 3.5, "basic_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.ofItems(ModItems.GOOD_FERTILIZER),
                3.5, 5., "good_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.ofItems(ModItems.ADVANCED_FERTILIZER),
                5., 6.5, "advanced_fertilizer");
    }

    private void buildMetalPressRecipes(RecipeExporter output) {
        addGearMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.PLATES_IRON), new ItemStack(ModItems.IRON_GEAR));

        addRodMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.PLATES_IRON), new ItemStack(ModItems.IRON_ROD));

        addWireMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.PLATES_TIN), new ItemStack(ModItems.TIN_WIRE));
        addWireMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.PLATES_COPPER), new ItemStack(ModItems.COPPER_WIRE));
        addWireMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.PLATES_GOLD), new ItemStack(ModItems.GOLD_WIRE));

        addWireMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.PLATES_ENERGIZED_COPPER), new ItemStack(ModItems.ENERGIZED_COPPER_WIRE));
        addWireMetalPressRecipe(output, Ingredient.fromTag(CommonItemTags.PLATES_ENERGIZED_GOLD), new ItemStack(ModItems.ENERGIZED_GOLD_WIRE));
    }

    private void buildHeatGeneratorRecipes(RecipeExporter output) {
        addHeatGeneratorRecipe(output, Fluids.FLOWING_LAVA, 15, "flowing_lava");
        addHeatGeneratorRecipe(output, Fluids.LAVA, 25, "still_lava");
    }

    private void buildThermalGeneratorRecipes(RecipeExporter output) {
        addThermalGeneratorRecipe(output, Fluids.LAVA, 20000, "lava");
    }

    private void buildAssemblingMachineRecipes(RecipeExporter output) {
        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(ModItems.BASIC_SOLAR_CELL), 2),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.INGOTS_ENERGIZED_COPPER), 4),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.INGOTS_TIN), 2),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY), 1)
        }, new ItemStack(ModItems.ADVANCED_SOLAR_CELL));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(ModItems.ADVANCED_SOLAR_CELL), 2),
                new IngredientWithCount(Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX), 4),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON), 2),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(ModItems.REINFORCED_ADVANCED_SOLAR_CELL));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(ModItems.BASIC_CIRCUIT), 4),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.WIRES_ENERGIZED_COPPER), 4),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON), 4),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.REDSTONE_DUSTS), 2)
        }, new ItemStack(ModItems.ADVANCED_CIRCUIT));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(ModItems.ADVANCED_CIRCUIT), 4),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.WIRES_ENERGIZED_GOLD), 6),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON), 6)
        }, new ItemStack(ModItems.PROCESSING_UNIT));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.ofItems(ModItems.PROCESSING_UNIT), 4),
                new IngredientWithCount(Ingredient.ofItems(ModItems.TELEPORTER_MATRIX), 4),
                new IngredientWithCount(Ingredient.ofItems(ModItems.ENERGIZED_CRYSTAL_MATRIX), 2),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.SILICON), 2)
        }, new ItemStack(ModItems.TELEPORTER_PROCESSING_UNIT));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.AMETHYST_GEMS), 6),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.DIAMOND_GEMS), 2),
                new IngredientWithCount(Ingredient.fromTag(ConventionalItemTags.DIAMOND_GEMS), 2),
                new IngredientWithCount(Ingredient.fromTag(CommonItemTags.INGOTS_REDSTONE_ALLOY), 1)
        }, new ItemStack(ModItems.CRYSTAL_MATRIX));
    }

    private void buildStoneSolidifierRecipes(RecipeExporter output) {
        addStoneSolidifierRecipe(output, 1000, 50, new ItemStack(Items.STONE));

        addStoneSolidifierRecipe(output, 50, 50, new ItemStack(Items.COBBLESTONE));

        addStoneSolidifierRecipe(output, 1000, 150, new ItemStack(Items.DEEPSLATE));

        addStoneSolidifierRecipe(output, 150, 150, new ItemStack(Items.COBBLED_DEEPSLATE));

        addStoneSolidifierRecipe(output, 1000, 50, new ItemStack(Items.GRANITE));

        addStoneSolidifierRecipe(output, 1000, 50, new ItemStack(Items.DIORITE));

        addStoneSolidifierRecipe(output, 1000, 50, new ItemStack(Items.ANDESITE));

        addStoneSolidifierRecipe(output, 1000, 250, new ItemStack(Items.BLACKSTONE));

        addStoneSolidifierRecipe(output, 1000, 1000, new ItemStack(Items.OBSIDIAN));
    }

    private void buildFiltrationPlantRecipes(RecipeExporter output) {
        addOreFiltrationRecipe(output, new ItemStack(ModItems.RAW_TIN), 0.05, "tin");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_COPPER), 0.05, "copper");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_IRON), 0.05, "iron");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_GOLD), 0.005, "gold");
    }

    private void buildFluidTransposerRecipes(RecipeExporter output) {
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.WHITE_CONCRETE_POWDER), new ItemStack(Items.WHITE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.ORANGE_CONCRETE_POWDER), new ItemStack(Items.ORANGE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.MAGENTA_CONCRETE_POWDER), new ItemStack(Items.MAGENTA_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.LIGHT_BLUE_CONCRETE_POWDER), new ItemStack(Items.LIGHT_BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.YELLOW_CONCRETE_POWDER), new ItemStack(Items.YELLOW_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.LIME_CONCRETE_POWDER), new ItemStack(Items.LIME_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.PINK_CONCRETE_POWDER), new ItemStack(Items.PINK_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.GRAY_CONCRETE_POWDER), new ItemStack(Items.GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.LIGHT_GRAY_CONCRETE_POWDER), new ItemStack(Items.LIGHT_GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.CYAN_CONCRETE_POWDER), new ItemStack(Items.CYAN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.PURPLE_CONCRETE_POWDER), new ItemStack(Items.PURPLE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.BLUE_CONCRETE_POWDER), new ItemStack(Items.BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.BROWN_CONCRETE_POWDER), new ItemStack(Items.BROWN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.GREEN_CONCRETE_POWDER), new ItemStack(Items.GREEN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.RED_CONCRETE_POWDER), new ItemStack(Items.RED_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.ofItems(Items.BLACK_CONCRETE_POWDER), new ItemStack(Items.BLACK_CONCRETE));

        addFluidTransposerRecipe(output, Ingredient.ofItems(Items.SPONGE), new ItemStack(Items.WET_SPONGE), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));
        addFluidTransposerRecipe(output, Ingredient.ofItems(Items.WET_SPONGE), new ItemStack(Items.SPONGE), FluidTransposerBlockEntity.Mode.EMPTYING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));

        addFluidTransposerRecipe(output, Ingredient.ofItems(Items.DIRT), new ItemStack(Items.MUD), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(250)));
    }

    private void buildChargerRecipes(RecipeExporter output) {
        addChargerRecipe(output, Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                new ItemStack(ModItems.ENERGIZED_COPPER_INGOT), 4194304);
    }

    private void buildEnergizerRecipes(RecipeExporter output) {
        addEnergizerRecipe(output, Ingredient.fromTag(ConventionalItemTags.COPPER_INGOTS),
                new ItemStack(ModItems.ENERGIZED_COPPER_INGOT), 32768);
        addEnergizerRecipe(output, Ingredient.fromTag(ConventionalItemTags.GOLD_INGOTS),
                new ItemStack(ModItems.ENERGIZED_GOLD_INGOT), 131072);
        addEnergizerRecipe(output, Ingredient.ofItems(ModItems.CRYSTAL_MATRIX),
                new ItemStack(ModItems.ENERGIZED_CRYSTAL_MATRIX), 524288);
    }

    private void buildCrystalGrowthChamberRecipes(RecipeExporter output) {
        addCrystalGrowthChamberRecipe(output, Ingredient.fromTag(ConventionalItemTags.AMETHYST_GEMS),
                new OutputItemStackWithPercentages(new ItemStack(Items.AMETHYST_SHARD), new double[] {
                        1., 1., .67, .5, .25, .125
                }), 16000);
        addCrystalGrowthChamberRecipe(output, Ingredient.ofItems(Items.AMETHYST_BLOCK),
                new OutputItemStackWithPercentages(new ItemStack(Items.BUDDING_AMETHYST), .25), 4,
                32000);
    }

    private static void addBlastingAndSmeltingRecipes(RecipeExporter output, ItemConvertible ingredient, ItemStack result,
                                                      CookingRecipeCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }
    private static void addBlastingAndSmeltingRecipes(RecipeExporter output, TagKey<Item> ingredient, ItemStack result,
                                                      CookingRecipeCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addSmeltingRecipe(RecipeExporter output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smelting/" +
                getItemPath(result.getItem()));

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(RecipeExporter output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smelting/" +
                getItemPath(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(RecipeExporter output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, Identifier recipeId) {
        Advancement.Builder advancementBuilder = output.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        AbstractCookingRecipe recipe = new SmeltingRecipe(Objects.requireNonNullElse(group, ""),
                category, Ingredient.ofItems(ingredient), result, xp, time);
        output.accept(recipeId, recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }
    private static void addSmeltingRecipe(RecipeExporter output, TagKey<Item> ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smelting/" +
                getItemPath(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        AbstractCookingRecipe recipe = new SmeltingRecipe(Objects.requireNonNullElse(group, ""),
                category, Ingredient.fromTag(ingredient), result, xp, time);
        output.accept(recipeId, recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }

    private static void addBlastingRecipe(RecipeExporter output, ItemConvertible ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "blasting/" +
                getItemPath(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromItem(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        AbstractCookingRecipe recipe = new BlastingRecipe(Objects.requireNonNullElse(group, ""),
                category, Ingredient.ofItems(ingredient), result, xp, time);
        output.accept(recipeId, recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }
    private static void addBlastingRecipe(RecipeExporter output, TagKey<Item> ingredient, ItemStack result, CookingRecipeCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "blasting/" +
                getItemPath(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        AbstractCookingRecipe recipe = new BlastingRecipe(Objects.requireNonNullElse(group, ""),
                category, Ingredient.fromTag(ingredient), result, xp, time);
        output.accept(recipeId, recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }

    private static void addNetheriteSmithingUpgradeRecipe(RecipeExporter RecipeExporter, Ingredient base, ItemStack output) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "smithing/" +
                getItemPath(output.getItem()));

        Advancement.Builder advancementBuilder = RecipeExporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .criterion("has_the_ingredient", conditionsFromTag(ConventionalItemTags.NETHERITE_INGOTS))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        SmithingTransformRecipe recipe = new SmithingTransformRecipe(Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                base, Ingredient.fromTag(ConventionalItemTags.NETHERITE_INGOTS), output);
        RecipeExporter.accept(recipeId, recipe, advancementBuilder.build(recipeId.withPrefixedPath("recipes/")));
    }

    private static void addAlloyFurnaceRecipe(RecipeExporter RecipeExporter, IngredientWithCount[] inputs, ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(RecipeExporter, inputs, output, OutputItemStackWithPercentages.EMPTY, ticks);
    }
    private static void addAlloyFurnaceRecipe(RecipeExporter RecipeExporter, IngredientWithCount[] inputs, ItemStack output,
                                              OutputItemStackWithPercentages secondaryOutput, int ticks) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "alloy_furnace/" +
                getItemPath(output.getItem()));

        AlloyFurnaceRecipe recipe = new AlloyFurnaceRecipe(output, secondaryOutput, inputs, ticks);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addPressMoldMakerRecipe(RecipeExporter RecipeExporter, int clayCount, ItemStack output) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "press_mold_maker/" +
                getItemPath(output.getItem()));

        PressMoldMakerRecipe recipe = new PressMoldMakerRecipe(output, clayCount);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addPlateCompressorRecipes(RecipeExporter RecipeExporter, Ingredient ingotInput,
                                                  Ingredient blockInput, ItemStack output, String metalName) {
        addPlateCompressorIngotRecipe(RecipeExporter, ingotInput, output, metalName);
        addCompressorRecipe(RecipeExporter, blockInput, output.copyWithCount(9), metalName + "_block");
    }
    private static void addPlateCompressorIngotRecipe(RecipeExporter RecipeExporter, Ingredient ingotInput,
                                                      ItemStack output, String metalName) {
        addCompressorRecipe(RecipeExporter, ingotInput, output, metalName + "_ingot");
    }
    private static void addCompressorRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output, String recipeIngredientName) {
        addCompressorRecipe(RecipeExporter, input, output, 1, recipeIngredientName);
    }
    private static void addCompressorRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output, int inputCount,
                                            String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "compressing/" +
                getItemPath(output.getItem()) + "_from_compressing_" + recipeIngredientName);

        CompressorRecipe recipe = new CompressorRecipe(output, input, inputCount);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addCrusherRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output,
                                         String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "crusher/" +
                getItemPath(output.getItem()) + "_from_crushing_" + recipeIngredientName);

        CrusherRecipe recipe = new CrusherRecipe(output, input);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addBasicMetalPulverizerRecipes(RecipeExporter RecipeExporter, Ingredient oreInput,
                                                       Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                       Ingredient ingotInput, ItemStack output, String metalName) {
        addPulverizerRecipe(RecipeExporter, oreInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., .25
        }, new double[] {
                1., 1, .5, .25
        }), metalName + "_ores");

        addRawMetalAndIngotPulverizerRecipes(RecipeExporter, rawMetalInput, rawMetalBlockInput, ingotInput, output, metalName);
    }
    private static void addRawMetalAndIngotPulverizerRecipes(RecipeExporter RecipeExporter,
                                                             Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                             Ingredient ingotInput, ItemStack output, String metalName) {
        addPulverizerRecipe(RecipeExporter, rawMetalInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., .25
        }, new double[] {
                1., .5
        }), "raw_" + metalName);

        addPulverizerRecipe(RecipeExporter, rawMetalBlockInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25
        }, new double[] {
                1., 1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .25, .25
        }), "raw_" + metalName + "_blocks");

        addPulverizerRecipe(RecipeExporter, ingotInput, new PulverizerRecipe.OutputItemStackWithPercentages(output,
                1., 1.), metalName + "_ingots");
    }
    private static void addPulverizerRecipe(RecipeExporter RecipeExporter, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            String recipeIngredientName) {
        addPulverizerRecipe(RecipeExporter, input, output,
                new PulverizerRecipe.OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0], new double[0]), recipeIngredientName);
    }
    private static void addPulverizerRecipe(RecipeExporter RecipeExporter, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            PulverizerRecipe.OutputItemStackWithPercentages secondaryOutput,
                                            String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "pulverizer/" +
                getItemPath(output.output().getItem()) + "_from_pulverizer_" + recipeIngredientName);

        PulverizerRecipe recipe = new PulverizerRecipe(output, secondaryOutput, input);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addBasicWoodSawmillRecipe(RecipeExporter RecipeExporter, ItemStack planksItem,
                                                  Ingredient logsInput, Ingredient fenceInput, Ingredient fenceGateInput,
                                                  Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                  Ingredient signInput, Ingredient boatInput, Ingredient chestBoatInput,
                                                  boolean isRaft, String woodName) {
        addSawmillRecipe(RecipeExporter, logsInput, planksItem.copyWithCount(6), 1, getItemPath(planksItem.getItem()),
                woodName + "_logs");

        addBasicWoodWithoutLogsSawmillRecipe(RecipeExporter, planksItem, fenceInput, fenceGateInput, doorInput, trapdoorInput,
                pressurePlateInput, signInput, boatInput, chestBoatInput, isRaft, woodName);
    }
    private static void addBasicWoodWithoutLogsSawmillRecipe(RecipeExporter RecipeExporter, ItemStack planksItem,
                                                             Ingredient fenceInput, Ingredient fenceGateInput,
                                                             Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                             Ingredient signInput, Ingredient boatInput, Ingredient chestBoatInput,
                                                             boolean isRaft, String woodName) {
        addBasicWoodWithoutLogsAndBoatsSawmillRecipe(RecipeExporter, planksItem, fenceInput, fenceGateInput, doorInput,
                trapdoorInput, pressurePlateInput, signInput, woodName);

        addSawmillRecipe(RecipeExporter, boatInput, planksItem.copyWithCount(4), 3, getItemPath(planksItem.getItem()),
                woodName + (isRaft?"_raft":"_boat"));
        addSawmillRecipe(RecipeExporter, chestBoatInput, planksItem.copyWithCount(5), 7, getItemPath(planksItem.getItem()),
                woodName + (isRaft?"_chest_raft":"_chest_boat"));
    }
    private static void addBasicWoodWithoutLogsAndBoatsSawmillRecipe(RecipeExporter RecipeExporter, ItemStack planksItem,
                                                                     Ingredient fenceInput, Ingredient fenceGateInput,
                                                                     Ingredient doorInput, Ingredient trapdoorInput, Ingredient pressurePlateInput,
                                                                     Ingredient signInput, String woodName) {
        addSawmillRecipe(RecipeExporter, fenceInput, planksItem, 2, getItemPath(planksItem.getItem()),
                woodName + "_fence");
        addSawmillRecipe(RecipeExporter, fenceGateInput, planksItem.copyWithCount(2), 3, getItemPath(planksItem.getItem()),
                woodName + "_fence_gate");
        addSawmillRecipe(RecipeExporter, doorInput, planksItem, 3, getItemPath(planksItem.getItem()),
                woodName + "_door");
        addSawmillRecipe(RecipeExporter, trapdoorInput, planksItem.copyWithCount(2), 3, getItemPath(planksItem.getItem()),
                woodName + "_trapdoor");
        addSawmillRecipe(RecipeExporter, pressurePlateInput, planksItem, 2, getItemPath(planksItem.getItem()),
                woodName + "_pressure_plate");
        addSawmillRecipe(RecipeExporter, signInput, planksItem.copyWithCount(2), 1, getItemPath(planksItem.getItem()),
                woodName + "_sign");
    }
    private static void addSawmillRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output,
                                         int sawdustAmount, String outputName, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillRecipe recipe = new SawmillRecipe(output, input, sawdustAmount);
        RecipeExporter.accept(recipeId, recipe, null);
    }
    private static void addSawmillRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output,
                                         ItemStack secondaryOutput, String outputName, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "sawmill/" +
                outputName + "_from_sawing_" + recipeIngredientName);

        SawmillRecipe recipe = new SawmillRecipe(output, secondaryOutput, input);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addBasicFlowerGrowingRecipe(RecipeExporter RecipeExporter, ItemConvertible flowerItem,
                                                    String outputName) {
        addPlantGrowthChamberRecipe(RecipeExporter, Ingredient.ofItems(flowerItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., 1., .33
                })
        }, 16000, outputName, getItemPath(flowerItem));
    }
    private static void addBasicMushroomsGrowingRecipe(RecipeExporter RecipeExporter, ItemConvertible mushroomItem,
                                                       String outputName) {
        addPlantGrowthChamberRecipe(RecipeExporter, Ingredient.ofItems(mushroomItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(mushroomItem), new double[] {
                        1., 1., .5, .25
                })
        }, 16000, outputName, getItemPath(mushroomItem));
    }
    private static void addBasicAncientFlowerGrowingRecipe(RecipeExporter RecipeExporter, ItemConvertible seedItem,
                                                           ItemConvertible flowerItem, String outputName) {
        addPlantGrowthChamberRecipe(RecipeExporter, Ingredient.ofItems(seedItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(seedItem), new double[] {
                        1., .33, .15
                }),
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., .15
                })
        }, 16000, outputName, getItemPath(seedItem));
    }
    private static void addPlantGrowthChamberRecipe(RecipeExporter RecipeExporter, Ingredient input,
                                                    OutputItemStackWithPercentages[] outputs, int ticks,
                                                    String outputName, String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "growing/" +
                outputName + "_from_growing_" + recipeIngredientName);

        PlantGrowthChamberRecipe recipe = new PlantGrowthChamberRecipe(outputs, input, ticks);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addPlantGrowthChamberFertilizerRecipe(RecipeExporter RecipeExporter, Ingredient input,
                                                              double speedMultiplier, double energyConsumptionMultiplier,
                                                              String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "growing/fertilizer/" +
                recipeIngredientName);

        PlantGrowthChamberFertilizerRecipe recipe = new PlantGrowthChamberFertilizerRecipe(input, speedMultiplier,
                energyConsumptionMultiplier);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addGearMetalPressRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output) {
        addMetalPressRecipe(RecipeExporter, input, output, new ItemStack(ModItems.GEAR_PRESS_MOLD), 2);
    }
    private static void addRodMetalPressRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output) {
        addMetalPressRecipe(RecipeExporter, input, output.copyWithCount(2), new ItemStack(ModItems.ROD_PRESS_MOLD));
    }
    private static void addWireMetalPressRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output) {
        addMetalPressRecipe(RecipeExporter, input, output.copyWithCount(3), new ItemStack(ModItems.WIRE_PRESS_MOLD));
    }
    private static void addMetalPressRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output,
                                            ItemStack pressMold) {
        addMetalPressRecipe(RecipeExporter, input, output, pressMold, 1);
    }
    private static void addMetalPressRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output,
                                            ItemStack pressMold, int inputCount) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "metal_press/" +
                getItemPath(output.getItem()));

        MetalPressRecipe recipe = new MetalPressRecipe(output, pressMold, input, inputCount);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addHeatGeneratorRecipe(RecipeExporter RecipeExporter, Fluid input, int energyProduction,
                                               String recipeIngredientName) {
        addHeatGeneratorRecipe(RecipeExporter, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private static void addHeatGeneratorRecipe(RecipeExporter RecipeExporter, Fluid[] input, int energyProduction,
                                               String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "heat_generator/" +
                "energy_production_from_" + recipeIngredientName);

        HeatGeneratorRecipe recipe = new HeatGeneratorRecipe(input, energyProduction);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addThermalGeneratorRecipe(RecipeExporter RecipeExporter, Fluid input, int energyProduction,
                                                  String recipeIngredientName) {
        addThermalGeneratorRecipe(RecipeExporter, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private static void addThermalGeneratorRecipe(RecipeExporter RecipeExporter, Fluid[] input, int energyProduction,
                                                  String recipeIngredientName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "thermal_generator/" +
                "energy_production_from_" + recipeIngredientName);

        ThermalGeneratorRecipe recipe = new ThermalGeneratorRecipe(input, energyProduction);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addStoneSolidifierRecipe(RecipeExporter RecipeExporter, int waterAmount, int lavaAmount, ItemStack output) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "stone_solidifier/" +
                getItemPath(output.getItem()));

        StoneSolidifierRecipe recipe = new StoneSolidifierRecipe(output, waterAmount, lavaAmount);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addAssemblingMachineRecipe(RecipeExporter RecipeExporter, IngredientWithCount[] inputs, ItemStack output) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "assembling/" +
                getItemPath(output.getItem()));

        AssemblingMachineRecipe recipe = new AssemblingMachineRecipe(output, inputs);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addOreFiltrationRecipe(RecipeExporter RecipeExporter, ItemStack oreOutput, double oreOutputPercentage,
                                               String oreName) {
        addFiltrationPlantRecipe(RecipeExporter, new OutputItemStackWithPercentages(new ItemStack(ModItems.STONE_PEBBLE), .33),
                new OutputItemStackWithPercentages(oreOutput, oreOutputPercentage), Registries.ITEM.getId(oreOutput.getItem()),
                oreName + "_ore_filtration");
    }
    private static void addFiltrationPlantRecipe(RecipeExporter RecipeExporter, OutputItemStackWithPercentages output,
                                                 Identifier icon, String recipeName) {
        addFiltrationPlantRecipe(RecipeExporter, output, OutputItemStackWithPercentages.EMPTY, icon, recipeName);
    }
    private static void addFiltrationPlantRecipe(RecipeExporter RecipeExporter, OutputItemStackWithPercentages output,
                                                 OutputItemStackWithPercentages secondaryOutput, Identifier icon,
                                                 String recipeName) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "filtration_plant/" +
                recipeName);

        FiltrationPlantRecipe recipe = new FiltrationPlantRecipe(output, secondaryOutput, icon);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addConcreteFluidTransposerRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output) {
        addFluidTransposerRecipe(RecipeExporter, input, output, FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, FluidUtils.convertMilliBucketsToDroplets(1000)));
    }
    private static void addFluidTransposerRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output,
                                                 FluidTransposerBlockEntity.Mode mode, FluidStack fluid) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "fluid_transposer/" +
                getItemPath(output.getItem()));

        FluidTransposerRecipe recipe = new FluidTransposerRecipe(mode, output, input, fluid);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addChargerRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output, int energyConsumption) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "charger/" +
                getItemPath(output.getItem()));

        ChargerRecipe recipe = new ChargerRecipe(output, input, energyConsumption);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addEnergizerRecipe(RecipeExporter RecipeExporter, Ingredient input, ItemStack output, int energyConsumption) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "energizer/" +
                getItemPath(output.getItem()));

        EnergizerRecipe recipe = new EnergizerRecipe(output, input, energyConsumption);
        RecipeExporter.accept(recipeId, recipe, null);
    }

    private static void addCrystalGrowthChamberRecipe(RecipeExporter RecipeExporter, Ingredient input, OutputItemStackWithPercentages output,
                                                      int ticks) {
        addCrystalGrowthChamberRecipe(RecipeExporter, input, output, 1, ticks);
    }
    private static void addCrystalGrowthChamberRecipe(RecipeExporter RecipeExporter, Ingredient input, OutputItemStackWithPercentages output,
                                                      int inputCount, int ticks) {
        Identifier recipeId = Identifier.of(EnergizedPowerMod.MODID, "crystal_growing/" +
                getItemPath(output.output().getItem()));

        CrystalGrowthChamberRecipe recipe = new CrystalGrowthChamberRecipe(output, input, inputCount, ticks);
        RecipeExporter.accept(recipeId, recipe, null);
    }
}
