package me.jddev0.ep.datagen.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;

import java.util.function.Supplier;

public record ItemWithDisplayModelSupplier(
        Identifier parent,
        Vec3f scaleFirstPersonLeftHand,
        Vec3f scaleFirstPersonRightHand,
        Vec3f scaleThirdPersonLeftHand,
        Vec3f scaleThirdPersonRightHand,
        Vec3f scaleFixed,
        Vec3f scaleGround,
        Vec3f scaleGui, Vec3i rotationGui
) implements Supplier<JsonElement> {
    public ItemWithDisplayModelSupplier(Identifier parent, Vec3f scaleWorld, Vec3f scaleGui, Vec3i rotationGui) {
        this(parent, scaleWorld, scaleWorld, scaleWorld, scaleWorld, scaleWorld, scaleWorld, scaleGui, rotationGui);
    }

    @Override
    public JsonElement get() {
        JsonObject modelJson = new JsonObject();
        modelJson.addProperty("parent", parent.toString());

        {
            JsonObject displayJson = new JsonObject();

            {
                JsonObject firstPersonLeftHandJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleFirstPersonLeftHand.getX());
                    scaleJson.add(scaleFirstPersonLeftHand.getY());
                    scaleJson.add(scaleFirstPersonLeftHand.getZ());

                    firstPersonLeftHandJson.add("scale", scaleJson);
                }

                displayJson.add("firstperson_lefthand", firstPersonLeftHandJson);
            }

            {
                JsonObject firstPersonRightHandJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleFirstPersonRightHand.getX());
                    scaleJson.add(scaleFirstPersonRightHand.getY());
                    scaleJson.add(scaleFirstPersonRightHand.getZ());

                    firstPersonRightHandJson.add("scale", scaleJson);
                }

                displayJson.add("firstperson_righthand", firstPersonRightHandJson);
            }

            {
                JsonObject thirdPersonLeftHandJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleThirdPersonLeftHand.getX());
                    scaleJson.add(scaleThirdPersonLeftHand.getY());
                    scaleJson.add(scaleThirdPersonLeftHand.getZ());

                    thirdPersonLeftHandJson.add("scale", scaleJson);
                }

                displayJson.add("thirdperson_lefthand", thirdPersonLeftHandJson);
            }

            {
                JsonObject thirdPersonRightHandJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleThirdPersonRightHand.getX());
                    scaleJson.add(scaleThirdPersonRightHand.getY());
                    scaleJson.add(scaleThirdPersonRightHand.getZ());

                    thirdPersonRightHandJson.add("scale", scaleJson);
                }

                displayJson.add("thirdperson_righthand", thirdPersonRightHandJson);
            }

            {
                JsonObject fixedJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleFixed.getX());
                    scaleJson.add(scaleFixed.getY());
                    scaleJson.add(scaleFixed.getZ());

                    fixedJson.add("scale", scaleJson);
                }

                displayJson.add("fixed", fixedJson);
            }

            {
                JsonObject groundJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleGround.getX());
                    scaleJson.add(scaleGround.getY());
                    scaleJson.add(scaleGround.getZ());

                    groundJson.add("scale", scaleJson);
                }

                displayJson.add("ground", groundJson);
            }

            {
                JsonObject guiJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleGui.getX());
                    scaleJson.add(scaleGui.getY());
                    scaleJson.add(scaleGui.getZ());

                    guiJson.add("scale", scaleJson);
                }

                {
                    JsonArray rotationJson = new JsonArray();

                    rotationJson.add(rotationGui.getX());
                    rotationJson.add(rotationGui.getY());
                    rotationJson.add(rotationGui.getZ());

                    guiJson.add("rotation", rotationJson);
                }

                displayJson.add("gui", guiJson);
            }

            modelJson.add("display", displayJson);
        }

        return modelJson;
    }
}
