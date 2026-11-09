package de.tsgscraft.advancedclothing.events;

import de.tsgscraft.advancedclothing.REFERENCE;
import de.tsgscraft.advancedclothing.network.*;
import de.tsgscraft.advancedclothing.client.render.AnchorLayer;
import de.tsgscraft.advancedclothing.client.render.AnchorLayerRender;
import de.tsgscraft.advancedclothing.client.screen.ClothingSelectionScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = REFERENCE.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    public static final KeyMapping toggleEditGUI = new KeyMapping("key.advancedclothing.clothing_selection_gui", GLFW.GLFW_KEY_U, "category.advancedclothing.generic") {

        @Override
        public void setDown(boolean value) {
            if (value && !isDown()) {
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.screen == null && minecraft.player != null) {
                    minecraft.setScreen(new ClothingSelectionScreen());
                }
            }
            super.setDown(value);
        }
    };

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(toggleEditGUI);
    }

    public static final ModelLayerLocation ANCHOR_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "anchor_layer"), "main");

    public static final ModelLayerLocation SLIM_ARMOR_LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(REFERENCE.MODID, "slim_armor_layer"), "main");

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ANCHOR_LAYER, () -> AnchorLayer.createBaseLayer(false));
        event.registerLayerDefinition(SLIM_ARMOR_LAYER, () -> AnchorLayer.createBaseLayer(true));
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        event.getSkins().forEach(
                skin -> {
                    PlayerRenderer renderer =
                            event.getSkin(skin);

                    renderer.addLayer(
                            new AnchorLayerRender(
                                    renderer,
                                    event.getEntityModels()
                            )
                    );
                }
        );
    }
}
