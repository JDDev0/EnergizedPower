package me.jddev0.ep.datagen;

import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.datagen.recipe.*;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        buildCookingRecipes(output);
        buildSmithingRecipes(output);
        buildPressMoldMakerRecipes(output);
        buildAlloyFurnaceRecipes(output);
        buildCompressorRecipes(output);
        buildCrusherRecipes(output);
        buildPulverizerRecipes(output);
        buildMetalPressRecipes(output);
        buildHeatGeneratorRecipes(output);
        buildThermalGeneratorRecipes(output);
        buildAssemblingMachineRecipes(output);
        buildFiltrationPlantRecipes(output);
        buildFluidTransposerRecipes(output);
        buildChargerRecipes(output);
        buildEnergizerRecipes(output);
        buildCrystalGrowthChamberRecipes(output);
    }

    private void buildCookingRecipes(RecipeOutput output) {
        addBlastingAndSmeltingRecipes(output, CommonItemTags.RAW_MATERIALS_TIN, new ItemStack(ModItems.TIN_INGOT.get()), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "raw_tin");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.ORES_TIN, new ItemStack(ModItems.TIN_INGOT.get()), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "tin_ores");

        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_TIN, new ItemStack(ModItems.TIN_INGOT.get()), CookingBookCategory.MISC,
                100, .7f, "tin_ingot", "tin_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_COPPER, new ItemStack(Items.COPPER_INGOT), CookingBookCategory.MISC,
                100, .7f, "copper_ingot", "copper_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_IRON, new ItemStack(Items.IRON_INGOT), CookingBookCategory.MISC,
                100, .7f, "iron_ingot", "iron_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_GOLD, new ItemStack(Items.GOLD_INGOT), CookingBookCategory.MISC,
                100, .7f, "gold_ingot", "gold_dust");

        addBlastingAndSmeltingRecipes(output, ModItems.IRON_HAMMER.get(), new ItemStack(Items.IRON_NUGGET), CookingBookCategory.MISC,
                100, .1f, "iron_nugget", "iron_hammer");
        addBlastingAndSmeltingRecipes(output, ModItems.GOLDEN_HAMMER.get(), new ItemStack(Items.GOLD_NUGGET), CookingBookCategory.MISC,
                100, .1f, "gold_nugget", "golden_hammer");

        addBlastingAndSmeltingRecipes(output, Tags.Items.GEMS_QUARTZ, new ItemStack(ModItems.SILICON.get()), CookingBookCategory.MISC,
                250, 4.1f, "silicon", "nether_quartz");

        addSmeltingRecipe(output, ModBlocks.SAWDUST_BLOCK_ITEM.get(), new ItemStack(Items.CHARCOAL), CookingBookCategory.MISC,
                200, .15f, null, "sawdust_block");

        addSmeltingRecipe(output, ModItems.RAW_GEAR_PRESS_MOLD.get(), new ItemStack(ModItems.GEAR_PRESS_MOLD.get()), CookingBookCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_ROD_PRESS_MOLD.get(), new ItemStack(ModItems.ROD_PRESS_MOLD.get()), CookingBookCategory.MISC,
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_WIRE_PRESS_MOLD.get(), new ItemStack(ModItems.WIRE_PRESS_MOLD.get()), CookingBookCategory.MISC,
                200, .3f, null);
    }

    private void buildSmithingRecipes(RecipeOutput output) {
        addNetheriteSmithingUpgradeRecipe(output, Ingredient.of(ModItems.DIAMOND_HAMMER),
                new ItemStack(ModItems.NETHERITE_HAMMER.get()));
    }

    private void buildPressMoldMakerRecipes(RecipeOutput output) {
        addPressMoldMakerRecipe(output, 4, new ItemStack(ModItems.RAW_GEAR_PRESS_MOLD.get()));
        addPressMoldMakerRecipe(output, 9, new ItemStack(ModItems.RAW_ROD_PRESS_MOLD.get()));
        addPressMoldMakerRecipe(output, 6, new ItemStack(ModItems.RAW_WIRE_PRESS_MOLD.get()));
    }

    private void buildAlloyFurnaceRecipes(RecipeOutput output) {
        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(Tags.Items.INGOTS_IRON)),
                new IngredientWithCount(Ingredient.of(ItemTags.COALS), 3)
        }, new ItemStack(ModItems.STEEL_INGOT.get()), 500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_TIN)),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON)),
                new IngredientWithCount(Ingredient.of(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(ModItems.REDSTONE_ALLOY_INGOT.get()), 2500);

        addAlloyFurnaceRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_STEEL), 3),
                new IngredientWithCount(Ingredient.of(Tags.Items.INGOTS_COPPER), 3),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_TIN), 3)
        }, new ItemStack(ModItems.ADVANCED_ALLOY_INGOT.get()), 10000);
    }

    private void buildCompressorRecipes(RecipeOutput output) {
        addCompressorRecipe(output, Ingredient.of(ModItems.STONE_PEBBLE), new ItemStack(Items.COBBLESTONE),
                16, "stone_pebbles");

        addPlateCompressorRecipes(output, Ingredient.of(CommonItemTags.INGOTS_TIN),
                Ingredient.of(CommonItemTags.STORAGE_BLOCKS_TIN), new ItemStack(ModItems.TIN_PLATE.get()),
                "tin");
        addPlateCompressorRecipes(output, Ingredient.of(Tags.Items.INGOTS_COPPER),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER), new ItemStack(ModItems.COPPER_PLATE.get()),
                "copper");
        addPlateCompressorRecipes(output, Ingredient.of(Tags.Items.INGOTS_IRON),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON), new ItemStack(ModItems.IRON_PLATE.get()),
                "iron");
        addPlateCompressorRecipes(output, Ingredient.of(Tags.Items.INGOTS_GOLD),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD), new ItemStack(ModItems.GOLD_PLATE.get()),
                "gold");

        addPlateCompressorIngotRecipe(output, Ingredient.of(CommonItemTags.INGOTS_ADVANCED_ALLOY),
                new ItemStack(ModItems.ADVANCED_ALLOY_PLATE.get()), "advanced_alloy");
        addPlateCompressorIngotRecipe(output, Ingredient.of(CommonItemTags.INGOTS_ENERGIZED_COPPER),
                new ItemStack(ModItems.ENERGIZED_COPPER_PLATE.get()), "energized_copper");
        addPlateCompressorIngotRecipe(output, Ingredient.of(CommonItemTags.INGOTS_ENERGIZED_GOLD),
                new ItemStack(ModItems.ENERGIZED_GOLD_PLATE.get()), "energized_gold");
    }

    private void buildCrusherRecipes(RecipeOutput output) {
        addCrusherRecipe(output, Ingredient.of(Items.STONE), new ItemStack(Items.COBBLESTONE),
                "stone");
        addCrusherRecipe(output, Ingredient.of(Items.STONE_BRICKS, Items.CHISELED_STONE_BRICKS, Items.CRACKED_STONE_BRICKS,
                        Items.SMOOTH_STONE), new ItemStack(Items.COBBLESTONE),
                "stone_variants");

        addCrusherRecipe(output, Ingredient.of(Items.MOSSY_STONE_BRICKS), new ItemStack(Items.MOSSY_COBBLESTONE),
                "mossy_stone_bricks");

        addCrusherRecipe(output, Ingredient.of(Items.DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate");
        addCrusherRecipe(output, Ingredient.of(Items.DEEPSLATE_BRICKS, Items.CHISELED_DEEPSLATE, Items.CRACKED_DEEPSLATE_BRICKS,
                        Items.DEEPSLATE_TILES, Items.CRACKED_DEEPSLATE_TILES, Items.POLISHED_DEEPSLATE), new ItemStack(Items.COBBLED_DEEPSLATE),
                "deepslate_variants");

        addCrusherRecipe(output, Ingredient.of(Items.POLISHED_GRANITE), new ItemStack(Items.GRANITE),
                "polished_granite");
        addCrusherRecipe(output, Ingredient.of(Items.POLISHED_DIORITE), new ItemStack(Items.DIORITE),
                "polished_diorite");
        addCrusherRecipe(output, Ingredient.of(Items.POLISHED_ANDESITE), new ItemStack(Items.ANDESITE),
                "polished_andesite");

        addCrusherRecipe(output, Ingredient.of(Tags.Items.COBBLESTONE_NORMAL), new ItemStack(Items.GRAVEL),
                "cobblestone");

        addCrusherRecipe(output, Ingredient.of(Tags.Items.GRAVEL), new ItemStack(Items.SAND),
                "gravel");

        addCrusherRecipe(output, Ingredient.of(Items.SANDSTONE), new ItemStack(Items.SAND),
                "sandstone");
        addCrusherRecipe(output, Ingredient.of(Items.SMOOTH_SANDSTONE, Items.CUT_SANDSTONE,
                        Items.CHISELED_SANDSTONE), new ItemStack(Items.SAND),
                "sandstone_variants");

        addCrusherRecipe(output, Ingredient.of(Items.RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone");
        addCrusherRecipe(output, Ingredient.of(Items.SMOOTH_RED_SANDSTONE, Items.CUT_RED_SANDSTONE,
                        Items.CHISELED_RED_SANDSTONE), new ItemStack(Items.RED_SAND),
                "red_sandstone_variants");

        addCrusherRecipe(output, Ingredient.of(Items.POLISHED_BLACKSTONE, Items.POLISHED_BLACKSTONE_BRICKS,
                        Items.CHISELED_POLISHED_BLACKSTONE, Items.CRACKED_POLISHED_BLACKSTONE_BRICKS,
                        Items.GILDED_BLACKSTONE), new ItemStack(Items.BLACKSTONE),
                "blackstone_variants");

        addCrusherRecipe(output, Ingredient.of(Items.SMOOTH_BASALT, Items.POLISHED_BASALT), new ItemStack(Items.BASALT),
                "basalt_variants");
    }

    private void buildPulverizerRecipes(RecipeOutput output) {
        addBasicMetalPulverizerRecipes(output,
                Ingredient.of(CommonItemTags.ORES_TIN), Ingredient.of(CommonItemTags.RAW_MATERIALS_TIN),
                Ingredient.of(CommonItemTags.STORAGE_BLOCKS_RAW_TIN), Ingredient.of(CommonItemTags.INGOTS_TIN),
                new ItemStack(ModItems.TIN_DUST.get()), "tin");
        addBasicMetalPulverizerRecipes(output,
                Ingredient.of(Tags.Items.ORES_IRON), Ingredient.of(Tags.Items.RAW_MATERIALS_IRON),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_RAW_IRON), Ingredient.of(Tags.Items.INGOTS_IRON),
                new ItemStack(ModItems.IRON_DUST.get()), "iron");
        addBasicMetalPulverizerRecipes(output,
                Ingredient.of(Tags.Items.ORES_GOLD), Ingredient.of(Tags.Items.RAW_MATERIALS_GOLD),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_RAW_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD),
                new ItemStack(ModItems.GOLD_DUST.get()), "gold");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_COPPER),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(ModItems.COPPER_DUST.get()), new double[] {
                        1., 1., 1., 1., .5, .5
                }, new double[] {
                        1., 1., 1., 1., .75, .5, .25
                }),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(ModItems.GOLD_DUST.get()),
                        .1, .2), "copper_ores");
        addRawMetalAndIngotPulverizerRecipes(output,
                Ingredient.of(Tags.Items.RAW_MATERIALS_COPPER),
                Ingredient.of(Tags.Items.STORAGE_BLOCKS_RAW_COPPER), Ingredient.of(Tags.Items.INGOTS_COPPER),
                new ItemStack(ModItems.COPPER_DUST.get()), "copper");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_COAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.COAL), new double[] {
                        1., 1., .25
                }, new double[] {
                        1., 1., .5, .25
                }), "coal_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_REDSTONE),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.REDSTONE), new double[] {
                        1., 1., 1., 1., 1., .67, .33, .33, .17
                }, new double[] {
                        1., 1., 1., 1., 1., .67, .67, .33, .33, .17
                }), "redstone_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_LAPIS),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.LAPIS_LAZULI), new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25, .125
                }, new double[] {
                        1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .5, .25, .125
                }), "lapis_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_EMERALD),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.EMERALD), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "emerald_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_DIAMOND),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.DIAMOND), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "diamond_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_QUARTZ),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.QUARTZ), new double[] {
                        1., .67, .17
                }, new double[] {
                        1., .67, .33, .17
                }), "nether_quartz_ores");

        addPulverizerRecipe(output, Ingredient.of(Tags.Items.ORES_NETHERITE_SCRAP),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(Items.NETHERITE_SCRAP), new double[] {
                        1., .125, .125
                }, new double[] {
                        1., .25, .25, .125
                }), "ancient_debris");

        addPulverizerRecipe(output, Ingredient.of(Items.CHARCOAL),
                new PulverizerRecipe.OutputItemStackWithPercentages(new ItemStack(ModItems.CHARCOAL_DUST.get()),
                        1., 1.), "charcoal");
    }

    private void buildMetalPressRecipes(RecipeOutput output) {
        addGearMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_IRON), new ItemStack(ModItems.IRON_GEAR.get()));

        addRodMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_IRON), new ItemStack(ModItems.IRON_ROD.get()));

        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_TIN), new ItemStack(ModItems.TIN_WIRE.get()));
        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_COPPER), new ItemStack(ModItems.COPPER_WIRE.get()));
        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_GOLD), new ItemStack(ModItems.GOLD_WIRE.get()));

        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER), new ItemStack(ModItems.ENERGIZED_COPPER_WIRE.get()));
        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD), new ItemStack(ModItems.ENERGIZED_GOLD_WIRE.get()));
    }

    private void buildHeatGeneratorRecipes(RecipeOutput output) {
        addHeatGeneratorRecipe(output, Fluids.FLOWING_LAVA, 15, "flowing_lava");
        addHeatGeneratorRecipe(output, Fluids.LAVA, 25, "still_lava");
    }

    private void buildThermalGeneratorRecipes(RecipeOutput output) {
        addThermalGeneratorRecipe(output, Fluids.LAVA, 20000, "lava");
    }

    private void buildAssemblingMachineRecipes(RecipeOutput output) {
        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(ModItems.BASIC_SOLAR_CELL), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_ENERGIZED_COPPER), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_TIN), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY), 1)
        }, new ItemStack(ModItems.ADVANCED_SOLAR_CELL.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(ModItems.ADVANCED_SOLAR_CELL), 2),
                new IngredientWithCount(Ingredient.of(ModItems.ENERGIZED_CRYSTAL_MATRIX), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 2),
                new IngredientWithCount(Ingredient.of(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(ModItems.REINFORCED_ADVANCED_SOLAR_CELL.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(ModItems.BASIC_CIRCUIT), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.WIRES_ENERGIZED_COPPER), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 4),
                new IngredientWithCount(Ingredient.of(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(ModItems.ADVANCED_CIRCUIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(ModItems.ADVANCED_CIRCUIT), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.WIRES_ENERGIZED_GOLD), 6),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 6)
        }, new ItemStack(ModItems.PROCESSING_UNIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(ModItems.PROCESSING_UNIT), 4),
                new IngredientWithCount(Ingredient.of(ModItems.TELEPORTER_MATRIX), 4),
                new IngredientWithCount(Ingredient.of(ModItems.ENERGIZED_CRYSTAL_MATRIX), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 2)
        }, new ItemStack(ModItems.TELEPORTER_PROCESSING_UNIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(Tags.Items.GEMS_AMETHYST), 6),
                new IngredientWithCount(Ingredient.of(Tags.Items.GEMS_DIAMOND), 2),
                new IngredientWithCount(Ingredient.of(Tags.Items.GEMS_EMERALD), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY))
        }, new ItemStack(ModItems.CRYSTAL_MATRIX.get()));
    }

    private void buildFiltrationPlantRecipes(RecipeOutput output) {
        addOreFiltrationRecipe(output, new ItemStack(ModItems.RAW_TIN.get()), 0.05, "tin");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_COPPER), 0.05, "copper");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_IRON), 0.05, "iron");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_GOLD), 0.005, "gold");
    }

    private void buildFluidTransposerRecipes(RecipeOutput output) {
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.WHITE_CONCRETE_POWDER), new ItemStack(Items.WHITE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.ORANGE_CONCRETE_POWDER), new ItemStack(Items.ORANGE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.MAGENTA_CONCRETE_POWDER), new ItemStack(Items.MAGENTA_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.LIGHT_BLUE_CONCRETE_POWDER), new ItemStack(Items.LIGHT_BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.YELLOW_CONCRETE_POWDER), new ItemStack(Items.YELLOW_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.LIME_CONCRETE_POWDER), new ItemStack(Items.LIME_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.PINK_CONCRETE_POWDER), new ItemStack(Items.PINK_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.GRAY_CONCRETE_POWDER), new ItemStack(Items.GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.LIGHT_GRAY_CONCRETE_POWDER), new ItemStack(Items.LIGHT_GRAY_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.CYAN_CONCRETE_POWDER), new ItemStack(Items.CYAN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.PURPLE_CONCRETE_POWDER), new ItemStack(Items.PURPLE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.BLUE_CONCRETE_POWDER), new ItemStack(Items.BLUE_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.BROWN_CONCRETE_POWDER), new ItemStack(Items.BROWN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.GREEN_CONCRETE_POWDER), new ItemStack(Items.GREEN_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.RED_CONCRETE_POWDER), new ItemStack(Items.RED_CONCRETE));
        addConcreteFluidTransposerRecipe(output, Ingredient.of(Items.BLACK_CONCRETE_POWDER), new ItemStack(Items.BLACK_CONCRETE));

        addFluidTransposerRecipe(output, Ingredient.of(Items.SPONGE), new ItemStack(Items.WET_SPONGE), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, 1000));
        addFluidTransposerRecipe(output, Ingredient.of(Items.WET_SPONGE), new ItemStack(Items.SPONGE), FluidTransposerBlockEntity.Mode.EMPTYING,
                new FluidStack(Fluids.WATER, 1000));

        addFluidTransposerRecipe(output, Ingredient.of(Items.DIRT), new ItemStack(Items.MUD), FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, 250));
    }

    private void buildChargerRecipes(RecipeOutput output) {
        addChargerRecipe(output, Ingredient.of(Tags.Items.INGOTS_COPPER),
                new ItemStack(ModItems.ENERGIZED_COPPER_INGOT.get()), 4194304);
    }

    private void buildEnergizerRecipes(RecipeOutput output) {
        addEnergizerRecipe(output, Ingredient.of(Tags.Items.INGOTS_COPPER),
                new ItemStack(ModItems.ENERGIZED_COPPER_INGOT.get()), 32768);
        addEnergizerRecipe(output, Ingredient.of(Tags.Items.INGOTS_GOLD),
                new ItemStack(ModItems.ENERGIZED_GOLD_INGOT.get()), 131072);
        addEnergizerRecipe(output, Ingredient.of(ModItems.CRYSTAL_MATRIX),
                new ItemStack(ModItems.ENERGIZED_CRYSTAL_MATRIX.get()), 524288);
    }

    private void buildCrystalGrowthChamberRecipes(RecipeOutput output) {
        addCrystalGrowthChamberRecipe(output, Ingredient.of(Tags.Items.GEMS_AMETHYST),
                new OutputItemStackWithPercentages(new ItemStack(Items.AMETHYST_SHARD), new double[] {
                    1., 1., .67, .5, .25, .125
                }), 16000);
        addCrystalGrowthChamberRecipe(output, Ingredient.of(Items.AMETHYST_BLOCK),
                new OutputItemStackWithPercentages(new ItemStack(Items.BUDDING_AMETHYST), .25), 4,
                32000);
    }

    private static void addBlastingAndSmeltingRecipes(RecipeOutput output, ItemLike ingredient, ItemStack result,
                                                      CookingBookCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }
    private static void addBlastingAndSmeltingRecipes(RecipeOutput output, TagKey<Item> ingredient, ItemStack result,
                                                      CookingBookCategory category, int time, float xp, String group,
                                                      String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, category, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, category, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addSmeltingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()));

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(output, ingredient, result, category, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, ResourceLocation recipeId) {
        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefix("recipes/")),
                RecipeSerializer.SMELTING_RECIPE
        );
        output.accept(recipe);
    }
    private static void addSmeltingRecipe(RecipeOutput output, TagKey<Item> ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefix("recipes/")),
                RecipeSerializer.SMELTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(RecipeOutput output, ItemLike ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefix("recipes/")),
                RecipeSerializer.BLASTING_RECIPE
        );
        output.accept(recipe);
    }
    private static void addBlastingRecipe(RecipeOutput output, TagKey<Item> ingredient, ItemStack result, CookingBookCategory category,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""), category,
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder.build(recipeId.withPrefix("recipes/")),
                RecipeSerializer.BLASTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addNetheriteSmithingUpgradeRecipe(RecipeOutput recipeOutput, Ingredient base, ItemStack output) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smithing/" +
                getItemName(output.getItem()));

        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(Tags.Items.INGOTS_NETHERITE))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(AdvancementRequirements.Strategy.OR);
        SmithingTransformFinishedRecipe recipe = new SmithingTransformFinishedRecipe(
                recipeId,
                Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                base,
                Ingredient.of(Items.NETHERITE_INGOT),
                output.getItem(),
                advancementBuilder.build(recipeId.withPrefix("recipes/"))
        );
        recipeOutput.accept(recipe);
    }

    private static void addAlloyFurnaceRecipe(RecipeOutput recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(recipeOutput, inputs, output,
                new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]), ticks);
    }
    private static void addAlloyFurnaceRecipe(RecipeOutput recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              OutputItemStackWithPercentages secondaryOutput, int ticks) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "alloy_furnace/" +
                getItemName(output.getItem()));

        AlloyFurnaceFinishedRecipe recipe = new AlloyFurnaceFinishedRecipe(
                recipeId,
                output, secondaryOutput, inputs, ticks
        );
        recipeOutput.accept(recipe);
    }

    private static void addPressMoldMakerRecipe(RecipeOutput recipeOutput, int clayCount, ItemStack output) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "press_mold_maker/" +
                getItemName(output.getItem()));

        PressMoldMakerFinishedRecipe recipe = new PressMoldMakerFinishedRecipe(
                recipeId,
                output, clayCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addPlateCompressorRecipes(RecipeOutput recipeOutput, Ingredient ingotInput,
                                                  Ingredient blockInput, ItemStack output, String metalName) {
        addPlateCompressorIngotRecipe(recipeOutput, ingotInput, output, metalName);
        addCompressorRecipe(recipeOutput, blockInput, output.copyWithCount(9), metalName + "_block");
    }
    private static void addPlateCompressorIngotRecipe(RecipeOutput recipeOutput, Ingredient ingotInput,
                                                   ItemStack output, String metalName) {
        addCompressorRecipe(recipeOutput, ingotInput, output, metalName + "_ingot");
    }
    private static void addCompressorRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output, String recipeIngredientName) {
        addCompressorRecipe(recipeOutput, input, output, 1, recipeIngredientName);
    }
    private static void addCompressorRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output, int inputCount,
                                            String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "compressing/" +
                getItemName(output.getItem()) + "_from_compressing_" + recipeIngredientName);

        CompressorFinishedRecipe recipe = new CompressorFinishedRecipe(
                recipeId,
                output, input, inputCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addCrusherRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output,
                                         String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "crusher/" +
                getItemName(output.getItem()) + "_from_crushing_" + recipeIngredientName);

        CrusherFinishedRecipe recipe = new CrusherFinishedRecipe(
                recipeId,
                output, input
        );
        recipeOutput.accept(recipe);
    }

    private static void addBasicMetalPulverizerRecipes(RecipeOutput recipeOutput, Ingredient oreInput,
                                                       Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                       Ingredient ingotInput, ItemStack output, String metalName) {
        addPulverizerRecipe(recipeOutput, oreInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., .25
        }, new double[] {
                1., 1, .5, .25
        }), metalName + "_ores");

        addRawMetalAndIngotPulverizerRecipes(recipeOutput, rawMetalInput, rawMetalBlockInput, ingotInput, output, metalName);
    }
    private static void addRawMetalAndIngotPulverizerRecipes(RecipeOutput recipeOutput,
                                                             Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                             Ingredient ingotInput, ItemStack output, String metalName) {
        addPulverizerRecipe(recipeOutput, rawMetalInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., .25
        }, new double[] {
                1., .5
        }), "raw_" + metalName);

        addPulverizerRecipe(recipeOutput, rawMetalBlockInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., 1., 1., 1., 1., 1., 1., 1., .5, .5, .25
        }, new double[] {
                1., 1., 1., 1., 1., 1., 1., 1., 1., .75, .5, .25, .25
        }), "raw_" + metalName + "_blocks");

        addPulverizerRecipe(recipeOutput, ingotInput, new PulverizerRecipe.OutputItemStackWithPercentages(output,
                1., 1.), metalName + "_ingots");
    }
    private static void addPulverizerRecipe(RecipeOutput recipeOutput, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            String recipeIngredientName) {
        addPulverizerRecipe(recipeOutput, input, output,
                new PulverizerRecipe.OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0], new double[0]), recipeIngredientName);
    }
    private static void addPulverizerRecipe(RecipeOutput recipeOutput, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            PulverizerRecipe.OutputItemStackWithPercentages secondaryOutput,
                                            String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "pulverizer/" +
                getItemName(output.output().getItem()) + "_from_pulverizer_" + recipeIngredientName);

        PulverizerFinishedRecipe recipe = new PulverizerFinishedRecipe(
                recipeId,
                output, secondaryOutput, input
        );
        recipeOutput.accept(recipe);
    }

    private static void addGearMetalPressRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output, new ItemStack(ModItems.GEAR_PRESS_MOLD.get()), 2);
    }
    private static void addRodMetalPressRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output.copyWithCount(2), new ItemStack(ModItems.ROD_PRESS_MOLD.get()));
    }
    private static void addWireMetalPressRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output.copyWithCount(3), new ItemStack(ModItems.WIRE_PRESS_MOLD.get()));
    }
    private static void addMetalPressRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold) {
        addMetalPressRecipe(recipeOutput, input, output, pressMold, 1);
    }
    private static void addMetalPressRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold, int inputCount) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "metal_press/" +
                getItemName(output.getItem()));

        MetalPressFinishedRecipe recipe = new MetalPressFinishedRecipe(
                recipeId,
                output, pressMold, input, inputCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addHeatGeneratorRecipe(RecipeOutput recipeOutput, Fluid input, int energyProduction,
                                               String recipeIngredientName) {
        addHeatGeneratorRecipe(recipeOutput, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private static void addHeatGeneratorRecipe(RecipeOutput recipeOutput, Fluid[] input, int energyProduction,
                                               String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "heat_generator/" +
                "energy_production_from_" + recipeIngredientName);

        HeatGeneratorFinishedRecipe recipe = new HeatGeneratorFinishedRecipe(
                recipeId,
                input, energyProduction
        );
        recipeOutput.accept(recipe);
    }

    private static void addThermalGeneratorRecipe(RecipeOutput recipeOutput, Fluid input, int energyProduction,
                                                  String recipeIngredientName) {
        addThermalGeneratorRecipe(recipeOutput, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private static void addThermalGeneratorRecipe(RecipeOutput recipeOutput, Fluid[] input, int energyProduction,
                                                  String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "thermal_generator/" +
                "energy_production_from_" + recipeIngredientName);

        ThermalGeneratorFinishedRecipe recipe = new ThermalGeneratorFinishedRecipe(
                recipeId,
                input, energyProduction
        );
        recipeOutput.accept(recipe);
    }

    private static void addAssemblingMachineRecipe(RecipeOutput recipeOutput, IngredientWithCount[] inputs, ItemStack output) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "assembling/" +
                getItemName(output.getItem()));

        AssemblingMachineFinishedRecipe recipe = new AssemblingMachineFinishedRecipe(
                recipeId,
                output, inputs
        );
        recipeOutput.accept(recipe);
    }

    private static void addOreFiltrationRecipe(RecipeOutput recipeOutput, ItemStack oreOutput, double oreOutputPercentage,
                                               String oreName) {
        addFiltrationPlantRecipe(recipeOutput, new OutputItemStackWithPercentages(new ItemStack(ModItems.STONE_PEBBLE.get()), .33),
                new OutputItemStackWithPercentages(oreOutput, oreOutputPercentage), BuiltInRegistries.ITEM.getKey(oreOutput.getItem()),
                oreName + "_ore_filtration");
    }
    private static void addFiltrationPlantRecipe(RecipeOutput recipeOutput, OutputItemStackWithPercentages output,
                                                 ResourceLocation icon, String recipeName) {
        addFiltrationPlantRecipe(recipeOutput, output, OutputItemStackWithPercentages.EMPTY, icon, recipeName);
    }
    private static void addFiltrationPlantRecipe(RecipeOutput recipeOutput, OutputItemStackWithPercentages output,
                                                 OutputItemStackWithPercentages secondaryOutput, ResourceLocation icon,
                                                 String recipeName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "filtration_plant/" +
                recipeName);

        FiltrationPlantFinishedRecipe recipe = new FiltrationPlantFinishedRecipe(
                recipeId,
                output, secondaryOutput, icon
        );
        recipeOutput.accept(recipe);
    }

    private static void addConcreteFluidTransposerRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output) {
        addFluidTransposerRecipe(recipeOutput, input, output, FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, 1000));
    }
    private static void addFluidTransposerRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output,
                                                 FluidTransposerBlockEntity.Mode mode, FluidStack fluid) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "fluid_transposer/" +
                getItemName(output.getItem()));

        FluidTransposerFinishedRecipe recipe = new FluidTransposerFinishedRecipe(
                recipeId,
                mode, output, input, fluid
        );
        recipeOutput.accept(recipe);
    }

    private static void addChargerRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "charger/" +
                getItemName(output.getItem()));

        ChargerFinishedRecipe recipe = new ChargerFinishedRecipe(
                recipeId,
                output, input, energyConsumption
        );
        recipeOutput.accept(recipe);
    }

    private static void addEnergizerRecipe(RecipeOutput recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "energizer/" +
                getItemName(output.getItem()));

        EnergizerFinishedRecipe recipe = new EnergizerFinishedRecipe(
                recipeId,
                output, input, energyConsumption
        );
        recipeOutput.accept(recipe);
    }

    private static void addCrystalGrowthChamberRecipe(RecipeOutput recipeOutput, Ingredient input, OutputItemStackWithPercentages output,
                                                      int ticks) {
        addCrystalGrowthChamberRecipe(recipeOutput, input, output, 1, ticks);
    }
    private static void addCrystalGrowthChamberRecipe(RecipeOutput recipeOutput, Ingredient input, OutputItemStackWithPercentages output,
                                                      int inputCount, int ticks) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "crystal_growing/" +
                getItemName(output.output().getItem()));

        CrystalGrowthChamberFinishedRecipe recipe = new CrystalGrowthChamberFinishedRecipe(
                recipeId,
                output, input, inputCount, ticks
        );
        recipeOutput.accept(recipe);
    }
}
