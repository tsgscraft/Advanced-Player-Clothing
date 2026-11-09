package de.tsgscraft.advancedclothing;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.tsgscraft.advancedclothing.client.ClothingElement;
import de.tsgscraft.advancedclothing.client.ClothingSave;
import de.tsgscraft.advancedclothing.network.ClothingNetwork;
import de.tsgscraft.advancedclothing.client.simpleClothing.ClothingModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class ClothingRegistry {
    private static ClothingRegistry instance;

    public static ClothingRegistry getInstance() {
        return instance;
    }

    public ClothingRegistry() {
        instance = this;
    }

    private Map<String, ClothingElement> clothingElements;
    private List<String> clothingTypes;
    private List<String> clothingIds;
    private Map<String, List<String>> clothingTypeToIdMap;
    private Map<String, List<ClothingElement>> clothingTypeToElementMap;

    private Map<ResourceLocation, ClothingModel> clothingModelMap = new HashMap<>();

    public void setClothingElements(Map<String, ClothingElement> clothingElements) {
        this.clothingElements = clothingElements;
        this.clothingTypes = clothingElements.values().stream().map(ClothingElement::type).distinct().collect(Collectors.toList());
        this.clothingIds = clothingElements.values().stream().map(clothingElement -> clothingElement.id().toString()).collect(Collectors.toList());
        this.clothingTypeToIdMap = clothingElements.values().stream()
                .collect(Collectors.groupingBy(
                        ClothingElement::type,
                        Collectors.mapping(clothingElement -> clothingElement.id().toString(), Collectors.toList())
                ));
        this.clothingTypeToElementMap = clothingElements.values().stream()
                .collect(Collectors.groupingBy(
                        ClothingElement::type,
                        Collectors.mapping(clothingElement -> clothingElement, Collectors.toList())
                ));
    }

    public Map<String, ClothingElement> getClothingElements() {
        return clothingElements;
    }

    public List<String> getClothingTypes() {
        return clothingTypes;
    }

    public List<String> getClothingIds() {
        return clothingIds;
    }

    public Map<String, List<String>> getClothingTypeToIdMap() {
        return clothingTypeToIdMap;
    }

    public Map<String, List<ClothingElement>> getClothingTypeToElementMap() {
        return clothingTypeToElementMap;
    }

    public Map<ResourceLocation, ClothingModel> getClothingModelMap() {
        return clothingModelMap;
    }

    public void setClothingModelMap(Map<ResourceLocation, ClothingModel> clothingModelMap) {
        this.clothingModelMap = clothingModelMap;
    }

    public void registerClothingModel(ResourceLocation modelLocation, ClothingModel clothingModel) {
        clothingModelMap.put(modelLocation, clothingModel);
    }

    public ClothingModel getClothingModel(ResourceLocation modelLocation) {
        return clothingModelMap.get(modelLocation);
    }

    public boolean hasModelRegistered(ResourceLocation modelLocation) {
        return clothingModelMap.containsKey(modelLocation);
    }

    public void clear() {
        this.clothingModelMap.clear();
    }



    public Map<UUID, Map<String, String>> activeClothingElementsByPlayer = new HashMap<>();

    public void setActiveClothingData(Map<String, String> clothingData) {
        this.activeClothingElementsByPlayer.put(AdvancedClothingClient.getClientPlayerUUID(), clothingData);
        saveActiveClothingData();
        ClothingNetwork.sendFullClothing();
    }

    public void setActiveClothingData(UUID uuid, Map<String, String> clothingData) {
        this.activeClothingElementsByPlayer.put(uuid, clothingData);
    }

    public void setClothing(String clothingType, String clothingId) {
        setClothing(clothingType, clothingId, true);
    }

    public void setClothing(String clothingType, String clothingId, boolean sendToServer) {
        if (clothingId == null || clothingId.isEmpty()) {
            getActiveClothingElements().remove(clothingType);
        } else {
            getActiveClothingElements().put(clothingType, clothingId);
        }
        saveActiveClothingData();
        if (sendToServer) {
            ClothingNetwork.sendSetClothing(clothingType, clothingId);
        }
    }

    public void setClothing(UUID uuid, String clothingType, String clothingId) {
        setClothing(uuid, clothingType, clothingId, true);
    }

    public void setClothing(UUID uuid, String clothingType, String clothingId, boolean sendToServer) {
        if (clothingId == null || clothingId.isEmpty()) {
            getActiveClothingElements(uuid).remove(clothingType);
        } else {
            getActiveClothingElements(uuid).put(clothingType, clothingId);
        }
        saveActiveClothingData();
        if (sendToServer) {
            ClothingNetwork.sendSetClothing(clothingType, clothingId);
        }
    }

    public void clearActiveClothingData() {
        getActiveClothingElements().clear();
        saveActiveClothingData();
        ClothingNetwork.sendFullClothing();
    }

    public boolean hasClothing(String clothingType) {
        return getActiveClothingElements().containsKey(clothingType);
    }

    public boolean hasClothing(UUID uuid, String clothingType) {
        return getActiveClothingElements(uuid).containsKey(clothingType);
    }

    public boolean hasClothing(String clothingType, String clothingId) {
        return getActiveClothingElements().containsKey(clothingType) && getActiveClothingElements().get(clothingType).equals(clothingId);
    }

    public boolean hasClothing(UUID uuid, String clothingType, String clothingId) {
        return getActiveClothingElements(uuid).containsKey(clothingType) && getActiveClothingElements(uuid).get(clothingType).equals(clothingId);
    }

    public Map<String, String> getActiveClothingElements() {
        return activeClothingElementsByPlayer.computeIfAbsent(AdvancedClothingClient.getClientPlayerUUID(), k -> new HashMap<>());
    }

    public Map<String, String> getActiveClothingElements(UUID uuid) {
        return activeClothingElementsByPlayer.computeIfAbsent(uuid, k -> new HashMap<>());
    }

    public void saveActiveClothingData() {
        if (AdvancedClothingClient.isClientSide()) {
            ClothingSave.saveActiveClothingData();
        }
    }
}
