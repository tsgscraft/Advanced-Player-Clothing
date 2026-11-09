package de.tsgscraft.advancedclothing.network.clothing;

import de.tsgscraft.advancedclothing.REFERENCE;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record ClothingSyncPacket(
        Map<String, Map<String, String>> activeClothingElementsByPlayer
) implements CustomPacketPayload {

    public static final Type<ClothingSyncPacket> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            REFERENCE.MODID,
                            "clothing_sync"
                    )
            );

    public static final StreamCodec<ByteBuf, ClothingSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ByteBufCodecs.map(
                                    HashMap::new,
                                    ByteBufCodecs.STRING_UTF8,
                                    ByteBufCodecs.STRING_UTF8
                            )
                    ),
                    ClothingSyncPacket::activeClothingElementsByPlayer,

                    ClothingSyncPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
