package de.tsgscraft.advancedclothing;

import com.mojang.logging.LogUtils;
import de.tsgscraft.advancedclothing.client.anchor.Anchors;
import de.tsgscraft.advancedclothing.client.anchor.anchors.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.UUID;

@Mod(value = REFERENCE.MODID, dist = Dist.CLIENT)
public class AdvancedClothingClient {

    private static boolean isClientSide = false;

    public static boolean isClientSide() {
        return isClientSide;
    }

    public AdvancedClothingClient(IEventBus modEventBus, ModContainer modContainer) {
        isClientSide = true;
        Anchors.registerAnchor(new HeadAnchor());
        Anchors.registerAnchor(new BodyAnchor());
        Anchors.registerAnchor(new LeftArmAnchor());
        Anchors.registerAnchor(new RightArmAnchor());
        Anchors.registerAnchor(new LeftLegAnchor());
        Anchors.registerAnchor(new RightLegAnchor());
        Anchors.registerAnchor(new LBoobAnchor());
        Anchors.registerAnchor(new RBoobAnchor());

        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    public static UUID getClientPlayerUUID() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            return player.getUUID();
        }
        return null;
    }
}
