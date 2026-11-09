package de.tsgscraft.advancedclothing.network;

import de.tsgscraft.advancedclothing.AdvancedClothing;
import de.tsgscraft.advancedclothing.AdvancedClothingClient;
import de.tsgscraft.advancedclothing.ClothingRegistry;
import de.tsgscraft.advancedclothing.REFERENCE;
import de.tsgscraft.advancedclothing.network.clothing.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

import java.util.Map;
import java.util.UUID;

public class ClothingNetwork {
    private ClothingNetwork() {
    }

    public static void sendSetClothing(String clothingType, String clothingId) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.getConnection() == null) {
            return;
        }

        if (!REFERENCE.compatibleServer) return;

        SetClothingPayload payload =
                new SetClothingPayload(
                        Minecraft.getInstance().player.getUUID().toString(),
                        clothingType,
                        clothingId
                );

        minecraft.getConnection().send(
                new ServerboundCustomPayloadPacket(payload)
        );
    }

    public static void receiveSetClothing(SetClothingSyncPayload payload) {
        UUID uuid = UUID.fromString(payload.playerUUID());
        if (uuid.equals(Minecraft.getInstance().player.getUUID())) {
            return;
        }
        ClothingRegistry.getInstance().setClothing(uuid, payload.clothingType(), payload.clothingId(), false);
    }

    public static void receiveSetClothing(SetClothingPayload payload) {
        UUID uuid = UUID.fromString(payload.playerUUID());
        ClothingRegistry.getInstance().setClothing(uuid, payload.clothingType(), payload.clothingId(), false);
    }

    public static void sendFullClothing() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.getConnection() == null) {
            return;
        }

        if (!REFERENCE.compatibleServer) return;

        FullClothingPayload payload =
                new FullClothingPayload(
                        Minecraft.getInstance().player.getUUID().toString(),
                        ClothingRegistry.getInstance().getActiveClothingElements()
                );

        minecraft.getConnection().send(
                new ServerboundCustomPayloadPacket(payload)
        );
    }

    public static void receiveFullClothing(FullClothingSyncPayload payload) {
        UUID uuid = UUID.fromString(payload.playerUUID());
        if (uuid.equals(Minecraft.getInstance().player.getUUID())) {
            return;
        }
        ClothingRegistry.getInstance().setActiveClothingData(uuid, payload.clothingData());
    }

    public static void receiveFullClothing(FullClothingPayload payload) {
        UUID uuid = UUID.fromString(payload.playerUUID());
        ClothingRegistry.getInstance().setActiveClothingData(uuid, payload.clothingData());
    }

    public static void receiveClothingSync(ClothingSyncPacket payload) {
        AdvancedClothing.LOGGER.info("Received clothing sync packet with " + payload.activeClothingElementsByPlayer().size() + " entries.");
        Map<String, Map<String, String>> clothingData = payload.activeClothingElementsByPlayer();
        for (Map.Entry<String, Map<String, String>> entry : clothingData.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());
            if (uuid.equals(Minecraft.getInstance().player.getUUID())) {
                continue;
            }
            ClothingRegistry.getInstance().setActiveClothingData(uuid, entry.getValue());
        }
    }
}
