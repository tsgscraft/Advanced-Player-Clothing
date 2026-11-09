package de.tsgscraft.advancedclothing.network.clothing;

import de.tsgscraft.advancedclothing.REFERENCE;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SetClothingSyncPayload(
        String playerUUID,
        String clothingType,
        String clothingId
) implements CustomPacketPayload {

    public static final Type<SetClothingSyncPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            REFERENCE.MODID,
                            "set_clothing_sync"
                    )
            );

    public static final StreamCodec<ByteBuf, SetClothingSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    SetClothingSyncPayload::playerUUID,

                    ByteBufCodecs.STRING_UTF8,
                    SetClothingSyncPayload::clothingType,

                    ByteBufCodecs.STRING_UTF8,
                    SetClothingSyncPayload::clothingId,

                    SetClothingSyncPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}