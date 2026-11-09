package de.tsgscraft.advancedclothing.network.clothing;

import de.tsgscraft.advancedclothing.REFERENCE;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record FullClothingSyncPayload(
        String playerUUID,
        Map<String, String> clothingData
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FullClothingSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            REFERENCE.MODID,
                            "set_full_clothing_sync"
                    )
            );

    public static final StreamCodec<ByteBuf, FullClothingSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    FullClothingSyncPayload::playerUUID,
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ByteBufCodecs.STRING_UTF8
                    ),
                    FullClothingSyncPayload::clothingData,

                    FullClothingSyncPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
