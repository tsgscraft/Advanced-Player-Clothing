package de.tsgscraft.advancedclothing.events;

import de.tsgscraft.advancedclothing.AdvancedClothingClient;
import de.tsgscraft.advancedclothing.REFERENCE;
import de.tsgscraft.advancedclothing.client.ClothingSave;
import de.tsgscraft.advancedclothing.network.ClothingNetwork;
import de.tsgscraft.advancedclothing.client.simpleClothing.ClothingResourceLoader;
import de.tsgscraft.advancedclothing.network.plugin.TestForServerPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientGameEvents {

    @SubscribeEvent
    public static void registerReloadListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ClothingResourceLoader());
    }

    private static int pendingTicks = -1;

    @SubscribeEvent
    public static void onPlayerLoggedIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClothingNetwork.sendFullClothing();
        try {
            PacketDistributor.sendToServer(
                    new TestForServerPayload((byte) 0)
            );
        }catch (Exception ignored) {}
        if (AdvancedClothingClient.isClientSide()) ClothingSave.loadActiveClothingData();
        pendingTicks = 5;
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(ClientPlayerNetworkEvent.LoggingOut event) {
        REFERENCE.compatibleServer = false;
    }

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        if (pendingTicks > 0 && --pendingTicks == 0) {
            ClothingNetwork.sendFullClothing();
        }
    }
}

