package de.tsgscraft.advancedclothing;

import com.mojang.logging.LogUtils;
import de.tsgscraft.advancedclothing.client.anchor.Anchors;
import de.tsgscraft.advancedclothing.client.anchor.anchors.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.UUID;

@Mod(REFERENCE.MODID)
public class AdvancedClothing {

    // TODO: Better parsing of clothing models (some parts should be optional)
    // TODO: Better handling of error when loading a clothing model (currently it just crashes the game)

    public static final Logger LOGGER = LogUtils.getLogger();

    private static ClothingRegistry clothingRegistry;

    public AdvancedClothing(IEventBus modEventBus, ModContainer modContainer) {
        AdvancedClothing.getClothingRegistry();
    }

    public static ClothingRegistry getClothingRegistry() {
        if (clothingRegistry == null) {
            clothingRegistry = new ClothingRegistry();
        }
        return clothingRegistry;
    }
}
