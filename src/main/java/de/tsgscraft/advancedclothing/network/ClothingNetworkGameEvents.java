package de.tsgscraft.advancedclothing.network;

import de.tsgscraft.advancedclothing.ClothingRegistry;
import de.tsgscraft.advancedclothing.REFERENCE;
import de.tsgscraft.advancedclothing.network.clothing.ClothingSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ClothingNetworkGameEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer joiningPlayer))
            return;

        Map<String, Map<String, String>> entries = new HashMap<>();

        for (ServerPlayer player : joiningPlayer.server
                .getPlayerList().getPlayers()) {
            if (player == joiningPlayer) continue;
            Map<String, String> a = ClothingRegistry.getInstance().getActiveClothingElements(player.getUUID());
            entries.put(
                    player.getUUID().toString(),
                    a
            );
        }

        PacketDistributor.sendToPlayer(
                joiningPlayer,
                new ClothingSyncPacket(entries)
        );
    }
}
