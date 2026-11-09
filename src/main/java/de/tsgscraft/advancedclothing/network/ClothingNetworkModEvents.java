package de.tsgscraft.advancedclothing.network;

import de.tsgscraft.advancedclothing.AdvancedClothing;
import de.tsgscraft.advancedclothing.REFERENCE;
import de.tsgscraft.advancedclothing.network.clothing.*;
import de.tsgscraft.advancedclothing.network.plugin.TestForServerPayload;
import de.tsgscraft.advancedclothing.network.plugin.TestForServerReturnPayload;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ClothingNetworkModEvents {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar =
                event.registrar("1").optional();

        registrar.playToServer(
                SetClothingPayload.TYPE,
                SetClothingPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ClothingNetwork.receiveSetClothing(payload);
                        PacketDistributor.sendToAllPlayers(
                                new SetClothingSyncPayload(
                                        context.player().getUUID().toString(),
                                        payload.clothingType(),
                                        payload.clothingId()
                                )
                        );
                    });
                }
        );

        registrar.playToServer(
                FullClothingPayload.TYPE,
                FullClothingPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ClothingNetwork.receiveFullClothing(payload);
                        PacketDistributor.sendToAllPlayers(
                                new FullClothingSyncPayload(payload.playerUUID(), payload.clothingData())
                        );
                    });
                }
        );

        // Sync packets ------------------------------------------------

        registrar.playToClient(
                SetClothingSyncPayload.TYPE,
                SetClothingSyncPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ClothingNetwork.receiveSetClothing(payload);
                    });
                }
        );

        registrar.playToClient(
                FullClothingSyncPayload.TYPE,
                FullClothingSyncPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        ClothingNetwork.receiveFullClothing(payload);
                    });
                }
        );

        registrar.playToClient(
                ClothingSyncPacket.TYPE,
                ClothingSyncPacket.STREAM_CODEC,
                ((payload, context) -> {
                    context.enqueueWork(() -> {
                        ClothingNetwork.receiveClothingSync(payload);
                    });
                })
        );

        // Plugin ------------------------------------------------


        registrar.playToServer(
                TestForServerPayload.TYPE,
                TestForServerPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() -> {
                        // Send back a response to the client
                        if (context.player() instanceof ServerPlayer player) {
                            PacketDistributor.sendToPlayer(
                                    player,
                                    new TestForServerReturnPayload((byte) 1)
                            );
                        }
                    });
                }
        );

        registrar.playToClient(
                TestForServerReturnPayload.TYPE,
                TestForServerReturnPayload.STREAM_CODEC,
                (payload, context) -> {
                    if (payload.data() == 1) {
                        // Compatible server detected!
                        AdvancedClothing.LOGGER.info("Compatible server detected!");
                        REFERENCE.compatibleServer = true;
                    }
                }
        );
    }
}
