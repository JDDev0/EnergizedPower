package me.jddev0.ep.datagen;

import com.mojang.datafixers.util.Pair;
import me.jddev0.ep.api.EPAPI;
import me.jddev0.ep.block.EPBlocks;
import me.jddev0.ep.datagen.generators.PageContent;
import me.jddev0.ep.datagen.generators.PageContentProvider;
import me.jddev0.ep.input.ModKeyBindings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModBookPageContentProvider extends PageContentProvider {
    private static final Style UNIFORM = Style.EMPTY.withFont(ResourceLocation.withDefaultNamespace("uniform"));
    private static final Style DEFAULT_FONT = Style.EMPTY.withFont(Style.DEFAULT_FONT);

    private int chapterSortingNumber = 0;
    private int subChapterSortingNumber = 0;
    private int pageSortingNumber = 0;

    private String currentChapterIdPrefix = "";
    private String currentSubChapterIdPrefix = "";

    private String mainTableOfContentPageId;
    private String energyBlocksTableOfContentPageId;
    private final Map<String, String> tableOfContentPageTitles = new HashMap<>();
    private final Map<String, List<Pair<String, PageContent>>> tableOfContentsEntries = new HashMap<>();

    public ModBookPageContentProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                      ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, EPAPI.MOD_ID, existingFileHelper);
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

        addSimplePage(pageId("welcome"), Component.empty().append(
                Component.translatable("book.energizedpower.page.welcome.title").
                        withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.welcome.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ).append(
                Component.literal("\n").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(addLinkToComponent(Component.translatable("book.energizedpower.page.welcome.2"),
                                "https://wiki.jddev0.com/books/energized-power/page/home"))
        ).append(
                Component.literal("\n").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.welcome.3")).append(": ").
                        append(addLinkToComponent(Component.translatable("book.energizedpower.page.welcome.3.link"),
                                "https://github.com/JDDev0/EnergizedPower/issues"))
        ).append(
                Component.literal("\n").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.welcome.4")).append(": ").
                        append(addLinkToComponent(Component.translatable("book.energizedpower.page.welcome.4.link"),
                                "https://discord.gg/sAKDNAU7yH"))
        ).append(
                Component.literal("\n").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.welcome.5")).append(": ").
                        append(addLinkToComponent(Component.translatable("book.energizedpower.page.welcome.5.link"),
                                "https://www.reddit.com/r/EnergizedPower/"))
        ));

        addSimplePage(pageId("credits"), Component.empty().append(
                Component.translatable("book.energizedpower.page.credits.title").
                        withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.credits.subtitle").
                        withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.literal("\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.credits.1")).append(": ").
                        append(addLinkToComponent(Component.literal("flashbulbs"),
                                "https://github.com/flashbulbs"))
        ).append(
                Component.literal("\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.credits.2")).append(": ").
                        append(addLinkToComponent(Component.literal("flashbulbs"),
                                "https://github.com/flashbulbs"))
        ).append(
                Component.literal("\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.credits.3")).append(": ").
                        append(addLinkToComponent(Component.literal("flashbulbs"),
                                "https://github.com/flashbulbs"))
        ).append(
                Component.literal("\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.credits.4")).append(": ").
                        append(addLinkToComponent(Component.literal("flashbulbs"),
                                "https://github.com/flashbulbs"))
        ).append(
                Component.literal("\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.credits.5")).append(": ").
                        append(addLinkToComponent(Component.literal("HanJiang-cn"),
                                "https://github.com/HanJiang-cn"))
        ).append(
                Component.literal("\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.credits.6")).append(": ").
                        append(addLinkToComponent(Component.literal("Roby1164"),
                                "https://github.com/Roby1164"))
        ).append(
                Component.literal("\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.credits.7")).append(": ").
                        append(addLinkToComponent(Component.literal("Eyyup"),
                                "https://github.com/msb-eyyup"))
        ).append(
                Component.literal("\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.credits.8")).append(": ").
                        append(Component.literal("Imperial Officer")).
                        append(", ").
                        append(addLinkToComponent(Component.literal("PlayboyX312"), "https://github.com/PlayboyX312"))
        ).append(
                Component.literal("\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                        append(Component.translatable("book.energizedpower.page.credits.9")).append(": ").
                        append(addLinkToComponent(Component.literal("Lucanoria"),
                                "https://github.com/Lucanoria"))
        ).append(
                    Component.literal("\n• ").
                            withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                            append(Component.translatable("book.energizedpower.page.credits.10")).append(": ").
                            append(Component.literal("Sebby"))
        ));

        mainTableOfContentPageId = pageId("table_of_contents");
        tableOfContentPageTitles.put(mainTableOfContentPageId, "book.energizedpower.page.table_of_contents.title");
        tableOfContentsEntries.put(mainTableOfContentPageId, new ArrayList<>());
    }
    private void registerResourcesChapter() {
        chapterId("resources");

        String resourcesChapterTitle = "book.energizedpower.page.chapter.resources.title";
        PageContent resourcesChapterPage = addChapterPage(pageId("resources_chapter"),
                Component.translatable(resourcesChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.resources.1")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ), EPAPI.id("textures/item/energized_copper_ingot.png"));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(resourcesChapterTitle, resourcesChapterPage));

        addSimplePage(pageId("cable_insulator"), Component.empty().append(
                Component.translatable("book.energizedpower.page.cable_insulator.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.cable_insulator.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.cable_insulator.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/cable_insulator.png"));

        addSimplePage(pageId("saw_blade"), Component.empty().append(
                Component.translatable("book.energizedpower.page.saw_blade").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/saw_blade.png"));

        addSimplePage(pageId("silicon"), Component.empty().append(
                Component.translatable("book.energizedpower.page.silicon.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.silicon.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/silicon.png"));

        addSimplePage(pageId("sawdust"), Component.empty().append(
                Component.translatable("book.energizedpower.page.sawdust.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.sawdust.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/sawdust.png"));

        addSimplePage(pageId("fertilizers"), Component.empty().append(
                Component.translatable("book.energizedpower.page.fertilizers").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/basic_fertilizer.png"),
                EPAPI.id("textures/item/good_fertilizer.png"),
                EPAPI.id("textures/item/advanced_fertilizer.png")
        });

        addSimplePage(pageId("ore_dusts"), Component.empty().append(
                Component.translatable("book.energizedpower.page.ore_dusts").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/tin_dust.png"),
                EPAPI.id("textures/item/copper_dust.png"),
                EPAPI.id("textures/item/iron_dust.png"),
                EPAPI.id("textures/item/gold_dust.png")
        });

        addSimplePage(pageId("charcoal_dust"), Component.empty().append(
                Component.translatable("book.energizedpower.page.charcoal_dust").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/charcoal_dust.png"));

        addSimplePage(pageId("charcoal_filter"), Component.empty().append(
                Component.translatable("book.energizedpower.page.charcoal_filter").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/charcoal_filter.png"));

        addSimplePage(pageId("plates"), Component.empty().append(
                Component.translatable("book.energizedpower.page.plates.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.plates.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/tin_plate.png"),
                EPAPI.id("textures/item/copper_plate.png"),
                EPAPI.id("textures/item/iron_plate.png"),
                EPAPI.id("textures/item/gold_plate.png"),
                EPAPI.id("textures/item/advanced_alloy_plate.png"),
                EPAPI.id("textures/item/energized_copper_plate.png"),
                EPAPI.id("textures/item/energized_gold_plate.png")
        });

        addSimplePage(pageId("alloys"), Component.empty().append(
                Component.translatable("book.energizedpower.page.alloys").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/steel_ingot.png"),
                EPAPI.id("textures/item/redstone_alloy_ingot.png"),
                EPAPI.id("textures/item/advanced_alloy_ingot.png")
        });

        addSimplePage(pageId("metal_products"), Component.empty().append(
                Component.translatable("book.energizedpower.page.metal_products.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.metal_products.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/iron_gear.png"),
                EPAPI.id("textures/item/iron_rod.png"),
                EPAPI.id("textures/item/tin_wire.png"),
                EPAPI.id("textures/item/copper_wire.png"),
                EPAPI.id("textures/item/gold_wire.png"),
                EPAPI.id("textures/item/energized_copper_wire.png"),
                EPAPI.id("textures/item/energized_gold_wire.png")
        });

        addSimplePage(pageId("circuits"), Component.empty().append(
                Component.translatable("book.energizedpower.page.circuits").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/basic_circuit.png"),
                EPAPI.id("textures/item/advanced_circuit.png"),
                EPAPI.id("textures/item/processing_unit.png"),
                EPAPI.id("textures/item/teleporter_processing_unit.png")
        });

        addSimplePage(pageId("energized_copper_ingot"), Component.empty().append(
                Component.translatable("book.energizedpower.page.energized_copper_ingot.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.energized_copper_ingot.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.energized_copper_ingot.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ).append(
                Component.translatable("book.energizedpower.page.energized_copper_ingot.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/energized_copper_ingot.png"));

        addSimplePage(pageId("energized_gold_ingot"), Component.empty().append(
                Component.translatable("book.energizedpower.page.energized_gold_ingot").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/energized_gold_ingot.png"));

        addSimplePage(pageId("energized_crystal_matrix"), Component.empty().append(
                Component.translatable("book.energizedpower.page.energized_crystal_matrix").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/energized_crystal_matrix.png"));
    }
    private void registerUpgradesChapter() {
        chapterId("upgrades");

        String upgradesChapterTitle = "book.energizedpower.page.chapter.upgrades.title";
        PageContent upgradesChapterPage = addChapterPage(pageId("upgrades_chapter"),
                Component.translatable(upgradesChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.upgrades")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ), EPAPI.id("textures/item/speed_upgrade_module_1.png"));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(upgradesChapterTitle, upgradesChapterPage));

        addSimplePage(pageId("speed_upgrades"), Component.empty().append(
                Component.translatable("book.energizedpower.page.speed_upgrades").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/speed_upgrade_module_1.png"),
                EPAPI.id("textures/item/speed_upgrade_module_2.png"),
                EPAPI.id("textures/item/speed_upgrade_module_3.png"),
                EPAPI.id("textures/item/speed_upgrade_module_4.png"),
                EPAPI.id("textures/item/speed_upgrade_module_5.png")
        });

        addSimplePage(pageId("energy_efficiency_upgrades"), Component.empty().append(
                Component.translatable("book.energizedpower.page.energy_efficiency_upgrades").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/energy_efficiency_upgrade_module_1.png"),
                EPAPI.id("textures/item/energy_efficiency_upgrade_module_2.png"),
                EPAPI.id("textures/item/energy_efficiency_upgrade_module_3.png"),
                EPAPI.id("textures/item/energy_efficiency_upgrade_module_4.png"),
                EPAPI.id("textures/item/energy_efficiency_upgrade_module_5.png")
        });

        addSimplePage(pageId("energy_capacity_upgrades"), Component.empty().append(
                Component.translatable("book.energizedpower.page.energy_capacity_upgrades").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/energy_capacity_upgrade_module_1.png"),
                EPAPI.id("textures/item/energy_capacity_upgrade_module_2.png"),
                EPAPI.id("textures/item/energy_capacity_upgrade_module_3.png"),
                EPAPI.id("textures/item/energy_capacity_upgrade_module_4.png"),
                EPAPI.id("textures/item/energy_capacity_upgrade_module_5.png")
        });

        addSimplePage(pageId("duration_upgrades"), Component.empty().append(
                Component.translatable("book.energizedpower.page.duration_upgrades.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.duration_upgrades.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/duration_upgrade_module_1.png"),
                EPAPI.id("textures/item/duration_upgrade_module_2.png"),
                EPAPI.id("textures/item/duration_upgrade_module_3.png"),
                EPAPI.id("textures/item/duration_upgrade_module_4.png"),
                EPAPI.id("textures/item/duration_upgrade_module_5.png"),
                EPAPI.id("textures/item/duration_upgrade_module_6.png")
        });

        addSimplePage(pageId("range_upgrades"), Component.empty().append(
                Component.translatable("book.energizedpower.page.range_upgrades").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/range_upgrade_module_1.png"),
                EPAPI.id("textures/item/range_upgrade_module_2.png"),
                EPAPI.id("textures/item/range_upgrade_module_3.png")
        });

        addSimplePage(pageId("extraction_depth_upgrades"), Component.empty().append(
                Component.translatable("book.energizedpower.page.extraction_depth_upgrades").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/extraction_depth_upgrade_module_1.png"),
                EPAPI.id("textures/item/extraction_depth_upgrade_module_2.png"),
                EPAPI.id("textures/item/extraction_depth_upgrade_module_3.png"),
                EPAPI.id("textures/item/extraction_depth_upgrade_module_4.png"),
                EPAPI.id("textures/item/extraction_depth_upgrade_module_5.png")
        });

        addSimplePage(pageId("extraction_range_upgrades"), Component.empty().append(
                Component.translatable("book.energizedpower.page.extraction_range_upgrades").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/extraction_range_upgrade_module_1.png"),
                EPAPI.id("textures/item/extraction_range_upgrade_module_2.png"),
                EPAPI.id("textures/item/extraction_range_upgrade_module_3.png"),
                EPAPI.id("textures/item/extraction_range_upgrade_module_4.png"),
                EPAPI.id("textures/item/extraction_range_upgrade_module_5.png")
        });

        addSimplePage(pageId("furnace_mode_upgrades"), Component.empty().append(
                Component.translatable("book.energizedpower.page.furnace_mode_upgrades").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/blast_furnace_upgrade_module.png"),
                EPAPI.id("textures/item/smoker_upgrade_module.png")
        });

        addSimplePage(pageId("moon_light_upgrades"), Component.empty().append(
                Component.translatable("book.energizedpower.page.moon_light_upgrades").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/moon_light_upgrade_module_1.png"),
                EPAPI.id("textures/item/moon_light_upgrade_module_2.png"),
                EPAPI.id("textures/item/moon_light_upgrade_module_3.png")
        });
    }
    private void registerToolsChapter() {
        chapterId("tools");

        String toolsChapterTitle = "book.energizedpower.page.chapter.tools.title";
        PageContent toolsChapterPage = addChapterPage(pageId("tools_chapter"),
                Component.translatable(toolsChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.tools")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ), EPAPI.id("textures/item/stone_hammer.png"));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(toolsChapterTitle, toolsChapterPage));

        addSimplePage(pageId("hammers"), Component.empty().append(
                Component.translatable("book.energizedpower.page.hammers").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/wooden_hammer.png"),
                EPAPI.id("textures/item/stone_hammer.png"),
                EPAPI.id("textures/item/iron_hammer.png"),
                EPAPI.id("textures/item/golden_hammer.png"),
                EPAPI.id("textures/item/diamond_hammer.png"),
                EPAPI.id("textures/item/netherite_hammer.png")
        });

        addSimplePage(pageId("cutters"), Component.empty().append(
                Component.translatable("book.energizedpower.page.cutters.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.cutters.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/cutter.png"));

        addSimplePage(pageId("wrench"), Component.empty().append(
                Component.translatable("book.energizedpower.page.wrench").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/wrench.png"));
    }
    private void registerWorkbenchesChapter() {
        chapterId("workbenches");

        String workbenchesChapterTitle = "book.energizedpower.page.chapter.workbenches.title";
        PageContent workbenchesChapterPage = addChapterPage(pageId("workbenches_chapter"),
                Component.translatable(workbenchesChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.workbenches")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ), Blocks.CRAFTING_TABLE);
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(workbenchesChapterTitle, workbenchesChapterPage));

        addSimplePage(pageId("press_mold_maker"), Component.empty().append(
                Component.translatable("book.energizedpower.page.press_mold_maker").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.PRESS_MOLD_MAKER.get());

        addSimplePage(pageId("alloy_furnace"), Component.empty().append(
                Component.translatable("book.energizedpower.page.alloy_furnace.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.alloy_furnace.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.alloy_furnace.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.ALLOY_FURNACE.get());
    }
    private void registerEnergyItemsChapter() {
        chapterId("energy_items");

        String energyItemsChapterTitle = "book.energizedpower.page.chapter.energy_items.title";
        PageContent energyItemsChapterPage = addChapterPage(pageId("energy_items_chapter"),
                Component.translatable(energyItemsChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.energy_items")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(energyItemsChapterTitle, energyItemsChapterPage));

        addSimplePage(pageId("inventory_coal_engine"), Component.empty().append(
                Component.translatable("book.energizedpower.page.inventory_coal_engine.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.inventory_coal_engine.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.inventory_coal_engine.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ).append(
                Component.translatable("book.energizedpower.page.inventory_coal_engine.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/inventory_coal_engine_on.png"));

        addSimplePage(pageId("inventory_charger"), Component.empty().append(
                Component.translatable("book.energizedpower.page.inventory_charger.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.inventory_charger.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/inventory_charger.png"));

        addSimplePage(pageId("inventory_teleporter"), Component.empty().append(
                Component.translatable("book.energizedpower.page.inventory_teleporter").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/inventory_teleporter.png"));

        addSimplePage(pageId("energy_analyzer"), Component.empty().append(
                Component.translatable("book.energizedpower.page.energy_analyzer.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.energy_analyzer.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/energy_analyzer.png"));

        addSimplePage(pageId("fluid_analyzer"), Component.empty().append(
                Component.translatable("book.energizedpower.page.fluid_analyzer.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.fluid_analyzer.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/fluid_analyzer.png"));

        addSimplePage(pageId("batteries"), Component.empty().append(
                Component.translatable("book.energizedpower.page.batteries.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.batteries.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
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
                Component.translatable(energyBlocksMainChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.energy_blocks")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
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
                Component.translatable(energyBlocksGeneralChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.energy_blocks.general")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(energyBlocksTableOfContentPageId))
                ));
        tableOfContentsEntries.get(energyBlocksTableOfContentPageId).add(Pair.of(energyBlocksGeneralChapterTitle, energyBlocksGeneralChapterPage));

        addSimplePage(pageId("machine_frames"), Component.empty().append(
                Component.translatable("book.energizedpower.page.machine_frames.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.machine_frames.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_MACHINE_FRAME.get(),
                EPBlocks.HARDENED_MACHINE_FRAME.get(),
                EPBlocks.ADVANCED_MACHINE_FRAME.get(),
                EPBlocks.REINFORCED_ADVANCED_MACHINE_FRAME.get()
        });
    }
    private void registerEnergyBlocksChapterEnergyTransportationSubChapter() {
        subChapterId("energy_transportation");

        String energyBlocksEnergyTransportationChapterTitle = "book.energizedpower.page.chapter.energy_blocks.energy_transportation.title";
        PageContent energyBlocksEnergyTransportationChapterPage = addChapterPage(pageId("energy_transportation_chapter"),
                Component.translatable(energyBlocksEnergyTransportationChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.energy_blocks.energy_transportation")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(energyBlocksTableOfContentPageId))
                ));
        tableOfContentsEntries.get(energyBlocksTableOfContentPageId).add(Pair.of(energyBlocksEnergyTransportationChapterTitle, energyBlocksEnergyTransportationChapterPage));

        addSimplePage(pageId("cables"), Component.empty().append(
                Component.translatable("book.energizedpower.page.cables.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.cables.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.cables.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.TIN_CABLE.get(),
                EPBlocks.COPPER_CABLE.get(),
                EPBlocks.GOLD_CABLE.get(),
                EPBlocks.ENERGIZED_COPPER_CABLE.get(),
                EPBlocks.ENERGIZED_GOLD_CABLE.get(),
                EPBlocks.ENERGIZED_CRYSTAL_MATRIX_CABLE.get()
        });

        addSimplePage(pageId("transformers"), Component.empty().append(
                Component.translatable("book.energizedpower.page.transformers.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.transformers.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.transformers.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.transformers.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.transformers.5").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.transformers.6").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.transformers.7").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.transformers.8").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.transformers.9").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.LV_TRANSFORMER_1_TO_N.get(),
                EPBlocks.LV_TRANSFORMER_3_TO_3.get(),
                EPBlocks.LV_TRANSFORMER_N_TO_1.get(),
                EPBlocks.CONFIGURABLE_LV_TRANSFORMER.get(),
                EPBlocks.MV_TRANSFORMER_1_TO_N.get(),
                EPBlocks.MV_TRANSFORMER_3_TO_3.get(),
                EPBlocks.MV_TRANSFORMER_N_TO_1.get(),
                EPBlocks.CONFIGURABLE_MV_TRANSFORMER.get(),
                EPBlocks.HV_TRANSFORMER_1_TO_N.get(),
                EPBlocks.HV_TRANSFORMER_3_TO_3.get(),
                EPBlocks.HV_TRANSFORMER_N_TO_1.get(),
                EPBlocks.CONFIGURABLE_HV_TRANSFORMER.get(),
                EPBlocks.EHV_TRANSFORMER_1_TO_N.get(),
                EPBlocks.EHV_TRANSFORMER_3_TO_3.get(),
                EPBlocks.EHV_TRANSFORMER_N_TO_1.get(),
                EPBlocks.CONFIGURABLE_EHV_TRANSFORMER.get()
        });

        addSimplePage(pageId("minecart_charger_uncharger"), Component.empty().append(
                Component.translatable("book.energizedpower.page.minecart_charger_uncharger.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.minecart_charger_uncharger.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.minecart_charger_uncharger.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.MINECART_CHARGER.get(),
                EPBlocks.MINECART_UNCHARGER.get(),
                EPBlocks.ADVANCED_MINECART_CHARGER.get(),
                EPBlocks.ADVANCED_MINECART_UNCHARGER.get()
        });
    }
    private void registerEnergyBlocksChapterEnergyStorageSubChapter() {
        subChapterId("energy_storage");

        String energyBlocksEnergyStorageChapterTitle = "book.energizedpower.page.chapter.energy_blocks.energy_storage.title";
        PageContent energyBlocksEnergyStorageChapterPage = addChapterPage(pageId("energy_storage_chapter"),
                Component.translatable(energyBlocksEnergyStorageChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.energy_blocks.energy_storage")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(energyBlocksTableOfContentPageId))
                ));
        tableOfContentsEntries.get(energyBlocksTableOfContentPageId).add(Pair.of(energyBlocksEnergyStorageChapterTitle, energyBlocksEnergyStorageChapterPage));

        addSimplePage(pageId("battery_box"), Component.empty().append(
                Component.translatable("book.energizedpower.page.battery_box").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BATTERY_BOX.get(),
                EPBlocks.ADVANCED_BATTERY_BOX.get()
        });
    }
    private void registerEnergyBlocksChapterEnergyProductionSubChapter() {
        subChapterId("energy_production");

        String energyBlocksEnergyProductionChapterTitle = "book.energizedpower.page.chapter.energy_blocks.energy_production.title";
        PageContent energyBlocksEnergyProductionChapterPage = addChapterPage(pageId("energy_production_chapter"),
                Component.translatable(energyBlocksEnergyProductionChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.energy_blocks.energy_production")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(energyBlocksTableOfContentPageId))
                ));
        tableOfContentsEntries.get(energyBlocksTableOfContentPageId).add(Pair.of(energyBlocksEnergyProductionChapterTitle, energyBlocksEnergyProductionChapterPage));

        addSimplePage(pageId("solar_cells"), Component.empty().append(
                Component.translatable("book.energizedpower.page.solar_cells.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.solar_cells.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/basic_solar_cell.png"),
                EPAPI.id("textures/item/advanced_solar_cell.png"),
                EPAPI.id("textures/item/reinforced_advanced_solar_cell.png")
        });

        addSimplePage(pageId("solar_panels"), Component.empty().append(
                Component.translatable("book.energizedpower.page.solar_panels.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.solar_panels.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.SOLAR_PANEL_1.get(),
                EPBlocks.SOLAR_PANEL_2.get(),
                EPBlocks.SOLAR_PANEL_3.get(),
                EPBlocks.SOLAR_PANEL_4.get(),
                EPBlocks.SOLAR_PANEL_5.get(),
                EPBlocks.SOLAR_PANEL_6.get()
        });

        addSimplePage(pageId("coal_engine"), Component.empty().append(
                Component.translatable("book.energizedpower.page.coal_engine.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.coal_engine.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.COAL_ENGINE.get());

        addSimplePage(pageId("heat_generator"), Component.empty().append(
                Component.translatable("book.energizedpower.page.heat_generator.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.heat_generator.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.heat_generator.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.heat_generator.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.HEAT_GENERATOR.get());

        addSimplePage(pageId("thermal_generator"), Component.empty().append(
                Component.translatable("book.energizedpower.page.thermal_generator.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.thermal_generator.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.THERMAL_GENERATOR.get());

        addSimplePage(pageId("lightning_generator"), Component.empty().append(
                Component.translatable("book.energizedpower.page.lightning_generator.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.lightning_generator.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.LIGHTNING_GENERATOR.get());
    }
    private void registerEnergyBlocksChapterEnergyConsumptionSubChapter() {
        subChapterId("energy_consumption");

        String energyBlocksEnergyConsumptionChapterTitle = "book.energizedpower.page.chapter.energy_blocks.energy_consumption.title";
        PageContent energyBlocksEnergyConsumptionChapterPage = addChapterPage(pageId("energy_consumption_chapter"),
                Component.translatable(energyBlocksEnergyConsumptionChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.energy_blocks.energy_consumption")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(energyBlocksTableOfContentPageId))
                ));
        tableOfContentsEntries.get(energyBlocksTableOfContentPageId).add(Pair.of(energyBlocksEnergyConsumptionChapterTitle, energyBlocksEnergyConsumptionChapterPage));

        addSimplePage(pageId("powered_lamp"), Component.empty().append(
                Component.translatable("book.energizedpower.page.powered_lamp").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.POWERED_LAMP.get());

        addSimplePage(pageId("powered_furnaces"), Component.empty().append(
                Component.translatable("book.energizedpower.page.powered_furnaces.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.powered_furnaces.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.POWERED_FURNACE.get(),
                EPBlocks.ADVANCED_POWERED_FURNACE.get()
        });

        addSimplePage(pageId("auto_crafters"), Component.empty().append(
                Component.translatable("book.energizedpower.page.auto_crafters.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.auto_crafters.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.auto_crafters.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.auto_crafters.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.AUTO_CRAFTER.get(),
                EPBlocks.ADVANCED_AUTO_CRAFTER.get()
        });

        addSimplePage(pageId("crushers"), Component.empty().append(
                Component.translatable("book.energizedpower.page.crushers.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.crushers.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.CRUSHER.get(),
                EPBlocks.ADVANCED_CRUSHER.get()
        });

        addSimplePage(pageId("pulverizers"), Component.empty().append(
                Component.translatable("book.energizedpower.page.pulverizers.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.pulverizers.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.PULVERIZER.get(),
                EPBlocks.ADVANCED_PULVERIZER.get()
        });

        addSimplePage(pageId("sawmill"), Component.empty().append(
                Component.translatable("book.energizedpower.page.sawmill.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.sawmill.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.SAWMILL.get());

        addSimplePage(pageId("compressor"), Component.empty().append(
                Component.translatable("book.energizedpower.page.compressor").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.COMPRESSOR.get());

        addSimplePage(pageId("metal_press"), Component.empty().append(
                Component.translatable("book.energizedpower.page.metal_press.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.metal_press.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.metal_press.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.metal_press.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.METAL_PRESS.get());

        addSimplePage(pageId("auto_press_mold_maker"), Component.empty().append(
                Component.translatable("book.energizedpower.page.auto_press_mold_maker").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.AUTO_PRESS_MOLD_MAKER.get());

        addSimplePage(pageId("auto_stonecutter"), Component.empty().append(
                Component.translatable("book.energizedpower.page.auto_stonecutter").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.AUTO_STONECUTTER.get());

        addSimplePage(pageId("assembling_machine"), Component.empty().append(
                Component.translatable("book.energizedpower.page.assembling_machine.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.assembling_machine.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.assembling_machine.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.ASSEMBLING_MACHINE.get());

        addSimplePage(pageId("plant_growth_chamber"), Component.empty().append(
                Component.translatable("book.energizedpower.page.plant_growth_chamber.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.plant_growth_chamber.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.plant_growth_chamber.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.plant_growth_chamber.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.plant_growth_chamber.5").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.plant_growth_chamber.6").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.plant_growth_chamber.7").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.PLANT_GROWTH_CHAMBER.get());

        addSimplePage(pageId("stone_liquefier"), Component.empty().append(
                Component.translatable("book.energizedpower.page.stone_liquefier.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.stone_liquefier.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.STONE_LIQUEFIER.get());

        addSimplePage(pageId("stone_solidifier"), Component.empty().append(
                Component.translatable("book.energizedpower.page.stone_solidifier.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.stone_solidifier.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.STONE_SOLIDIFIER.get());

        addSimplePage(pageId("filtration_plant"), Component.empty().append(
                Component.translatable("book.energizedpower.page.filtration_plant.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.filtration_plant.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.FILTRATION_PLANT.get());

        addSimplePage(pageId("fluid_transposer"), Component.empty().append(
                Component.translatable("book.energizedpower.page.fluid_transposer").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.FLUID_TRANSPOSER.get());

        addSimplePage(pageId("induction_smelter"), Component.empty().append(
                Component.translatable("book.energizedpower.page.induction_smelter.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.induction_smelter.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.induction_smelter.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.INDUCTION_SMELTER.get());

        addSimplePage(pageId("block_placer"), Component.empty().append(
                Component.translatable("book.energizedpower.page.block_placer.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.block_placer.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.BLOCK_PLACER.get());

        addSimplePage(pageId("fluid_filler_fluid_drainer"), Component.empty().append(
                Component.translatable("book.energizedpower.page.fluid_filler_fluid_drainer").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.FLUID_FILLER.get(),
                EPBlocks.FLUID_DRAINER.get()
        });

        addSimplePage(pageId("fluid_pumps"), Component.empty().append(
                Component.translatable("book.energizedpower.page.fluid_pumps").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.FLUID_PUMP.get(),
                EPBlocks.ADVANCED_FLUID_PUMP.get()
        });

        addSimplePage(pageId("charger_uncharger"), Component.empty().append(
                Component.translatable("book.energizedpower.page.charger_uncharger.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.charger_uncharger.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.CHARGER.get(),
                EPBlocks.UNCHARGER.get(),
                EPBlocks.ADVANCED_CHARGER.get(),
                EPBlocks.ADVANCED_UNCHARGER.get()
        });

        addSimplePage(pageId("charging_station"), Component.empty().append(
                Component.translatable("book.energizedpower.page.charging_station").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.CHARGING_STATION.get());

        addSimplePage(pageId("crystal_growth_chamber"), Component.empty().append(
                Component.translatable("book.energizedpower.page.crystal_growth_chamber").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.CRYSTAL_GROWTH_CHAMBER.get());

        addSimplePage(pageId("energizer"), Component.empty().append(
                Component.translatable("book.energizedpower.page.energizer").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.ENERGIZER.get());

        addSimplePage(pageId("weather_controller"), Component.empty().append(
                Component.translatable("book.energizedpower.page.weather_controller").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.WEATHER_CONTROLLER.get());

        addSimplePage(pageId("time_controller"), Component.empty().append(
                Component.translatable("book.energizedpower.page.time_controller.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.time_controller.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPBlocks.TIME_CONTROLLER.get());

        addSimplePage(pageId("teleporter_matrix"), Component.empty().append(
                Component.translatable("book.energizedpower.page.teleporter_matrix.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.teleporter_matrix.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/teleporter_matrix.png"));

        addSimplePage(pageId("teleporter"), Component.empty().append(
                Component.translatable("book.energizedpower.page.teleporter.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.teleporter.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.teleporter.3.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append(
                                Component.literal(" [").withStyle(ChatFormatting.BLACK).withStyle(DEFAULT_FONT)
                        ).append(
                                Component.keybind(ModKeyBindings.KEY_TELEPORTER_USE).withStyle(ChatFormatting.DARK_GRAY).
                                        withStyle(DEFAULT_FONT)
                        ).append(
                                Component.literal("] ").withStyle(ChatFormatting.BLACK).withStyle(DEFAULT_FONT)
                        ).append(
                                Component.translatable("book.energizedpower.page.teleporter.3.2")
                        )
        ), EPBlocks.TELEPORTER.get());
    }

    private void registerFluidBlocksChapter() {
        chapterId("fluid_blocks");

        String fluidBlocksChapterTitle = "book.energizedpower.page.chapter.fluid_blocks.title";
        PageContent fluidBlocksChapterPage = addChapterPage(pageId("fluid_blocks_chapter"),
                Component.translatable(fluidBlocksChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.fluid_blocks")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(fluidBlocksChapterTitle, fluidBlocksChapterPage));

        addSimplePage(pageId("fluid_pipes"), Component.empty().append(
                Component.translatable("book.energizedpower.page.fluid_pipes.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.fluid_pipes.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.fluid_pipes.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.fluid_pipes.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.IRON_FLUID_PIPE.get(),
                EPBlocks.GOLDEN_FLUID_PIPE.get()
        });

        addSimplePage(pageId("fluid_tanks"), Component.empty().append(
                Component.translatable("book.energizedpower.page.fluid_tanks.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.fluid_tanks.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.FLUID_TANK_SMALL.get(),
                EPBlocks.FLUID_TANK_MEDIUM.get(),
                EPBlocks.FLUID_TANK_LARGE.get()
        });

        addSimplePage(pageId("drain"), Component.empty().append(
                Component.translatable("book.energizedpower.page.drain.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.drain.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPBlocks.DRAIN.get());
    }
    private void registerItemTransportationChapter() {
        chapterId("item_transportation");

        String itemTransportationChapterTitle = "book.energizedpower.page.chapter.item_transportation.title";
        PageContent itemTransportationChapterPage = addChapterPage(pageId("item_transportation_chapter"),
                Component.translatable(itemTransportationChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.item_transportation")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(itemTransportationChapterTitle, itemTransportationChapterPage));

        addSimplePage(pageId("item_conveyor_belt"), Component.empty().append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt.5").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt.6").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT.get(),
                EPBlocks.FAST_ITEM_CONVEYOR_BELT.get(),
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT.get(),
        });

        addSimplePage(pageId("item_conveyor_belt_loader"), Component.empty().append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_loader.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_loader.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_loader.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_loader.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_loader.5").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_LOADER.get(),
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_LOADER.get(),
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_LOADER.get(),
        });

        addSimplePage(pageId("item_conveyor_belt_sorter"), Component.empty().append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_sorter.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_sorter.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_sorter.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_sorter.4").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_sorter.5").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_sorter.6").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_sorter.7").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_sorter.8").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_sorter.9").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SORTER.get(),
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_SORTER.get(),
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SORTER.get(),
        });

        addSimplePage(pageId("item_conveyor_belt_switch"), Component.empty().append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_switch.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_switch.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SWITCH.get(),
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_SWITCH.get(),
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SWITCH.get(),
        });

        addSimplePage(pageId("item_conveyor_belt_splitter"), Component.empty().append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_splitter").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_SPLITTER.get(),
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_SPLITTER.get(),
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_SPLITTER.get(),
        });

        addSimplePage(pageId("item_conveyor_belt_merger"), Component.empty().append(
                Component.translatable("book.energizedpower.page.item_conveyor_belt_merger").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.BASIC_ITEM_CONVEYOR_BELT_MERGER.get(),
                EPBlocks.FAST_ITEM_CONVEYOR_BELT_MERGER.get(),
                EPBlocks.EXPRESS_ITEM_CONVEYOR_BELT_MERGER.get(),
        });

        addSimplePage(pageId("item_silos"), Component.empty().append(
                Component.translatable("book.energizedpower.page.item_silos.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(" ")
        ).append(
                Component.translatable("book.energizedpower.page.item_silos.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.item_silos.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.ITALIC).withStyle(UNIFORM)
        ), new Block[] {
                EPBlocks.ITEM_SILO_TINY.get(),
                EPBlocks.ITEM_SILO_SMALL.get(),
                EPBlocks.ITEM_SILO_MEDIUM.get(),
                EPBlocks.ITEM_SILO_LARGE.get(),
                EPBlocks.ITEM_SILO_GIANT.get(),
        });
    }
    private void registerFluidsChapter() {
        chapterId("fluids");

        String fluidsChapterTitle = "book.energizedpower.page.chapter.fluids.title";
        PageContent fluidsChapterPage = addChapterPage(pageId("fluids_chapter"),
                Component.translatable(fluidsChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.fluids")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(fluidsChapterTitle, fluidsChapterPage));

        addSimplePage(pageId("dirty_water"), Component.empty().append(
                Component.translatable("book.energizedpower.page.dirty_water.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n")
        ).append(
                Component.translatable("book.energizedpower.page.dirty_water.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/item/dirty_water_bucket.png"));
    }
    private void registerMachineConfigurationChapter() {
        chapterId("machine_configuration");

        String machineConfigurationChapterTitle = "book.energizedpower.page.chapter.machine_configuration.title";
        PageContent machineConfigurationChapterPage = addChapterPage(pageId("machine_configuration_chapter"),
                Component.translatable(machineConfigurationChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.machine_configuration")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(machineConfigurationChapterTitle, machineConfigurationChapterPage));

        addSimplePage(pageId("upgrade_configuration"), Component.empty().append(
                Component.translatable("book.energizedpower.page.upgrade_configuration").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), EPAPI.id("textures/gui/book_icons/upgrade_view.png"));

        addSimplePage(pageId("redstone_mode_configuration"), Component.empty().append(
                Component.translatable("book.energizedpower.page.redstone_mode_configuration.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ).append(
                Component.literal("\n\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(
                                Component.translatable("book.energizedpower.page.redstone_mode_configuration.2").
                                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
                        )
        ).append(
                Component.literal("\n\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(
                                Component.translatable("book.energizedpower.page.redstone_mode_configuration.3").
                                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
                        )
        ).append(
                Component.literal("\n\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(
                                Component.translatable("book.energizedpower.page.redstone_mode_configuration.4").
                                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
                        )
        ), new ResourceLocation[] {
                EPAPI.id("textures/gui/book_icons/redstone_mode_ignore.png"),
                EPAPI.id("textures/gui/book_icons/redstone_mode_high.png"),
                EPAPI.id("textures/gui/book_icons/redstone_mode_low.png")
        });

        addSimplePage(pageId("comparator_mode_configuration"), Component.empty().append(
                Component.translatable("book.energizedpower.page.comparator_mode_configuration.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ).append(
                Component.literal("\n\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(
                                Component.translatable("book.energizedpower.page.comparator_mode_configuration.2").
                                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
                        )
        ).append(
                Component.literal("\n\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(
                                Component.translatable("book.energizedpower.page.comparator_mode_configuration.3").
                                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
                        )
        ).append(
                Component.literal("\n\n• ").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(
                                Component.translatable("book.energizedpower.page.comparator_mode_configuration.4").
                                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
                        )
        ), new ResourceLocation[] {
                EPAPI.id("textures/gui/book_icons/comparator_mode_item.png"),
                EPAPI.id("textures/gui/book_icons/comparator_mode_fluid.png"),
                EPAPI.id("textures/gui/book_icons/comparator_mode_energy.png")
        });
    }
    private void registerEntitiesChapter() {
        chapterId("entities");

        String entitiesChapterTitle = "book.energizedpower.page.chapter.entities.title";
        PageContent entitiesChapterPage = addChapterPage(pageId("entities_chapter"),
                Component.translatable(entitiesChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.entities")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ));
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(entitiesChapterTitle, entitiesChapterPage));

        addSimplePage(pageId("battery_box_minecarts"), Component.empty().append(
                Component.translatable("book.energizedpower.page.battery_box_minecarts").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ), new ResourceLocation[] {
                EPAPI.id("textures/item/battery_box_minecart.png"),
                EPAPI.id("textures/item/advanced_battery_box_minecart.png")
        });
    }
    private void registerStructuresChapter() {
        chapterId("structures");

        String structuresChapterTitle = "book.energizedpower.page.chapter.structures.title";
        PageContent structuresChapterPage = addChapterPage(pageId("structures_chapter"),
                Component.translatable(structuresChapterTitle).
                        withStyle(ChatFormatting.GOLD),
                Component.empty().append(
                        Component.translatable("book.energizedpower.page.chapter.structures")
                ).append(
                        Component.literal("\n\n").
                                withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).
                                append(backToTableOfContentComponent(mainTableOfContentPageId))
                ), EPBlocks.BASIC_MACHINE_FRAME.get());
        tableOfContentsEntries.get(mainTableOfContentPageId).add(Pair.of(structuresChapterTitle, structuresChapterPage));

        addSimplePage(pageId("electrician"), Component.empty().append(
                Component.translatable("book.energizedpower.page.electrician.title").
                        withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.electrician.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.electrician.2").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.electrician.3").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ));

        addSimplePage(pageId("factory"), Component.empty().append(
                Component.translatable("book.energizedpower.page.factory.title").
                        withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.factory.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ));

        addSimplePage(pageId("small_solar_farm"), Component.empty().append(
                Component.translatable("book.energizedpower.page.small_solar_farm.title").
                        withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD).append("\n\n")
        ).append(
                Component.translatable("book.energizedpower.page.small_solar_farm.1").
                        withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM)
        ));
    }

    private void registerTableOfContentsPages() {
        tableOfContentsEntries.forEach((pageId, entries) -> {
            MutableComponent content = Component.translatable(tableOfContentPageTitles.get(pageId)).
                    withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);

            entries.forEach(entry -> content.append(Component.literal("\n\n• ").
                    withStyle(ChatFormatting.BLACK, ChatFormatting.BOLD).withStyle(UNIFORM).append(
                            tableOfContentEntryComponent(entry.getFirst(), entry.getSecond())
                    )));

            addSimplePage(pageId, content);
        });
    }

    private MutableComponent tableOfContentEntryComponent(String chapterTitle, PageContent chapterPage) {
        return Component.translatable(chapterTitle).withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.UNDERLINE).
                withStyle(Style.EMPTY.
                        withClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, chapterPage.pageId().toString())).
                        withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("book.energizedpower.tooltip.page"))));
    }
    private MutableComponent backToTableOfContentComponent(String tableOfContentPageId) {
        return Component.translatable(tableOfContentPageTitles.get(tableOfContentPageId)).
                withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.UNDERLINE).withStyle(Style.EMPTY.
                        withClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE,
                                EPAPI.MOD_ID + ":" + tableOfContentPageId)).
                        withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("book.energizedpower.tooltip.page"))));
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
