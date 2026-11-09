package de.tsgscraft.advancedclothing.network.clothing;

import de.tsgscraft.advancedclothing.REFERENCE;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public record FullClothingPayload(
        String playerUUID,
        Map<String, String> clothingData
) implements CustomPacketPayload {

    public static final Type<FullClothingPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            REFERENCE.MODID,
                            "set_full_clothing"
                    )
            );

    public static final StreamCodec<ByteBuf, FullClothingPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    FullClothingPayload::playerUUID,
                    ByteBufCodecs.map(
                            HashMap::new,
                            ByteBufCodecs.STRING_UTF8,
                            ByteBufCodecs.STRING_UTF8
                    ),
                    FullClothingPayload::clothingData,

                    FullClothingPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
