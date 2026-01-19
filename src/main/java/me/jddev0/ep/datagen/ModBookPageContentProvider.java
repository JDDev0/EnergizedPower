package me.jddev0.ep.datagen;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.datagen.generators.PageContent;
import me.jddev0.ep.datagen.generators.PageContentProvider;
import me.jddev0.ep.input.ModKeyBindings;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModBookPageContentProvider extends PageContentProvider {
    private static final Style UNIFORM = Style.EMPTY.withFont(new Identifier("uniform"));
    private static final Style DEFAULT_FONT = Style.EMPTY.withFont(Style.DEFAULT_FONT_ID);

    private int chapterSortingNumber = 0;
    private int subChapterSortingNumber = 0;
    private int pageSortingNumber = 0;

    private String currentChapterIdPrefix = "";
    private String currentSubChapterIdPrefix = "";

    private String mainTableOfContentPageId;
    private String energyBlocksTableOfContentPageId;
    private final Map<String, String> tableOfContentPageTitles = new HashMap<>();
    private final Map<String, List<Pair<String, PageContent>>> tableOfContentsEntries = new HashMap<>();

    public ModBookPageContentProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void registerPageContent() {
        registerWelcomeChapter();
        registerResourcesChapter();
        registerUpgradesChapter();
        registerToolsChapter();
        registerWorkbenchesChapter();
        registerEnergyItemsChapter();

        registerEnergyBlocksChapter();

        registerFluidBlocksChapter();
        registerItemTransportationChapter();
        registerFluidsChapter();
        registerMachineConfigurationChapter();
        registerEntitiesChapter();
        registerStructuresChapter();

        registerTableOfContentsPages();
    }

    private void registerWelcomeChapter() {
        chapterId("welcome");

        addSimplePage(pageId("welcome"), Text.empty().append(
                Text.translatable("book.energizedpower.page.welcome.title").
                        formatted(Formatting.GOLD, Formatting.BOLD).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.welcome.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ).append(
                Text.literal("\n").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(addLinkToComponent(Text.translatable("book.energizedpower.page.welcome.2"),
                                "https://wiki.jddev0.com/books/energized-power/page/home"))
        ).append(
                Text.literal("\n").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.welcome.3")).append(": ").
                        append(addLinkToComponent(Text.translatable("book.energizedpower.page.welcome.3.link"),
                                "https://github.com/JDDev0/EnergizedPower/issues"))
        ).append(
                Text.literal("\n").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.welcome.4")).append(": ").
                        append(addLinkToComponent(Text.translatable("book.energizedpower.page.welcome.4.link"),
                                "https://discord.gg/sAKDNAU7yH"))
        ).append(
                Text.literal("\n").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.welcome.5")).append(": ").
                        append(addLinkToComponent(Text.translatable("book.energizedpower.page.welcome.5.link"),
                                "https://www.reddit.com/r/EnergizedPower/"))
        ));

        addSimplePage(pageId("credits"), Text.empty().append(
                Text.translatable("book.energizedpower.page.credits.title").
                        formatted(Formatting.GOLD, Formatting.BOLD).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.credits.subtitle").
                        formatted(Formatting.DARK_GREEN, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.literal("\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.credits.1")).append(": ").
                        append(addLinkToComponent(Text.literal("flashbulbs"),
                                "https://github.com/flashbulbs"))
        ).append(
                Text.literal("\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.credits.2")).append(": ").
                        append(addLinkToComponent(Text.literal("flashbulbs"),
                                "https://github.com/flashbulbs"))
        ).append(
                Text.literal("\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.credits.3")).append(": ").
                        append(addLinkToComponent(Text.literal("flashbulbs"),
                                "https://github.com/flashbulbs"))
        ).append(
                Text.literal("\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.credits.4")).append(": ").
                        append(addLinkToComponent(Text.literal("flashbulbs"),
                                "https://github.com/flashbulbs"))
        ).append(
                Text.literal("\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.credits.5")).append(": ").
                        append(addLinkToComponent(Text.literal("HanJiang-cn"),
                                "https://github.com/HanJiang-cn"))
        ).append(
                Text.literal("\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.credits.6")).append(": ").
                        append(addLinkToComponent(Text.literal("Roby1164"),
                                "https://github.com/Roby1164"))
        ).append(
                Text.literal("\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.credits.7")).append(": ").
                        append(addLinkToComponent(Text.literal("Eyyup"),
                                "https://github.com/msb-eyyup"))
        ).append(
                Text.literal("\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.credits.8")).append(": ").
                        append(Text.literal("Imperial Officer")).
                        append(", ").
                        append(addLinkToComponent(Text.literal("PlayboyX312"), "https://github.com/PlayboyX312"))
        ).append(
                Text.literal("\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.credits.9")).append(": ").
                        append(addLinkToComponent(Text.literal("Lucanoria"),
                                "https://github.com/Lucanoria"))
        ).append(
                Text.literal("\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                        append(Text.translatable("book.energizedpower.page.credits.10")).append(": ").
                        append(Text.literal("Sebby"))
        ));

        mainTableOfContentPageId = pageId("table_of_contents");
        tableOfContentPageTitles.put(mainTableOfContentPageId, "book.energizedpower.page.table_of_contents.title");
        tableOfContentsEntries.put(mainTableOfContentPageId, new ArrayList<>());
    }
    private void registerResourcesChapter() {
        chapterId("resources");

        String resourcesChapterTitle = "book.energizedpower.page.chapter.resources.title";
        PageContent resourcesChapterPage = addChapterPage(pageId("resources_chapter"),
                Text.translatable(resourcesChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.resources.1")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ), EPAPI.id("textures/item/energized_copper_ingot.png"));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(resourcesChapterTitle, resourcesChapterPage));

        addSimplePage(pageId("cable_insulator"), Text.empty().append(
                Text.translatable("book.energizedpower.page.cable_insulator.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.cable_insulator.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.cable_insulator.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/cable_insulator.png"));

        addSimplePage(pageId("saw_blade"), Text.empty().append(
                Text.translatable("book.energizedpower.page.saw_blade").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/saw_blade.png"));

        addSimplePage(pageId("silicon"), Text.empty().append(
                Text.translatable("book.energizedpower.page.silicon.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.silicon.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/silicon.png"));

        addSimplePage(pageId("sawdust"), Text.empty().append(
                Text.translatable("book.energizedpower.page.sawdust.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.sawdust.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/sawdust.png"));

        addSimplePage(pageId("fertilizers"), Text.empty().append(
                Text.translatable("book.energizedpower.page.fertilizers").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/basic_fertilizer.png"),
                EPAPI.id("textures/item/good_fertilizer.png"),
                EPAPI.id("textures/item/advanced_fertilizer.png")
        });

        addSimplePage(pageId("ore_dusts"), Text.empty().append(
                Text.translatable("book.energizedpower.page.ore_dusts").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/tin_dust.png"),
                EPAPI.id("textures/item/copper_dust.png"),
                EPAPI.id("textures/item/iron_dust.png"),
                EPAPI.id("textures/item/gold_dust.png")
        });

        addSimplePage(pageId("charcoal_dust"), Text.empty().append(
                Text.translatable("book.energizedpower.page.charcoal_dust").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/charcoal_dust.png"));

        addSimplePage(pageId("charcoal_filter"), Text.empty().append(
                Text.translatable("book.energizedpower.page.charcoal_filter").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/charcoal_filter.png"));

        addSimplePage(pageId("plates"), Text.empty().append(
                Text.translatable("book.energizedpower.page.plates.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.plates.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/tin_plate.png"),
                EPAPI.id("textures/item/copper_plate.png"),
                EPAPI.id("textures/item/iron_plate.png"),
                EPAPI.id("textures/item/gold_plate.png"),
                EPAPI.id("textures/item/advanced_alloy_plate.png"),
                EPAPI.id("textures/item/energized_copper_plate.png"),
                EPAPI.id("textures/item/energized_gold_plate.png")
        });

        addSimplePage(pageId("alloys"), Text.empty().append(
                Text.translatable("book.energizedpower.page.alloys").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/steel_ingot.png"),
                EPAPI.id("textures/item/redstone_alloy_ingot.png"),
                EPAPI.id("textures/item/advanced_alloy_ingot.png")
        });

        addSimplePage(pageId("metal_products"), Text.empty().append(
                Text.translatable("book.energizedpower.page.metal_products.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.metal_products.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/iron_gear.png"),
                EPAPI.id("textures/item/iron_rod.png"),
                EPAPI.id("textures/item/tin_wire.png"),
                EPAPI.id("textures/item/copper_wire.png"),
                EPAPI.id("textures/item/gold_wire.png"),
                EPAPI.id("textures/item/energized_copper_wire.png"),
                EPAPI.id("textures/item/energized_gold_wire.png")
        });

        addSimplePage(pageId("circuits"), Text.empty().append(
                Text.translatable("book.energizedpower.page.circuits").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/basic_circuit.png"),
                EPAPI.id("textures/item/advanced_circuit.png"),
                EPAPI.id("textures/item/processing_unit.png"),
                EPAPI.id("textures/item/teleporter_processing_unit.png")
        });

        addSimplePage(pageId("energized_copper_ingot"), Text.empty().append(
                Text.translatable("book.energizedpower.page.energized_copper_ingot.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.energized_copper_ingot.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.energized_copper_ingot.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ).append(
                Text.translatable("book.energizedpower.page.energized_copper_ingot.4").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/energized_copper_ingot.png"));

        addSimplePage(pageId("energized_gold_ingot"), Text.empty().append(
                Text.translatable("book.energizedpower.page.energized_gold_ingot").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/energized_gold_ingot.png"));

        addSimplePage(pageId("energized_crystal_matrix"), Text.empty().append(
                Text.translatable("book.energizedpower.page.energized_crystal_matrix").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/energized_crystal_matrix.png"));
    }
    private void registerUpgradesChapter() {
        chapterId("upgrades");

        String upgradesChapterTitle = "book.energizedpower.page.chapter.upgrades.title";
        PageContent upgradesChapterPage = addChapterPage(pageId("upgrades_chapter"),
                Text.translatable(upgradesChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.upgrades")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ), EPAPI.id("textures/item/speed_upgrade_module_1.png"));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(upgradesChapterTitle, upgradesChapterPage));

        addSimplePage(pageId("speed_upgrades"), Text.empty().append(
                Text.translatable("book.energizedpower.page.speed_upgrades").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/speed_upgrade_module_1.png"),
                EPAPI.id("textures/item/speed_upgrade_module_2.png"),
                EPAPI.id("textures/item/speed_upgrade_module_3.png"),
                EPAPI.id("textures/item/speed_upgrade_module_4.png"),
                EPAPI.id("textures/item/speed_upgrade_module_5.png")
        });

        addSimplePage(pageId("energy_efficiency_upgrades"), Text.empty().append(
                Text.translatable("book.energizedpower.page.energy_efficiency_upgrades").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/energy_efficiency_upgrade_module_1.png"),
                EPAPI.id("textures/item/energy_efficiency_upgrade_module_2.png"),
                EPAPI.id("textures/item/energy_efficiency_upgrade_module_3.png"),
                EPAPI.id("textures/item/energy_efficiency_upgrade_module_4.png"),
                EPAPI.id("textures/item/energy_efficiency_upgrade_module_5.png")
        });

        addSimplePage(pageId("energy_capacity_upgrades"), Text.empty().append(
                Text.translatable("book.energizedpower.page.energy_capacity_upgrades").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/energy_capacity_upgrade_module_1.png"),
                EPAPI.id("textures/item/energy_capacity_upgrade_module_2.png"),
                EPAPI.id("textures/item/energy_capacity_upgrade_module_3.png"),
                EPAPI.id("textures/item/energy_capacity_upgrade_module_4.png"),
                EPAPI.id("textures/item/energy_capacity_upgrade_module_5.png")
        });

        addSimplePage(pageId("duration_upgrades"), Text.empty().append(
                Text.translatable("book.energizedpower.page.duration_upgrades.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.duration_upgrades.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/duration_upgrade_module_1.png"),
                EPAPI.id("textures/item/duration_upgrade_module_2.png"),
                EPAPI.id("textures/item/duration_upgrade_module_3.png"),
                EPAPI.id("textures/item/duration_upgrade_module_4.png"),
                EPAPI.id("textures/item/duration_upgrade_module_5.png"),
                EPAPI.id("textures/item/duration_upgrade_module_6.png")
        });

        addSimplePage(pageId("range_upgrades"), Text.empty().append(
                Text.translatable("book.energizedpower.page.range_upgrades").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/range_upgrade_module_1.png"),
                EPAPI.id("textures/item/range_upgrade_module_2.png"),
                EPAPI.id("textures/item/range_upgrade_module_3.png")
        });

        addSimplePage(pageId("extraction_depth_upgrades"), Text.empty().append(
                Text.translatable("book.energizedpower.page.extraction_depth_upgrades").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/extraction_depth_upgrade_module_1.png"),
                EPAPI.id("textures/item/extraction_depth_upgrade_module_2.png"),
                EPAPI.id("textures/item/extraction_depth_upgrade_module_3.png"),
                EPAPI.id("textures/item/extraction_depth_upgrade_module_4.png"),
                EPAPI.id("textures/item/extraction_depth_upgrade_module_5.png")
        });

        addSimplePage(pageId("extraction_range_upgrades"), Text.empty().append(
                Text.translatable("book.energizedpower.page.extraction_range_upgrades").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/extraction_range_upgrade_module_1.png"),
                EPAPI.id("textures/item/extraction_range_upgrade_module_2.png"),
                EPAPI.id("textures/item/extraction_range_upgrade_module_3.png"),
                EPAPI.id("textures/item/extraction_range_upgrade_module_4.png"),
                EPAPI.id("textures/item/extraction_range_upgrade_module_5.png")
        });

        addSimplePage(pageId("furnace_mode_upgrades"), Text.empty().append(
                Text.translatable("book.energizedpower.page.furnace_mode_upgrades").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/blast_furnace_upgrade_module.png"),
                EPAPI.id("textures/item/smoker_upgrade_module.png")
        });

        addSimplePage(pageId("moon_light_upgrades"), Text.empty().append(
                Text.translatable("book.energizedpower.page.moon_light_upgrades").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/moon_light_upgrade_module_1.png"),
                EPAPI.id("textures/item/moon_light_upgrade_module_2.png"),
                EPAPI.id("textures/item/moon_light_upgrade_module_3.png")
        });
    }
    private void registerToolsChapter() {
        chapterId("tools");

        String toolsChapterTitle = "book.energizedpower.page.chapter.tools.title";
        PageContent toolsChapterPage = addChapterPage(pageId("tools_chapter"),
                Text.translatable(toolsChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.tools")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ), EPAPI.id("textures/item/stone_hammer.png"));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(toolsChapterTitle, toolsChapterPage));

        addSimplePage(pageId("hammers"), Text.empty().append(
                Text.translatable("book.energizedpower.page.hammers").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/wooden_hammer.png"),
                EPAPI.id("textures/item/stone_hammer.png"),
                EPAPI.id("textures/item/iron_hammer.png"),
                EPAPI.id("textures/item/golden_hammer.png"),
                EPAPI.id("textures/item/diamond_hammer.png"),
                EPAPI.id("textures/item/netherite_hammer.png")
        });

        addSimplePage(pageId("cutters"), Text.empty().append(
                Text.translatable("book.energizedpower.page.cutters.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.cutters.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/cutter.png"));

        addSimplePage(pageId("wrench"), Text.empty().append(
                Text.translatable("book.energizedpower.page.wrench").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/wrench.png"));
    }
    private void registerWorkbenchesChapter() {
        chapterId("workbenches");

        String workbenchesChapterTitle = "book.energizedpower.page.chapter.workbenches.title";
        PageContent workbenchesChapterPage = addChapterPage(pageId("workbenches_chapter"),
                Text.translatable(workbenchesChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.workbenches")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ), Blocks.CRAFTING_TABLE);
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(workbenchesChapterTitle, workbenchesChapterPage));

        addSimplePage(pageId("press_mold_maker"), Text.empty().append(
                Text.translatable("book.energizedpower.page.press_mold_maker").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.PRESS_MOLD_MAKER);

        addSimplePage(pageId("alloy_furnace"), Text.empty().append(
                Text.translatable("book.energizedpower.page.alloy_furnace.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.alloy_furnace.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.alloy_furnace.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.ALLOY_FURNACE);
    }
    private void registerEnergyItemsChapter() {
        chapterId("energy_items");

        String energyItemsChapterTitle = "book.energizedpower.page.chapter.energy_items.title";
        PageContent energyItemsChapterPage = addChapterPage(pageId("energy_items_chapter"),
                Text.translatable(energyItemsChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.energy_items")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(energyItemsChapterTitle, energyItemsChapterPage));

        addSimplePage(pageId("inventory_coal_engine"), Text.empty().append(
                Text.translatable("book.energizedpower.page.inventory_coal_engine.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.inventory_coal_engine.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.inventory_coal_engine.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ).append(
                Text.translatable("book.energizedpower.page.inventory_coal_engine.4").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/inventory_coal_engine_on.png"));

        addSimplePage(pageId("inventory_charger"), Text.empty().append(
                Text.translatable("book.energizedpower.page.inventory_charger.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.inventory_charger.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/inventory_charger.png"));

        addSimplePage(pageId("inventory_teleporter"), Text.empty().append(
                Text.translatable("book.energizedpower.page.inventory_teleporter").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/inventory_teleporter.png"));

        addSimplePage(pageId("energy_analyzer"), Text.empty().append(
                Text.translatable("book.energizedpower.page.energy_analyzer.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.energy_analyzer.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/energy_analyzer.png"));

        addSimplePage(pageId("fluid_analyzer"), Text.empty().append(
                Text.translatable("book.energizedpower.page.fluid_analyzer.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.fluid_analyzer.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/fluid_analyzer.png"));

        addSimplePage(pageId("batteries"), Text.empty().append(
                Text.translatable("book.energizedpower.page.batteries.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.batteries.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/battery_1.png"),
                EPAPI.id("textures/item/battery_2.png"),
                EPAPI.id("textures/item/battery_3.png"),
                EPAPI.id("textures/item/battery_4.png"),
                EPAPI.id("textures/item/battery_5.png"),
                EPAPI.id("textures/item/battery_6.png"),
                EPAPI.id("textures/item/battery_7.png"),
                EPAPI.id("textures/item/battery_8.png")
        });
    }

    private void registerEnergyBlocksChapter() {
        chapterId("energy_blocks");

        registerEnergyBlocksChapterMainSubChapter();
        registerEnergyBlocksChapterGeneralSubChapter();
        registerEnergyBlocksChapterEnergyTransportationSubChapter();
        registerEnergyBlocksChapterEnergyStorageSubChapter();
        registerEnergyBlocksChapterEnergyProductionSubChapter();
        registerEnergyBlocksChapterEnergyConsumptionSubChapter();
    }
    private void registerEnergyBlocksChapterMainSubChapter() {
        subChapterId("main");

        String energyBlocksMainChapterTitle = "book.energizedpower.page.chapter.energy_blocks.title";
        PageContent energyBlocksMainChapterPage = addChapterPage(pageId("energy_blocks_chapter"),
                Text.translatable(energyBlocksMainChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.energy_blocks")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(energyBlocksMainChapterTitle, energyBlocksMainChapterPage));

        energyBlocksTableOfContentPageId = pageId("table_of_contents");
        tableOfContentPageTitles.put(energyBlocksTableOfContentPageId, "book.energizedpower.page.energy_blocks.table_of_contents.title");
        tableOfContentsEntries.put(energyBlocksTableOfContentPageId, new ArrayList<>());
    }
    private void registerEnergyBlocksChapterGeneralSubChapter() {
        subChapterId("general");

        String energyBlocksGeneralChapterTitle = "book.energizedpower.page.chapter.energy_blocks.general.title";
        PageContent energyBlocksGeneralChapterPage = addChapterPage(pageId("general_chapter"),
                Text.translatable(energyBlocksGeneralChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.energy_blocks.general")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(energyBlocksTableOfContentPageId))
                ));
        tableOfContentsEntries.get(energyBlocksTableOfContentPageId).add(Pair.of(energyBlocksGeneralChapterTitle, energyBlocksGeneralChapterPage));

        addSimplePage(pageId("machine_frames"), Text.empty().append(
                Text.translatable("book.energizedpower.page.machine_frames.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.machine_frames.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_MACHINE_FRAME,
                EPBlocks.HARDENED_MACHINE_FRAME,
                EPBlocks.ADVANCED_MACHINE_FRAME,
                EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME
        });
    }
    private void registerEnergyBlocksChapterEnergyTransportationSubChapter() {
        subChapterId("energy_transportation");

        String energyBlocksEnergyTransportationChapterTitle = "book.energizedpower.page.chapter.energy_blocks.energy_transportation.title";
        PageContent energyBlocksEnergyTransportationChapterPage = addChapterPage(pageId("energy_transportation_chapter"),
                Text.translatable(energyBlocksEnergyTransportationChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.energy_blocks.energy_transportation")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(energyBlocksTableOfContentPageId))
                ));
        tableOfContentsEntries.get(energyBlocksTableOfContentPageId).add(Pair.of(energyBlocksEnergyTransportationChapterTitle, energyBlocksEnergyTransportationChapterPage));

        addSimplePage(pageId("cables"), Text.empty().append(
                Text.translatable("book.energizedpower.page.cables.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.cables.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.cables.3").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.TIN_CABLE,
                EPBlocks.COPPER_CABLE,
                EPBlocks.GOLD_CABLE,
                EPBlocks.ENERGIZED_COPPER_CABLE,
                EPBlocks.ENERGIZED_GOLD_CABLE,
                EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE
        });

        addSimplePage(pageId("transformers"), Text.empty().append(
                Text.translatable("book.energizedpower.page.transformers.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.transformers.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.transformers.3").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.transformers.4").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.transformers.5").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.transformers.6").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.transformers.7").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.transformers.8").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.transformers.9").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.LV_TRANSFORMER_1_TO_N,
                EPBlocks.LV_TRANSFORMER_3_TO_3,
                EPBlocks.LV_TRANSFORMER_N_TO_1,
                EPBlocks.CONFIGURABLE_LV_TRANSFORMER,
                EPBlocks.MV_TRANSFORMER_1_TO_N,
                EPBlocks.MV_TRANSFORMER_3_TO_3,
                EPBlocks.MV_TRANSFORMER_N_TO_1,
                EPBlocks.CONFIGURABLE_MV_TRANSFORMER,
                EPBlocks.HV_TRANSFORMER_1_TO_N,
                EPBlocks.HV_TRANSFORMER_3_TO_3,
                EPBlocks.HV_TRANSFORMER_N_TO_1,
                EPBlocks.CONFIGURABLE_HV_TRANSFORMER,
                EPBlocks.EHV_TRANSFORMER_1_TO_N,
                EPBlocks.EHV_TRANSFORMER_3_TO_3,
                EPBlocks.EHV_TRANSFORMER_N_TO_1,
                EPBlocks.CONFIGURABLE_EHV_TRANSFORMER
        });

        addSimplePage(pageId("minecart_charger_uncharger"), Text.empty().append(
                Text.translatable("book.energizedpower.page.minecart_charger_uncharger.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.minecart_charger_uncharger.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.minecart_charger_uncharger.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.MINECART_CHARGER,
                EPBlocks.MINECART_UNCHARGER,
                EPBlocks.ADVANCED_MINECART_CHARGER,
                EPBlocks.ADVANCED_MINECART_UNCHARGER
        });
    }
    private void registerEnergyBlocksChapterEnergyStorageSubChapter() {
        subChapterId("energy_storage");

        String energyBlocksEnergyStorageChapterTitle = "book.energizedpower.page.chapter.energy_blocks.energy_storage.title";
        PageContent energyBlocksEnergyStorageChapterPage = addChapterPage(pageId("energy_storage_chapter"),
                Text.translatable(energyBlocksEnergyStorageChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.energy_blocks.energy_storage")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(energyBlocksTableOfContentPageId))
                ));
        tableOfContentsEntries.get(energyBlocksTableOfContentPageId).add(Pair.of(energyBlocksEnergyStorageChapterTitle, energyBlocksEnergyStorageChapterPage));

        addSimplePage(pageId("battery_box"), Text.empty().append(
                Text.translatable("book.energizedpower.page.battery_box").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BATTERY_BOX,
                EPBlocks.ADVANCED_BATTERY_BOX
        });
    }
    private void registerEnergyBlocksChapterEnergyProductionSubChapter() {
        subChapterId("energy_production");

        String energyBlocksEnergyProductionChapterTitle = "book.energizedpower.page.chapter.energy_blocks.energy_production.title";
        PageContent energyBlocksEnergyProductionChapterPage = addChapterPage(pageId("energy_production_chapter"),
                Text.translatable(energyBlocksEnergyProductionChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.energy_blocks.energy_production")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(energyBlocksTableOfContentPageId))
                ));
        tableOfContentsEntries.get(energyBlocksTableOfContentPageId).add(Pair.of(energyBlocksEnergyProductionChapterTitle, energyBlocksEnergyProductionChapterPage));

        addSimplePage(pageId("solar_cells"), Text.empty().append(
                Text.translatable("book.energizedpower.page.solar_cells.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.solar_cells.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/basic_solar_cell.png"),
                EPAPI.id("textures/item/advanced_solar_cell.png"),
                EPAPI.id("textures/item/reinforced_advanced_solar_cell.png")
        });

        addSimplePage(pageId("solar_panels"), Text.empty().append(
                Text.translatable("book.energizedpower.page.solar_panels.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.solar_panels.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.SOLAR_PANEL_1,
                EPBlocks.SOLAR_PANEL_2,
                EPBlocks.SOLAR_PANEL_3,
                EPBlocks.SOLAR_PANEL_4,
                EPBlocks.SOLAR_PANEL_5,
                EPBlocks.SOLAR_PANEL_6
        });

        addSimplePage(pageId("coal_engine"), Text.empty().append(
                Text.translatable("book.energizedpower.page.coal_engine.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.coal_engine.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.COAL_ENGINE);

        addSimplePage(pageId("heat_generator"), Text.empty().append(
                Text.translatable("book.energizedpower.page.heat_generator.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.heat_generator.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.heat_generator.3").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.heat_generator.4").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.HEAT_GENERATOR);

        addSimplePage(pageId("thermal_generator"), Text.empty().append(
                Text.translatable("book.energizedpower.page.thermal_generator.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.thermal_generator.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.THERMAL_GENERATOR);

        addSimplePage(pageId("lightning_generator"), Text.empty().append(
                Text.translatable("book.energizedpower.page.lightning_generator.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.lightning_generator.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.LIGHTNING_GENERATOR);
    }
    private void registerEnergyBlocksChapterEnergyConsumptionSubChapter() {
        subChapterId("energy_consumption");

        String energyBlocksEnergyConsumptionChapterTitle = "book.energizedpower.page.chapter.energy_blocks.energy_consumption.title";
        PageContent energyBlocksEnergyConsumptionChapterPage = addChapterPage(pageId("energy_consumption_chapter"),
                Text.translatable(energyBlocksEnergyConsumptionChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.energy_blocks.energy_consumption")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(energyBlocksTableOfContentPageId))
                ));
        tableOfContentsEntries.get(energyBlocksTableOfContentPageId).add(Pair.of(energyBlocksEnergyConsumptionChapterTitle, energyBlocksEnergyConsumptionChapterPage));

        addSimplePage(pageId("powered_lamp"), Text.empty().append(
                Text.translatable("book.energizedpower.page.powered_lamp").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.POWERED_LAMP);

        addSimplePage(pageId("powered_furnaces"), Text.empty().append(
                Text.translatable("book.energizedpower.page.powered_furnaces.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.powered_furnaces.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.POWERED_FURNACE,
                EPBlocks.ADVANCED_POWERED_FURNACE
        });

        addSimplePage(pageId("auto_crafters"), Text.empty().append(
                Text.translatable("book.energizedpower.page.auto_crafters.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.auto_crafters.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.auto_crafters.3").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.auto_crafters.4").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.AUTO_CRAFTER,
                EPBlocks.ADVANCED_AUTO_CRAFTER
        });

        addSimplePage(pageId("crushers"), Text.empty().append(
                Text.translatable("book.energizedpower.page.crushers.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.crushers.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.CRUSHER,
                EPBlocks.ADVANCED_CRUSHER
        });

        addSimplePage(pageId("pulverizers"), Text.empty().append(
                Text.translatable("book.energizedpower.page.pulverizers.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.pulverizers.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.PULVERIZER,
                EPBlocks.ADVANCED_PULVERIZER
        });

        addSimplePage(pageId("sawmill"), Text.empty().append(
                Text.translatable("book.energizedpower.page.sawmill.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.sawmill.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.SAWMILL);

        addSimplePage(pageId("compressor"), Text.empty().append(
                Text.translatable("book.energizedpower.page.compressor").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.COMPRESSOR);

        addSimplePage(pageId("metal_press"), Text.empty().append(
                Text.translatable("book.energizedpower.page.metal_press.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.metal_press.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.metal_press.3").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.metal_press.4").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.METAL_PRESS);

        addSimplePage(pageId("auto_press_mold_maker"), Text.empty().append(
                Text.translatable("book.energizedpower.page.auto_press_mold_maker").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.AUTO_PRESS_MOLD_MAKER);

        addSimplePage(pageId("auto_stonecutter"), Text.empty().append(
                Text.translatable("book.energizedpower.page.auto_stonecutter").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.AUTO_STONECUTTER);

        addSimplePage(pageId("assembling_machine"), Text.empty().append(
                Text.translatable("book.energizedpower.page.assembling_machine.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.assembling_machine.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.assembling_machine.3").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.ASSEMBLING_MACHINE);

        addSimplePage(pageId("plant_growth_chamber"), Text.empty().append(
                Text.translatable("book.energizedpower.page.plant_growth_chamber.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.plant_growth_chamber.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.plant_growth_chamber.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.plant_growth_chamber.4").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.plant_growth_chamber.5").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.plant_growth_chamber.6").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.plant_growth_chamber.7").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.PLANT_GROWTH_CHAMBER);

        addSimplePage(pageId("stone_liquefier"), Text.empty().append(
                Text.translatable("book.energizedpower.page.stone_liquefier.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.stone_liquefier.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.STONE_LIQUEFIER);

        addSimplePage(pageId("stone_solidifier"), Text.empty().append(
                Text.translatable("book.energizedpower.page.stone_solidifier.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.stone_solidifier.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.STONE_SOLIDIFIER);

        addSimplePage(pageId("filtration_plant"), Text.empty().append(
                Text.translatable("book.energizedpower.page.filtration_plant.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.filtration_plant.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.FILTRATION_PLANT);

        addSimplePage(pageId("fluid_transposer"), Text.empty().append(
                Text.translatable("book.energizedpower.page.fluid_transposer").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.FLUID_TRANSPOSER);

        addSimplePage(pageId("induction_smelter"), Text.empty().append(
                Text.translatable("book.energizedpower.page.induction_smelter.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.induction_smelter.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.induction_smelter.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.INDUCTION_SMELTER);

        addSimplePage(pageId("block_placer"), Text.empty().append(
                Text.translatable("book.energizedpower.page.block_placer.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.block_placer.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.BLOCK_PLACER);

        addSimplePage(pageId("fluid_filler_fluid_drainer"), Text.empty().append(
                Text.translatable("book.energizedpower.page.fluid_filler_fluid_drainer").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.FLUID_FILLER,
                EPBlocks.FLUID_DRAINER
        });

        addSimplePage(pageId("fluid_pumps"), Text.empty().append(
                Text.translatable("book.energizedpower.page.fluid_pumps").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.FLUID_PUMP,
                EPBlocks.ADVANCED_FLUID_PUMP
        });

        addSimplePage(pageId("charger_uncharger"), Text.empty().append(
                Text.translatable("book.energizedpower.page.charger_uncharger.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.charger_uncharger.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.CHARGER,
                EPBlocks.UNCHARGER,
                EPBlocks.ADVANCED_CHARGER,
                EPBlocks.ADVANCED_UNCHARGER
        });

        addSimplePage(pageId("charging_station"), Text.empty().append(
                Text.translatable("book.energizedpower.page.charging_station").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.CHARGING_STATION);

        addSimplePage(pageId("crystal_growth_chamber"), Text.empty().append(
                Text.translatable("book.energizedpower.page.crystal_growth_chamber").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.CRYSTAL_GROWTH_CHAMBER);

        addSimplePage(pageId("energizer"), Text.empty().append(
                Text.translatable("book.energizedpower.page.energizer").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.ENERGIZER);

        addSimplePage(pageId("weather_controller"), Text.empty().append(
                Text.translatable("book.energizedpower.page.weather_controller").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.WEATHER_CONTROLLER);

        addSimplePage(pageId("time_controller"), Text.empty().append(
                Text.translatable("book.energizedpower.page.time_controller.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.time_controller.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPBlocks.TIME_CONTROLLER);

        addSimplePage(pageId("teleporter_matrix"), Text.empty().append(
                Text.translatable("book.energizedpower.page.teleporter_matrix.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.teleporter_matrix.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/teleporter_matrix.png"));

        addSimplePage(pageId("teleporter"), Text.empty().append(
                Text.translatable("book.energizedpower.page.teleporter.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.teleporter.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.teleporter.3.1").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append(
                                Text.literal(" [").formatted(Formatting.BLACK).fillStyle(DEFAULT_FONT)
                        ).append(
                                Text.keybind(ModKeyBindings.KEY_TELEPORTER_USE).formatted(Formatting.DARK_GRAY).
                                        fillStyle(DEFAULT_FONT)
                        ).append(
                                Text.literal("] ").formatted(Formatting.BLACK).fillStyle(DEFAULT_FONT)
                        ).append(
                                Text.translatable("book.energizedpower.page.teleporter.3.2")
                        )
        ), EPBlocks.TELEPORTER);
    }

    private void registerFluidBlocksChapter() {
        chapterId("fluid_blocks");

        String fluidBlocksChapterTitle = "book.energizedpower.page.chapter.fluid_blocks.title";
        PageContent fluidBlocksChapterPage = addChapterPage(pageId("fluid_blocks_chapter"),
                Text.translatable(fluidBlocksChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.fluid_blocks")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(fluidBlocksChapterTitle, fluidBlocksChapterPage));

        addSimplePage(pageId("fluid_pipes"), Text.empty().append(
                Text.translatable("book.energizedpower.page.fluid_pipes.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.fluid_pipes.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.fluid_pipes.3").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.fluid_pipes.4").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.IRON_FLUID_PIPE,
                EPBlocks.GOLDEN_FLUID_PIPE
        });

        addSimplePage(pageId("fluid_tanks"), Text.empty().append(
                Text.translatable("book.energizedpower.page.fluid_tanks.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.fluid_tanks.2").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.FLUID_TANK_SMALL,
                EPBlocks.FLUID_TANK_MEDIUM,
                EPBlocks.FLUID_TANK_LARGE
        });

        addSimplePage(pageId("drain"), Text.empty().append(
                Text.translatable("book.energizedpower.page.drain.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.drain.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPBlocks.DRAIN);
    }
    private void registerItemTransportationChapter() {
        chapterId("item_transportation");

        String itemTransportationChapterTitle = "book.energizedpower.page.chapter.item_transportation.title";
        PageContent itemTransportationChapterPage = addChapterPage(pageId("item_transportation_chapter"),
                Text.translatable(itemTransportationChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.item_transportation")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(itemTransportationChapterTitle, itemTransportationChapterPage));

        addSimplePage(pageId("item_conveyor_belt"), Text.empty().append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt.4").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt.5").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt.6").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT,
        });

        addSimplePage(pageId("item_conveyor_belt_loader"), Text.empty().append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_loader.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_loader.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_loader.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_loader.4").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_loader.5").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER,
        });

        addSimplePage(pageId("item_conveyor_belt_sorter"), Text.empty().append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_sorter.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_sorter.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_sorter.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_sorter.4").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_sorter.5").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_sorter.6").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_sorter.7").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_sorter.8").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_sorter.9").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER,
        });

        addSimplePage(pageId("item_conveyor_belt_switch"), Text.empty().append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_switch.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_switch.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH,
        });

        addSimplePage(pageId("item_conveyor_belt_splitter"), Text.empty().append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_splitter").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER,
        });

        addSimplePage(pageId("item_conveyor_belt_merger"), Text.empty().append(
                Text.translatable("book.energizedpower.page.item_conveyor_belt_merger").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER,
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER,
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER,
        });

        addSimplePage(pageId("item_silos"), Text.empty().append(
                Text.translatable("book.energizedpower.page.item_silos.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(" ")
        ).append(
                Text.translatable("book.energizedpower.page.item_silos.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.item_silos.3").
                        formatted(Formatting.BLACK, Formatting.ITALIC).fillStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.ITEM_SILO_TINY,
                EPBlocks.ITEM_SILO_SMALL,
                EPBlocks.ITEM_SILO_MEDIUM,
                EPBlocks.ITEM_SILO_LARGE,
                EPBlocks.ITEM_SILO_GIANT,
        });
    }
    private void registerFluidsChapter() {
        chapterId("fluids");

        String fluidsChapterTitle = "book.energizedpower.page.chapter.fluids.title";
        PageContent fluidsChapterPage = addChapterPage(pageId("fluids_chapter"),
                Text.translatable(fluidsChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.fluids")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(fluidsChapterTitle, fluidsChapterPage));

        addSimplePage(pageId("dirty_water"), Text.empty().append(
                Text.translatable("book.energizedpower.page.dirty_water.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n")
        ).append(
                Text.translatable("book.energizedpower.page.dirty_water.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/item/dirty_water_bucket.png"));
    }
    private void registerMachineConfigurationChapter() {
        chapterId("machine_configuration");

        String machineConfigurationChapterTitle = "book.energizedpower.page.chapter.machine_configuration.title";
        PageContent machineConfigurationChapterPage = addChapterPage(pageId("machine_configuration_chapter"),
                Text.translatable(machineConfigurationChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.machine_configuration")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(machineConfigurationChapterTitle, machineConfigurationChapterPage));

        addSimplePage(pageId("upgrade_configuration"), Text.empty().append(
                Text.translatable("book.energizedpower.page.upgrade_configuration").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), EPAPI.id("textures/gui/book_icons/upgrade_view.png"));

        addSimplePage(pageId("redstone_mode_configuration"), Text.empty().append(
                Text.translatable("book.energizedpower.page.redstone_mode_configuration.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ).append(
                Text.literal("\n\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(
                                Text.translatable("book.energizedpower.page.redstone_mode_configuration.2").
                                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
                        )
        ).append(
                Text.literal("\n\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(
                                Text.translatable("book.energizedpower.page.redstone_mode_configuration.3").
                                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
                        )
        ).append(
                Text.literal("\n\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(
                                Text.translatable("book.energizedpower.page.redstone_mode_configuration.4").
                                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
                        )
        ), new Identifier[] {
                EPAPI.id("textures/gui/book_icons/redstone_mode_ignore.png"),
                EPAPI.id("textures/gui/book_icons/redstone_mode_high.png"),
                EPAPI.id("textures/gui/book_icons/redstone_mode_low.png")
        });

        addSimplePage(pageId("comparator_mode_configuration"), Text.empty().append(
                Text.translatable("book.energizedpower.page.comparator_mode_configuration.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ).append(
                Text.literal("\n\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(
                                Text.translatable("book.energizedpower.page.comparator_mode_configuration.2").
                                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
                        )
        ).append(
                Text.literal("\n\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(
                                Text.translatable("book.energizedpower.page.comparator_mode_configuration.3").
                                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
                        )
        ).append(
                Text.literal("\n\n• ").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(
                                Text.translatable("book.energizedpower.page.comparator_mode_configuration.4").
                                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
                        )
        ), new Identifier[] {
                EPAPI.id("textures/gui/book_icons/comparator_mode_item.png"),
                EPAPI.id("textures/gui/book_icons/comparator_mode_fluid.png"),
                EPAPI.id("textures/gui/book_icons/comparator_mode_energy.png")
        });
    }
    private void registerEntitiesChapter() {
        chapterId("entities");

        String entitiesChapterTitle = "book.energizedpower.page.chapter.entities.title";
        PageContent entitiesChapterPage = addChapterPage(pageId("entities_chapter"),
                Text.translatable(entitiesChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.entities")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(entitiesChapterTitle, entitiesChapterPage));

        addSimplePage(pageId("battery_box_minecarts"), Text.empty().append(
                Text.translatable("book.energizedpower.page.battery_box_minecarts").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ), new Identifier[] {
                EPAPI.id("textures/item/battery_box_minecart.png"),
                EPAPI.id("textures/item/advanced_battery_box_minecart.png")
        });
    }
    private void registerStructuresChapter() {
        chapterId("structures");

        String structuresChapterTitle = "book.energizedpower.page.chapter.structures.title";
        PageContent structuresChapterPage = addChapterPage(pageId("structures_chapter"),
                Text.translatable(structuresChapterTitle).
                        formatted(Formatting.GOLD),
                Text.empty().append(
                        Text.translatable("book.energizedpower.page.chapter.structures")
                ).append(
                        Text.literal("\n\n").
                                formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ), EPBlocks.BASIC_MACHINE_FRAME);
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(structuresChapterTitle, structuresChapterPage));

        addSimplePage(pageId("electrician"), Text.empty().append(
                Text.translatable("book.energizedpower.page.electrician.title").
                        formatted(Formatting.GOLD, Formatting.BOLD).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.electrician.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.electrician.2").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.electrician.3").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ));

        addSimplePage(pageId("factory"), Text.empty().append(
                Text.translatable("book.energizedpower.page.factory.title").
                        formatted(Formatting.GOLD, Formatting.BOLD).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.factory.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ));

        addSimplePage(pageId("small_solar_farm"), Text.empty().append(
                Text.translatable("book.energizedpower.page.small_solar_farm.title").
                        formatted(Formatting.GOLD, Formatting.BOLD).append("\n\n")
        ).append(
                Text.translatable("book.energizedpower.page.small_solar_farm.1").
                        formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM)
        ));
    }

    private void registerTableOfContentsPages() {
        tableOfContentsEntries.forEach((pageId, entries) -> {
            MutableText content = Text.translatable(tableOfContentPageTitles.get(pageId)).
                    formatted(Formatting.GOLD, Formatting.BOLD);

            entries.forEach(entry -> content.append(Text.literal("\n\n• ").
                    formatted(Formatting.BLACK, Formatting.BOLD).fillStyle(UNIFORM).append(
                            tableOfContentEntryComponent(entry.getFirst(), entry.getSecond())
                    )));

            addSimplePage(pageId, content);
        });
    }

    private MutableText tableOfContentEntryComponent(String chapterTitle, PageContent chapterPage) {
        return Text.translatable(chapterTitle).formatted(Formatting.DARK_GREEN, Formatting.UNDERLINE).
                fillStyle(Style.EMPTY.
                        withClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, chapterPage.pageId().toString())).
                        withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.translatable("book.energizedpower.tooltip.page"))));
    }
    private MutableText backToTableOfContentComponent(String tableOfContentPageId) {
        return Text.translatable(tableOfContentPageTitles.get(tableOfContentPageId)).
                formatted(Formatting.DARK_GREEN, Formatting.UNDERLINE).fillStyle(Style.EMPTY.
                        withClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE,
                                EPAPI.MOD_ID + ":" + tableOfContentPageId)).
                        withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.translatable("book.energizedpower.tooltip.page"))));
    }

    private int nextChapterSortingNumber() {
        subChapterSortingNumber = 0;
        pageSortingNumber = 0;

        int i = chapterSortingNumber;
        chapterSortingNumber += 10;
        return i;
    }
    private int nextSubChapterSortingNumber() {
        pageSortingNumber = 0;

        int i = subChapterSortingNumber;
        subChapterSortingNumber += 10;
        return i;
    }
    private int nextPageSortingNumber() {
        int i = pageSortingNumber;
        pageSortingNumber += 10;
        return i;
    }

    private void chapterId(String chapter) {
        currentChapterIdPrefix = String.format("chapters/%04d_%s/", nextChapterSortingNumber(), chapter);
        currentSubChapterIdPrefix = currentChapterIdPrefix;
    }
    private void subChapterId(String chapter) {
        currentSubChapterIdPrefix = String.format("%s%04d_%s/", currentChapterIdPrefix, nextSubChapterSortingNumber(), chapter);
    }
    private String pageId(String page) {
        return String.format("%s%04d_%s", currentSubChapterIdPrefix, nextPageSortingNumber(), page);
    }
}
