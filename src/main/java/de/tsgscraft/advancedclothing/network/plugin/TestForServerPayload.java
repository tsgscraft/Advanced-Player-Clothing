package de.tsgscraft.advancedclothing.network.plugin;

import de.tsgscraft.advancedclothing.REFERENCE;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TestForServerPayload(byte data) implements CustomPacketPayload {

    public static final Type<TestForServerPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(
                    REFERENCE.MODID, "test_for_server"
            ));

    public static final StreamCodec<ByteBuf, TestForServerPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BYTE,
                    TestForServerPayload::data,
                    TestForServerPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
