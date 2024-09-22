package me.jddev0.ep.datagen;

import me.jddev0.ep.util.ItemStackUtils;
import net.minecraft.data.DataGenerator;
import me.jddev0.ep.EnergizedPowerMod;
import me.jddev0.ep.block.ModBlocks;
import me.jddev0.ep.block.entity.FluidTransposerBlockEntity;
import me.jddev0.ep.datagen.recipe.*;
import me.jddev0.ep.item.ModItems;
import me.jddev0.ep.recipe.*;
import me.jddev0.ep.registry.tags.CommonItemTags;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(DataGenerator output) {
        super(output);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> output) {
        buildCookingRecipes(output);
        buildSmithingRecipes(output);
        buildPressMoldMakerRecipes(output);
        buildAlloyFurnaceRecipes(output);
        buildCompressorRecipes(output);
        buildCrusherRecipes(output);
        buildPulverizerRecipes(output);
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

    private void buildCookingRecipes(Consumer<FinishedRecipe> output) {
        addBlastingAndSmeltingRecipes(output, CommonItemTags.RAW_MATERIALS_TIN, new ItemStack(ModItems.TIN_INGOT.get()),
                100, .7f, "tin_ingot", "raw_tin");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.ORES_TIN, new ItemStack(ModItems.TIN_INGOT.get()),
                100, .7f, "tin_ingot", "tin_ores");

        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_TIN, new ItemStack(ModItems.TIN_INGOT.get()),
                100, .7f, "tin_ingot", "tin_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_COPPER, new ItemStack(Items.COPPER_INGOT),
                100, .7f, "copper_ingot", "copper_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_IRON, new ItemStack(Items.IRON_INGOT),
                100, .7f, "iron_ingot", "iron_dust");
        addBlastingAndSmeltingRecipes(output, CommonItemTags.DUSTS_GOLD, new ItemStack(Items.GOLD_INGOT),
                100, .7f, "gold_ingot", "gold_dust");

        addBlastingAndSmeltingRecipes(output, ModItems.IRON_HAMMER.get(), new ItemStack(Items.IRON_NUGGET),
                100, .1f, "iron_nugget", "iron_hammer");
        addBlastingAndSmeltingRecipes(output, ModItems.GOLDEN_HAMMER.get(), new ItemStack(Items.GOLD_NUGGET),
                100, .1f, "gold_nugget", "golden_hammer");

        addBlastingAndSmeltingRecipes(output, Tags.Items.GEMS_QUARTZ, new ItemStack(ModItems.SILICON.get()),
                250, 4.1f, "silicon", "nether_quartz");

        addSmeltingRecipe(output, ModBlocks.SAWDUST_BLOCK_ITEM.get(), new ItemStack(Items.CHARCOAL),
                200, .15f, null, "sawdust_block");

        addSmeltingRecipe(output, ModItems.RAW_GEAR_PRESS_MOLD.get(), new ItemStack(ModItems.GEAR_PRESS_MOLD.get()),
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_ROD_PRESS_MOLD.get(), new ItemStack(ModItems.ROD_PRESS_MOLD.get()),
                200, .3f, null);
        addSmeltingRecipe(output, ModItems.RAW_WIRE_PRESS_MOLD.get(), new ItemStack(ModItems.WIRE_PRESS_MOLD.get()),
                200, .3f, null);
    }

    private void buildSmithingRecipes(Consumer<FinishedRecipe> output) {
        addNetheriteSmithingUpgradeRecipe(output, Ingredient.of(ModItems.DIAMOND_HAMMER.get()),
                new ItemStack(ModItems.NETHERITE_HAMMER.get()));
    }

    private void buildPressMoldMakerRecipes(Consumer<FinishedRecipe> output) {
        addPressMoldMakerRecipe(output, 4, new ItemStack(ModItems.RAW_GEAR_PRESS_MOLD.get()));
        addPressMoldMakerRecipe(output, 9, new ItemStack(ModItems.RAW_ROD_PRESS_MOLD.get()));
        addPressMoldMakerRecipe(output, 6, new ItemStack(ModItems.RAW_WIRE_PRESS_MOLD.get()));
    }

    private void buildAlloyFurnaceRecipes(Consumer<FinishedRecipe> output) {
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

    private void buildCompressorRecipes(Consumer<FinishedRecipe> output) {
        addCompressorRecipe(output, Ingredient.of(ModItems.STONE_PEBBLE.get()), new ItemStack(Items.COBBLESTONE),
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

    private void buildCrusherRecipes(Consumer<FinishedRecipe> output) {
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

    private void buildPulverizerRecipes(Consumer<FinishedRecipe> output) {
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

    private void buildPlantGrowthChamberRecipes(Consumer<FinishedRecipe> output) {
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

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.SWEET_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SWEET_BERRIES), new double[] {
                        1., 1., .33, .17
                })
        }, 16000, "sweet_berries", "sweet_berries");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.GLOW_BERRIES), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.GLOW_BERRIES), new double[] {
                        1., 1., .67, .33, .17, .17
                })
        }, 16000, "glow_berries", "glow_berries");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.WHEAT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.WHEAT), new double[] {
                        1., .75, .25
                })
        }, 16000, "wheat", "wheat_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.BEETROOT_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT_SEEDS), new double[] {
                        1., .33, .33
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.BEETROOT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "beetroots", "beetroot_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.POTATO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.POTATO), new double[] {
                        1., .75, .25, .25
                }),
                new OutputItemStackWithPercentages(new ItemStack(Items.POISONOUS_POTATO), new double[] {
                        .125
                })
        }, 16000, "potatoes", "potato");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.CARROT), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.CARROT), new double[] {
                        1., .75, .25, .25
                })
        }, 16000, "carrots", "carrot");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.MELON_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.MELON_SLICE), new double[] {
                        1., 1., .75, .25, .25
                })
        }, 16000, "melon_slices", "melon_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.PUMPKIN_SEEDS), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.PUMPKIN), new double[] {
                        1.
                })
        }, 16000, "pumpkin", "pumpkin_seeds");

        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.SUGAR_CANE), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.SUGAR_CANE), new double[] {
                        1., 1., .67, .67, .33, .17, .17
                })
        }, 16000, "sugar_canes", "sugar_cane");
        addPlantGrowthChamberRecipe(output, Ingredient.of(Items.BAMBOO), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(Items.BAMBOO), new double[] {
                        1., 1., .67, .17
                })
        }, 16000, "bamboo", "bamboo");
    }

    private void buildPlantGrowthChamberFertilizerRecipes(Consumer<FinishedRecipe> output) {
        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.of(Items.BONE_MEAL),
                1.5, 3., "bone_meal");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.of(ModItems.BASIC_FERTILIZER.get()),
                2.5, 3.5, "basic_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.of(ModItems.GOOD_FERTILIZER.get()),
                3.5, 5., "good_fertilizer");

        addPlantGrowthChamberFertilizerRecipe(output, Ingredient.of(ModItems.ADVANCED_FERTILIZER.get()),
                5., 6.5, "advanced_fertilizer");
    }

    private void buildMetalPressRecipes(Consumer<FinishedRecipe> output) {
        addGearMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_IRON), new ItemStack(ModItems.IRON_GEAR.get()));

        addRodMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_IRON), new ItemStack(ModItems.IRON_ROD.get()));

        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_TIN), new ItemStack(ModItems.TIN_WIRE.get()));
        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_COPPER), new ItemStack(ModItems.COPPER_WIRE.get()));
        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_GOLD), new ItemStack(ModItems.GOLD_WIRE.get()));

        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_ENERGIZED_COPPER), new ItemStack(ModItems.ENERGIZED_COPPER_WIRE.get()));
        addWireMetalPressRecipe(output, Ingredient.of(CommonItemTags.PLATES_ENERGIZED_GOLD), new ItemStack(ModItems.ENERGIZED_GOLD_WIRE.get()));
    }

    private void buildHeatGeneratorRecipes(Consumer<FinishedRecipe> output) {
        addHeatGeneratorRecipe(output, Fluids.FLOWING_LAVA, 15, "flowing_lava");
        addHeatGeneratorRecipe(output, Fluids.LAVA, 25, "still_lava");
    }

    private void buildThermalGeneratorRecipes(Consumer<FinishedRecipe> output) {
        addThermalGeneratorRecipe(output, Fluids.LAVA, 20000, "lava");
    }

    private void buildAssemblingMachineRecipes(Consumer<FinishedRecipe> output) {
        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(ModItems.BASIC_SOLAR_CELL.get()), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_ENERGIZED_COPPER), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_TIN), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY), 1)
        }, new ItemStack(ModItems.ADVANCED_SOLAR_CELL.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(ModItems.ADVANCED_SOLAR_CELL.get()), 2),
                new IngredientWithCount(Ingredient.of(ModItems.ENERGIZED_CRYSTAL_MATRIX.get()), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 2),
                new IngredientWithCount(Ingredient.of(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(ModItems.REINFORCED_ADVANCED_SOLAR_CELL.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(ModItems.BASIC_CIRCUIT.get()), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.WIRES_ENERGIZED_COPPER), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 4),
                new IngredientWithCount(Ingredient.of(Tags.Items.DUSTS_REDSTONE), 2)
        }, new ItemStack(ModItems.ADVANCED_CIRCUIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(ModItems.ADVANCED_CIRCUIT.get()), 4),
                new IngredientWithCount(Ingredient.of(CommonItemTags.WIRES_ENERGIZED_GOLD), 6),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 6)
        }, new ItemStack(ModItems.PROCESSING_UNIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(ModItems.PROCESSING_UNIT.get()), 4),
                new IngredientWithCount(Ingredient.of(ModItems.TELEPORTER_MATRIX.get()), 4),
                new IngredientWithCount(Ingredient.of(ModItems.ENERGIZED_CRYSTAL_MATRIX.get()), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.SILICON), 2)
        }, new ItemStack(ModItems.TELEPORTER_PROCESSING_UNIT.get()));

        addAssemblingMachineRecipe(output, new IngredientWithCount[] {
                new IngredientWithCount(Ingredient.of(Tags.Items.GEMS_AMETHYST), 6),
                new IngredientWithCount(Ingredient.of(Tags.Items.GEMS_DIAMOND), 2),
                new IngredientWithCount(Ingredient.of(Tags.Items.GEMS_EMERALD), 2),
                new IngredientWithCount(Ingredient.of(CommonItemTags.INGOTS_REDSTONE_ALLOY))
        }, new ItemStack(ModItems.CRYSTAL_MATRIX.get()));
    }

    private void buildStoneSolidifierRecipes(Consumer<FinishedRecipe> output) {
        addStoneSolidierRecipe(output, 1000, 50, new ItemStack(Items.STONE));

        addStoneSolidierRecipe(output, 50, 50, new ItemStack(Items.COBBLESTONE));

        addStoneSolidierRecipe(output, 1000, 150, new ItemStack(Items.DEEPSLATE));

        addStoneSolidierRecipe(output, 150, 150, new ItemStack(Items.COBBLED_DEEPSLATE));

        addStoneSolidierRecipe(output, 1000, 50, new ItemStack(Items.GRANITE));

        addStoneSolidierRecipe(output, 1000, 50, new ItemStack(Items.DIORITE));

        addStoneSolidierRecipe(output, 1000, 50, new ItemStack(Items.ANDESITE));

        addStoneSolidierRecipe(output, 1000, 250, new ItemStack(Items.BLACKSTONE));

        addStoneSolidierRecipe(output, 1000, 1000, new ItemStack(Items.OBSIDIAN));
    }

    private void buildFiltrationPlantRecipes(Consumer<FinishedRecipe> output) {
        addOreFiltrationRecipe(output, new ItemStack(ModItems.RAW_TIN.get()), 0.05, "tin");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_COPPER), 0.05, "copper");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_IRON), 0.05, "iron");
        addOreFiltrationRecipe(output, new ItemStack(Items.RAW_GOLD), 0.005, "gold");
    }

    private void buildFluidTransposerRecipes(Consumer<FinishedRecipe> output) {
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

    private void buildChargerRecipes(Consumer<FinishedRecipe> output) {
        addChargerRecipe(output, Ingredient.of(Tags.Items.INGOTS_COPPER),
                new ItemStack(ModItems.ENERGIZED_COPPER_INGOT.get()), 4194304);
    }

    private void buildEnergizerRecipes(Consumer<FinishedRecipe> output) {
        addEnergizerRecipe(output, Ingredient.of(Tags.Items.INGOTS_COPPER),
                new ItemStack(ModItems.ENERGIZED_COPPER_INGOT.get()), 32768);
        addEnergizerRecipe(output, Ingredient.of(Tags.Items.INGOTS_GOLD),
                new ItemStack(ModItems.ENERGIZED_GOLD_INGOT.get()), 131072);
        addEnergizerRecipe(output, Ingredient.of(ModItems.CRYSTAL_MATRIX.get()),
                new ItemStack(ModItems.ENERGIZED_CRYSTAL_MATRIX.get()), 524288);
    }

    private void buildCrystalGrowthChamberRecipes(Consumer<FinishedRecipe> output) {
        addCrystalGrowthChamberRecipe(output, Ingredient.of(Tags.Items.GEMS_AMETHYST),
                new OutputItemStackWithPercentages(new ItemStack(Items.AMETHYST_SHARD), new double[] {
                    1., 1., .67, .5, .25, .125
                }), 16000);
        addCrystalGrowthChamberRecipe(output, Ingredient.of(Items.AMETHYST_BLOCK),
                new OutputItemStackWithPercentages(new ItemStack(Items.BUDDING_AMETHYST), .25), 4,
                32000);
    }

    private static void addBlastingAndSmeltingRecipes(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                                      int time, float xp, String group, String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, 2 * time, xp, group, recipeIngredientName);
    }
    private static void addBlastingAndSmeltingRecipes(Consumer<FinishedRecipe> output, TagKey<Item> ingredient, ItemStack result,
                                                      int time, float xp, String group, String recipeIngredientName) {
        addBlastingRecipe(output, ingredient, result, time, xp, group, recipeIngredientName);
        addSmeltingRecipe(output, ingredient, result, 2 * time, xp, group, recipeIngredientName);
    }

    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                          int time, float xp, String group) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()));

        addSmeltingRecipe(output, ingredient, result, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        addSmeltingRecipe(output, ingredient, result, time, xp, group, recipeId);
    }
    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                          int time, float xp, String group, ResourceLocation recipeId) {
        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefix(recipeId, "recipes/"),
                RecipeSerializer.SMELTING_RECIPE
        );
        output.accept(recipe);
    }
    private static void addSmeltingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smelting/" +
                getItemName(result.getItem()) + "_from_smelting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefix(recipeId, "recipes/"),
                RecipeSerializer.SMELTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addBlastingRecipe(Consumer<FinishedRecipe> output, ItemLike ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefix(recipeId, "recipes/"),
                RecipeSerializer.BLASTING_RECIPE
        );
        output.accept(recipe);
    }
    private static void addBlastingRecipe(Consumer<FinishedRecipe> output, TagKey<Item> ingredient, ItemStack result,
                                          int time, float xp, String group, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "blasting/" +
                getItemName(result.getItem()) + "_from_blasting_" + recipeIngredientName);

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(ingredient))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        FinishedRecipe recipe = new AbstractCookingFinishedRecipe(
                recipeId,
                Objects.requireNonNullElse(group, ""),
                Ingredient.of(ingredient), result.getItem(),
                xp, time,
                advancementBuilder,
                withPrefix(recipeId, "recipes/"),
                RecipeSerializer.BLASTING_RECIPE
        );
        output.accept(recipe);
    }

    private static void addNetheriteSmithingUpgradeRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient base, ItemStack output) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "smithing/" +
                getItemName(output.getItem()));

        Advancement.Builder advancementBuilder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
                .addCriterion("has_the_ingredient", has(Tags.Items.INGOTS_NETHERITE))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .requirements(RequirementsStrategy.OR);
        SmithingUpgradeFinishedRecipe recipe = new SmithingUpgradeFinishedRecipe(
                recipeId,
                base,
                Ingredient.of(Items.NETHERITE_INGOT),
                output.getItem(),
                advancementBuilder,
                withPrefix(recipeId, "recipes/")
        );
        recipeOutput.accept(recipe);
    }

    private static void addAlloyFurnaceRecipe(Consumer<FinishedRecipe> recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              int ticks) {
        addAlloyFurnaceRecipe(recipeOutput, inputs, output,
                new OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0]), ticks);
    }
    private static void addAlloyFurnaceRecipe(Consumer<FinishedRecipe> recipeOutput, IngredientWithCount[] inputs, ItemStack output,
                                              OutputItemStackWithPercentages secondaryOutput, int ticks) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "alloy_furnace/" +
                getItemName(output.getItem()));

        AlloyFurnaceFinishedRecipe recipe = new AlloyFurnaceFinishedRecipe(
                recipeId,
                output, secondaryOutput, inputs, ticks
        );
        recipeOutput.accept(recipe);

    }

    public static ResourceLocation withPrefix(ResourceLocation resourceLocation, String pathPrefix) {
        return new ResourceLocation(resourceLocation.getNamespace(), pathPrefix + resourceLocation.getPath());
    }

    private static void addPressMoldMakerRecipe(Consumer<FinishedRecipe> recipeOutput, int clayCount, ItemStack output) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "press_mold_maker/" +
                getItemName(output.getItem()));

        PressMoldMakerFinishedRecipe recipe = new PressMoldMakerFinishedRecipe(
                recipeId,
                output, clayCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addPlateCompressorRecipes(Consumer<FinishedRecipe> recipeOutput, Ingredient ingotInput,
                                                  Ingredient blockInput, ItemStack output, String metalName) {
        addPlateCompressorIngotRecipe(recipeOutput, ingotInput, output, metalName);
        addCompressorRecipe(recipeOutput, blockInput, ItemStackUtils.copyWithCount(output, 9), metalName + "_block");
    }
    private static void addPlateCompressorIngotRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient ingotInput,
                                                   ItemStack output, String metalName) {
        addCompressorRecipe(recipeOutput, ingotInput, output, metalName + "_ingot");
    }
    private static void addCompressorRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output, String recipeIngredientName) {
        addCompressorRecipe(recipeOutput, input, output, 1, recipeIngredientName);
    }
    private static void addCompressorRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output, int inputCount,
                                            String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "compressing/" +
                getItemName(output.getItem()) + "_from_compressing_" + recipeIngredientName);

        CompressorFinishedRecipe recipe = new CompressorFinishedRecipe(
                recipeId,
                output, input, inputCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addCrusherRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output,
                                         String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "crusher/" +
                getItemName(output.getItem()) + "_from_crushing_" + recipeIngredientName);

        CrusherFinishedRecipe recipe = new CrusherFinishedRecipe(
                recipeId,
                output, input
        );
        recipeOutput.accept(recipe);
    }

    private static void addBasicMetalPulverizerRecipes(Consumer<FinishedRecipe> recipeOutput, Ingredient oreInput,
                                                       Ingredient rawMetalInput, Ingredient rawMetalBlockInput,
                                                       Ingredient ingotInput, ItemStack output, String metalName) {
        addPulverizerRecipe(recipeOutput, oreInput, new PulverizerRecipe.OutputItemStackWithPercentages(output, new double[] {
                1., 1., .25
        }, new double[] {
                1., 1, .5, .25
        }), metalName + "_ores");

        addRawMetalAndIngotPulverizerRecipes(recipeOutput, rawMetalInput, rawMetalBlockInput, ingotInput, output, metalName);
    }
    private static void addRawMetalAndIngotPulverizerRecipes(Consumer<FinishedRecipe> recipeOutput,
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
    private static void addPulverizerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input,
                                            PulverizerRecipe.OutputItemStackWithPercentages output,
                                            String recipeIngredientName) {
        addPulverizerRecipe(recipeOutput, input, output,
                new PulverizerRecipe.OutputItemStackWithPercentages(ItemStack.EMPTY, new double[0], new double[0]), recipeIngredientName);
    }
    private static void addPulverizerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input,
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

    private static void addBasicFlowerGrowingRecipe(Consumer<FinishedRecipe> recipeOutput, ItemLike flowerItem,
                                                    String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, Ingredient.of(flowerItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., 1., .33
                })
        }, 16000, outputName, getItemName(flowerItem));
    }
    private static void addBasicMushroomsGrowingRecipe(Consumer<FinishedRecipe> recipeOutput, ItemLike mushroomItem,
                                                       String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, Ingredient.of(mushroomItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(mushroomItem), new double[] {
                        1., 1., .5, .25
                })
        }, 16000, outputName, getItemName(mushroomItem));
    }
    private static void addBasicAncientFlowerGrowingRecipe(Consumer<FinishedRecipe> recipeOutput, ItemLike seedItem,
                                                           ItemLike flowerItem, String outputName) {
        addPlantGrowthChamberRecipe(recipeOutput, Ingredient.of(seedItem), new OutputItemStackWithPercentages[] {
                new OutputItemStackWithPercentages(new ItemStack(seedItem), new double[] {
                        1., .33, .15
                }),
                new OutputItemStackWithPercentages(new ItemStack(flowerItem), new double[] {
                        1., .15
                })
        }, 16000, outputName, getItemName(seedItem));
    }
    private static void addPlantGrowthChamberRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input,
                                                    OutputItemStackWithPercentages[] outputs, int ticks,
                                                    String outputName, String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "growing/" +
                outputName + "_from_growing_" + recipeIngredientName);

        PlantGrowthChamberFinishedRecipe recipe = new PlantGrowthChamberFinishedRecipe(
                recipeId,
                outputs, input, ticks
        );
        recipeOutput.accept(recipe);
    }

    private static void addPlantGrowthChamberFertilizerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input,
                                                              double speedMultiplier, double energyConsumptionMultiplier,
                                                              String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "growing/fertilizer/" +
                recipeIngredientName);

        PlantGrowthChamberFertilizerFinishedRecipe recipe = new PlantGrowthChamberFertilizerFinishedRecipe(
                recipeId,
                input, speedMultiplier, energyConsumptionMultiplier
        );
        recipeOutput.accept(recipe);
    }

    private static void addGearMetalPressRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, output, new ItemStack(ModItems.GEAR_PRESS_MOLD.get()), 2);
    }
    private static void addRodMetalPressRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, ItemStackUtils.copyWithCount(output, 2), new ItemStack(ModItems.ROD_PRESS_MOLD.get()));
    }
    private static void addWireMetalPressRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output) {
        addMetalPressRecipe(recipeOutput, input, ItemStackUtils.copyWithCount(output, 3), new ItemStack(ModItems.WIRE_PRESS_MOLD.get()));
    }
    private static void addMetalPressRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold) {
        addMetalPressRecipe(recipeOutput, input, output, pressMold, 1);
    }
    private static void addMetalPressRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output,
                                            ItemStack pressMold, int inputCount) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "metal_press/" +
                getItemName(output.getItem()));

        MetalPressFinishedRecipe recipe = new MetalPressFinishedRecipe(
                recipeId,
                output, pressMold, input, inputCount
        );
        recipeOutput.accept(recipe);
    }

    private static void addHeatGeneratorRecipe(Consumer<FinishedRecipe> recipeOutput, Fluid input, int energyProduction,
                                               String recipeIngredientName) {
        addHeatGeneratorRecipe(recipeOutput, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private static void addHeatGeneratorRecipe(Consumer<FinishedRecipe> recipeOutput, Fluid[] input, int energyProduction,
                                               String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "heat_generator/" +
                "energy_production_from_" + recipeIngredientName);

        HeatGeneratorFinishedRecipe recipe = new HeatGeneratorFinishedRecipe(
                recipeId,
                input, energyProduction
        );
        recipeOutput.accept(recipe);
    }

    private static void addThermalGeneratorRecipe(Consumer<FinishedRecipe> recipeOutput, Fluid input, int energyProduction,
                                                  String recipeIngredientName) {
        addThermalGeneratorRecipe(recipeOutput, new Fluid[] {
                input
        }, energyProduction, recipeIngredientName);
    }
    private static void addThermalGeneratorRecipe(Consumer<FinishedRecipe> recipeOutput, Fluid[] input, int energyProduction,
                                                  String recipeIngredientName) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "thermal_generator/" +
                "energy_production_from_" + recipeIngredientName);

        ThermalGeneratorFinishedRecipe recipe = new ThermalGeneratorFinishedRecipe(
                recipeId,
                input, energyProduction
        );
        recipeOutput.accept(recipe);
    }

    private static void addStoneSolidierRecipe(Consumer<FinishedRecipe> recipeOutput, int waterAmount, int lavaAmount, ItemStack output) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "stone_solidifier/" +
                getItemName(output.getItem()));

        StoneSolidifierFinishedRecipe recipe = new StoneSolidifierFinishedRecipe(
                recipeId,
                output, waterAmount, lavaAmount
        );
        recipeOutput.accept(recipe);
    }

    private static void addAssemblingMachineRecipe(Consumer<FinishedRecipe> recipeOutput, IngredientWithCount[] inputs, ItemStack output) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "assembling/" +
                getItemName(output.getItem()));

        AssemblingMachineFinishedRecipe recipe = new AssemblingMachineFinishedRecipe(
                recipeId,
                output, inputs
        );
        recipeOutput.accept(recipe);
    }

    private static void addOreFiltrationRecipe(Consumer<FinishedRecipe> recipeOutput, ItemStack oreOutput, double oreOutputPercentage,
                                               String oreName) {
        addFiltrationPlantRecipe(recipeOutput, new OutputItemStackWithPercentages(new ItemStack(ModItems.STONE_PEBBLE.get()), .33),
                new OutputItemStackWithPercentages(oreOutput, oreOutputPercentage), ForgeRegistries.ITEMS.getKey(oreOutput.getItem()),
                oreName + "_ore_filtration");
    }
    private static void addFiltrationPlantRecipe(Consumer<FinishedRecipe> recipeOutput, OutputItemStackWithPercentages output,
                                                 ResourceLocation icon, String recipeName) {
        addFiltrationPlantRecipe(recipeOutput, output, OutputItemStackWithPercentages.EMPTY, icon, recipeName);
    }
    private static void addFiltrationPlantRecipe(Consumer<FinishedRecipe> recipeOutput, OutputItemStackWithPercentages output,
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

    private static void addConcreteFluidTransposerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output) {
        addFluidTransposerRecipe(recipeOutput, input, output, FluidTransposerBlockEntity.Mode.FILLING,
                new FluidStack(Fluids.WATER, 1000));
    }
    private static void addFluidTransposerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output,
                                                 FluidTransposerBlockEntity.Mode mode, FluidStack fluid) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "fluid_transposer/" +
                getItemName(output.getItem()));

        FluidTransposerFinishedRecipe recipe = new FluidTransposerFinishedRecipe(
                recipeId,
                mode, output, input, fluid
        );
        recipeOutput.accept(recipe);
    }

    private static void addChargerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "charger/" +
                getItemName(output.getItem()));

        ChargerFinishedRecipe recipe = new ChargerFinishedRecipe(
                recipeId,
                output, input, energyConsumption
        );
        recipeOutput.accept(recipe);
    }

    private static void addEnergizerRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, ItemStack output, int energyConsumption) {
        ResourceLocation recipeId = new ResourceLocation(EnergizedPowerMod.MODID, "energizer/" +
                getItemName(output.getItem()));

        EnergizerFinishedRecipe recipe = new EnergizerFinishedRecipe(
                recipeId,
                output, input, energyConsumption
        );
        recipeOutput.accept(recipe);
    }

    private static void addCrystalGrowthChamberRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, OutputItemStackWithPercentages output,
                                                      int ticks) {
        addCrystalGrowthChamberRecipe(recipeOutput, input, output, 1, ticks);
    }
    private static void addCrystalGrowthChamberRecipe(Consumer<FinishedRecipe> recipeOutput, Ingredient input, OutputItemStackWithPercentages output,
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
