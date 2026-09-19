package de.tsgscraft.advancedclothing.client.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import de.tsgscraft.advancedclothing.AdvancedClothing;
import de.tsgscraft.advancedclothing.Config;
import de.tsgscraft.advancedclothing.REFERENCE;
import de.tsgscraft.advancedclothing.client.ClothingRegistry;
import de.tsgscraft.advancedclothing.client.anchor.Anchors;
import de.tsgscraft.advancedclothing.client.anchor.ClothingAnchor;
import de.tsgscraft.advancedclothing.client.anchor.ClothingAnchorInfo;
import de.tsgscraft.advancedclothing.client.simpleClothing.*;
import de.tsgscraft.advancedclothing.mixin.PlayerModelAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClothingRendering {

    private final Map<String, TextureData> textureDataMap;
    private final ClothingModel model;
    private final ClothingModel slimModel;

    private final boolean hasModel; // Steve variant
    private final boolean hasSlimModel; // Alex variant

    public ClothingRendering(Map<String, TextureData> textureDataMap, ResourceLocation modelLocation, ResourceLocation slimModelLocation) {
        this.textureDataMap = textureDataMap;
        if (modelLocation != null) {
            this.model = ClothingRegistry.getInstance().getClothingModel(modelLocation).setup(textureDataMap);
            hasModel = true;
        } else {
            this.model = null;
            hasModel = false;
        }
        if (slimModelLocation != null) {
            this.slimModel = ClothingRegistry.getInstance().getClothingModel(slimModelLocation).setup(textureDataMap);
            hasSlimModel = true;
        } else {
            this.slimModel = null;
            hasSlimModel = false;
        }
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int light, int overlay, int i, PlayerModel<?> parent, PlayerModel<?> layer, AbstractClientPlayer entity, String renderType) {
        if (Config.onlyBreasts && !(renderType.equals("lboob") || renderType.equals("rboob"))) {
            return;
        }

        if (isSlim(parent) && hasSlimModel) {
            slimModel.compile(poseStack, buffer, light, overlay, i, parent, layer, entity, renderType, false);
        } else if (hasModel) {
            model.compile(poseStack, buffer, light, overlay, i, parent, layer, entity, renderType, false);
        } else if (hasSlimModel) {
            slimModel.compile(poseStack, buffer, light, overlay, i, parent, layer, entity, renderType, false);
        }
    }

    public void renderInInventory(GuiGraphics guiGraphics, int x, int y, int width, int height, float partialTick, int mouseX, int mouseY) {
        if (REFERENCE.isClientSlim && hasSlimModel) {
            slimModel.renderInInventory(guiGraphics, x, y, width, height, partialTick, mouseX, mouseY);
        } else if (hasModel) {
            model.renderInInventory(guiGraphics, x, y, width, height, partialTick, mouseX, mouseY);
        } else if (hasSlimModel) {
            slimModel.renderInInventory(guiGraphics, x, y, width, height, partialTick, mouseX, mouseY);
        }
    }

    public void renderEntityInInventoryFollowsMouse(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int scale, float yOffset, float mouseX, float mouseY) {
        if (REFERENCE.isClientSlim && hasSlimModel) {
            slimModel.renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, x2, y2, scale, yOffset, mouseX, mouseY);
        } else if (hasModel) {
            model.renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, x2, y2, scale, yOffset, mouseX, mouseY);
        } else if (hasSlimModel) {
            slimModel.renderEntityInInventoryFollowsMouse(guiGraphics, x1, y1, x2, y2, scale, yOffset, mouseX, mouseY);
        }
    }

    public void renderEntityInInventoryFollowsAngle(GuiGraphics p_282802_, int p_275688_, int p_275245_, int p_275535_, int p_294406_, int p_294663_, float p_275604_, float angleXComponent, float angleYComponent) {
        if (REFERENCE.isClientSlim && hasSlimModel) {
            slimModel.renderEntityInInventoryFollowsAngle(p_282802_, p_275688_, p_275245_, p_275535_, p_294406_, p_294663_, p_275604_, angleXComponent, angleYComponent);
        } else if (hasModel) {
            model.renderEntityInInventoryFollowsAngle(p_282802_, p_275688_, p_275245_, p_275535_, p_294406_, p_294663_, p_275604_, angleXComponent, angleYComponent);
        } else if (hasSlimModel) {
            slimModel.renderEntityInInventoryFollowsAngle(p_282802_, p_275688_, p_275245_, p_275535_, p_294406_, p_294663_, p_275604_, angleXComponent, angleYComponent);
        }
    }

    public void renderEntityInInventory(GuiGraphics guiGraphics, float x, float y, float scale, Vector3f translate, Quaternionf pose, @Nullable Quaternionf cameraOrientation, LocalPlayer entity) {
        if (REFERENCE.isClientSlim && hasSlimModel) {
            slimModel.renderEntityInInventory(guiGraphics, x, y, scale, translate, pose, cameraOrientation, entity);
        } else if (hasModel) {
            model.renderEntityInInventory(guiGraphics, x, y, scale, translate, pose, cameraOrientation, entity);
        } else if (hasSlimModel) {
            slimModel.renderEntityInInventory(guiGraphics, x, y, scale, translate, pose, cameraOrientation, entity);
        }
    }

    private boolean isSlim(PlayerModel<?> model) {
        return ((PlayerModelAccessor) model).isSlim();
    }

    public void bake() {
        if (hasModel) {
            model.bake(textureDataMap);
        }
        if (hasSlimModel) {
            slimModel.bake(textureDataMap);
        }
    }
}
