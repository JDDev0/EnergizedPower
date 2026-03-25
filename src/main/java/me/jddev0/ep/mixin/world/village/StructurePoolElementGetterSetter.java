package me.jddev0.ep.mixin.world.village;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

@Mixin(StructureTemplatePool.class)
public interface StructurePoolElementGetterSetter {
    @Accessor("templates")
    ObjectArrayList<StructurePoolElement> getElements();

    @Accessor("rawTemplates")
    List<Pair<StructurePoolElement, Integer>> getElementCounts();

    @Accessor("rawTemplates")
    @Mutable
    void setElementCounts(List<Pair<StructurePoolElement, Integer>> elementCounts);
}
