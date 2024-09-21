package me.jddev0.ep.datagen.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Supplier;

public record ItemWithOverridesModelSupplier(
        Identifier textureId,
        List<ItemPredicateOverrides> overridesPredicates
) implements Supplier<JsonElement> {
    @Override
    public JsonElement get() {
        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("parent", new Identifier("item/generated").toString());

        {
            JsonObject texturesJson = new JsonObject();

            texturesJson.addProperty("layer0", textureId.toString());

            modelJson.add("textures", texturesJson);
        }

        {
            JsonArray overridesJson = new JsonArray();

            for(ItemPredicateOverrides overridesPredicate:overridesPredicates) {
                JsonObject predicateOverrideJson = new JsonObject();

                {
                    JsonObject predicateJson = new JsonObject();

                    for(ItemPredicateValue predicateValue:overridesPredicate.predicateValues) {
                        predicateJson.addProperty(predicateValue.predicateId.toString(), predicateValue.value);
                    }

                    predicateOverrideJson.add("predicate", predicateJson);
                }

                predicateOverrideJson.addProperty("model", overridesPredicate.modelId.toString());

                overridesJson.add(predicateOverrideJson);
            }

            modelJson.add("overrides", overridesJson);
        }

        return modelJson;
    }

    public record ItemPredicateOverrides(List<ItemPredicateValue> predicateValues, Identifier modelId) {}

    public record ItemPredicateValue(Identifier predicateId, float value) {}
}
