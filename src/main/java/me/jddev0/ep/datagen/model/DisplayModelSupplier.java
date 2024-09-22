package me.jddev0.ep.datagen.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3f;

import java.util.function.Supplier;

public record DisplayModelSupplier(
        Identifier parent,
        Vector3f scaleFirstPersonLeftHand,
        Vector3f scaleFirstPersonRightHand,
        Vector3f scaleThirdPersonLeftHand,
        Vector3f scaleThirdPersonRightHand,
        Vector3f scaleFixed,
        Vector3f scaleGround,
        Vector3f scaleGui, Vec3i rotationGui
) implements Supplier<JsonElement> {
    public DisplayModelSupplier(Identifier parent, Vector3f scaleWorld, Vector3f scaleGui, Vec3i rotationGui) {
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

                    scaleJson.add(scaleFirstPersonLeftHand.x);
                    scaleJson.add(scaleFirstPersonLeftHand.y);
                    scaleJson.add(scaleFirstPersonLeftHand.z);

                    firstPersonLeftHandJson.add("scale", scaleJson);
                }

                displayJson.add("firstperson_lefthand", firstPersonLeftHandJson);
            }

            {
                JsonObject firstPersonRightHandJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleFirstPersonRightHand.x);
                    scaleJson.add(scaleFirstPersonRightHand.y);
                    scaleJson.add(scaleFirstPersonRightHand.z);

                    firstPersonRightHandJson.add("scale", scaleJson);
                }

                displayJson.add("firstperson_righthand", firstPersonRightHandJson);
            }

            {
                JsonObject thirdPersonLeftHandJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleThirdPersonLeftHand.x);
                    scaleJson.add(scaleThirdPersonLeftHand.y);
                    scaleJson.add(scaleThirdPersonLeftHand.z);

                    thirdPersonLeftHandJson.add("scale", scaleJson);
                }

                displayJson.add("thirdperson_lefthand", thirdPersonLeftHandJson);
            }

            {
                JsonObject thirdPersonRightHandJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleThirdPersonRightHand.x);
                    scaleJson.add(scaleThirdPersonRightHand.y);
                    scaleJson.add(scaleThirdPersonRightHand.z);

                    thirdPersonRightHandJson.add("scale", scaleJson);
                }

                displayJson.add("thirdperson_righthand", thirdPersonRightHandJson);
            }

            {
                JsonObject fixedJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleFixed.x);
                    scaleJson.add(scaleFixed.y);
                    scaleJson.add(scaleFixed.z);

                    fixedJson.add("scale", scaleJson);
                }

                displayJson.add("fixed", fixedJson);
            }

            {
                JsonObject groundJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleGround.x);
                    scaleJson.add(scaleGround.y);
                    scaleJson.add(scaleGround.z);

                    groundJson.add("scale", scaleJson);
                }

                displayJson.add("ground", groundJson);
            }

            {
                JsonObject guiJson = new JsonObject();

                {
                    JsonArray scaleJson = new JsonArray();

                    scaleJson.add(scaleGui.x);
                    scaleJson.add(scaleGui.y);
                    scaleJson.add(scaleGui.z);

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
