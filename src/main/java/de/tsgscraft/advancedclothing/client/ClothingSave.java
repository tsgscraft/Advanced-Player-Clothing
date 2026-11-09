package de.tsgscraft.advancedclothing.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.tsgscraft.advancedclothing.AdvancedClothing;
import de.tsgscraft.advancedclothing.ClothingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ClothingSave {


    private static final File file = new File("config/advancedclothing/active_clothing_data.json");

    public static void saveActiveClothingData() {
        /*
        Save to file in the following format:
        {
            "singleplayer": {
                "<world_folder_name>": {
                    "activeClothingElements": {
                        "hat": "modid:hat_id",
                        "shirt": "modid:shirt_id",
                        "pants": "modid:pants_id",
                        "shoes": "modid:shoes_id"
                    }
                }
            },
            "multiplayer": {
                "<server_ip>": {
                    "activeClothingElements": {
                        "hat": "modid:hat_id",
                        "shirt": "modid:shirt_id",
                        "pants": "modid:pants_id",
                        "shoes": "modid:shoes_id"
                    }
                }
            }
        }
         */

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean isSingleplayer = Minecraft.getInstance().getCurrentServer() == null;
        String saveKey;
        if (isSingleplayer && Minecraft.getInstance().getSingleplayerServer() != null) {
            // do not use getLevelName() as the can be multiple worlds with the same name, use the world folder name instead
            saveKey = Minecraft.getInstance().getSingleplayerServer().getWorldPath(LevelResource.ROOT).toFile().getParentFile().getName();
        } else {
            saveKey = Minecraft.getInstance().getCurrentServer().ip;
        }
        AdvancedClothing.LOGGER.info("Saving active clothing data for " + (isSingleplayer ? "singleplayer" : "multiplayer") + " with key: " + saveKey);

        JsonObject activeClothingData = readFile(file);
        if (activeClothingData == null) {
            activeClothingData = new JsonObject();
            activeClothingData.add("singleplayer", new JsonObject());
            activeClothingData.add("multiplayer", new JsonObject());
        }

        JsonObject environmentData = isSingleplayer ? activeClothingData.getAsJsonObject("singleplayer") : activeClothingData.getAsJsonObject("multiplayer");
        JsonObject currentData = new JsonObject();
        JsonObject activeClothingElementsJson = new JsonObject();
        for (Map.Entry<String, String> entry : ClothingRegistry.getInstance().getActiveClothingElements().entrySet()) {
            activeClothingElementsJson.addProperty(entry.getKey(), entry.getValue());
        }
        currentData.add("activeClothingElements", activeClothingElementsJson);
        environmentData.add(saveKey, currentData);

        try (FileWriter writer = new FileWriter(file)) {
            new Gson().toJson(activeClothingData, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadActiveClothingData() {
        if (!file.exists()) {
            return;
        }

        boolean isSingleplayer = Minecraft.getInstance().isSingleplayer();
        String saveKey;
        if (isSingleplayer && Minecraft.getInstance().getSingleplayerServer() != null) {
            saveKey = Minecraft.getInstance().getSingleplayerServer().getWorldPath(LevelResource.ROOT).toFile().getParentFile().getName();
        } else {
            saveKey = Objects.requireNonNull(Minecraft.getInstance().getCurrentServer()).ip;
        }

        JsonObject activeClothingData = readFile(file);
        if (activeClothingData == null) {
            AdvancedClothing.LOGGER.info("Failed at file could not be read.");
            return;
        }

        JsonObject environmentData = isSingleplayer ? activeClothingData.getAsJsonObject("singleplayer") : activeClothingData.getAsJsonObject("multiplayer");
        if (environmentData == null) {
            AdvancedClothing.LOGGER.info("Failed at environmentData is null.");
            return;
        }

        JsonObject currentData = environmentData.getAsJsonObject(saveKey);
        if (currentData == null) {
            AdvancedClothing.LOGGER.info("Failed at currentData is null.");
            return;
        }

        JsonObject activeClothingElementsJson = currentData.getAsJsonObject("activeClothingElements");
        if (activeClothingElementsJson == null) {
            AdvancedClothing.LOGGER.info("Failed at activeClothingElementsJson is null.");
            return;
        }

        for (String clothingType : ClothingRegistry.getInstance().getClothingTypes()) {
            String clothingId = activeClothingElementsJson.has(clothingType) ? activeClothingElementsJson.get(clothingType).getAsString() : null;
            if (clothingId != null && !clothingId.isEmpty()) {
                ClothingRegistry.getInstance().getActiveClothingElements().put(clothingType, clothingId);
            }
        }
    }

    public static JsonObject readFile(File file) {
        try {
            return new Gson().fromJson(new String(Files.readAllBytes(file.toPath())), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
