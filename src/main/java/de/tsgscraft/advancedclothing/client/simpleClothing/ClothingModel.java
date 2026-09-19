package de.tsgscraft.advancedclothing.client.simpleClothing;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import de.tsgscraft.advancedclothing.client.anchor.Anchors;
import de.tsgscraft.advancedclothing.client.anchor.ClothingAnchor;
import de.tsgscraft.advancedclothing.client.anchor.ClothingAnchorInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClothingModel {
    private final Map<ClothingAnchorInfo, List<CubeDefinition>> cubesWithAnchor;
    private final Map<ClothingAnchorInfo, Model> bakedCubesWithAnchor;

    private boolean isBaked = false;

    public ClothingModel(Map<ClothingAnchorInfo, List<CubeDefinition>> cubesWithAnchor) {
        this.cubesWithAnchor = cubesWithAnchor;
        this.bakedCubesWithAnchor = new HashMap<>();
    }

    public ClothingModel setup(Map<String, TextureData> textureDataMap) {
        Map<ClothingAnchorInfo, List<CubeDefinition>> clonedCubesWithAnchor = new HashMap<>();
        for (Map.Entry<ClothingAnchorInfo, List<CubeDefinition>> entry : cubesWithAnchor.entrySet()) {
            List<CubeDefinition> clonedCubes = new ArrayList<>();
            for (CubeDefinition cube : entry.getValue()) {
                clonedCubes.add(cube.cloneCube());
            }
            clonedCubesWithAnchor.put(entry.getKey(), clonedCubes);
        }
        return new ClothingModel(clonedCubesWithAnchor).bake(textureDataMap);
    }

    public ClothingModel bake(Map<String, TextureData> textureDataMap) {
        bake(cubesWithAnchor, bakedCubesWithAnchor, textureDataMap);
        isBaked = true;
        return this;
    }

    private void bake(Map<ClothingAnchorInfo, List<CubeDefinition>> cubesWithAnchor, Map<ClothingAnchorInfo, Model> bakedCubesWithAnchor, Map<String, TextureData> textureDataMap) {
        for (Map.Entry<ClothingAnchorInfo, List<CubeDefinition>> entry : cubesWithAnchor.entrySet()) {
            List<ModelCube> modelCubes = new ArrayList<>();
            for (CubeDefinition cube : entry.getValue()) {
                ModelCube bakedCube = cube.bake(textureDataMap);
                modelCubes.add(bakedCube);
            }
            bakedCubesWithAnchor.put(entry.getKey(), new Model(modelCubes));
        }
    }

    public boolean isBaked() {
        return isBaked;
    }

    public Map<ClothingAnchorInfo, Model> getBakedCubesWithAnchor() {
        return bakedCubesWithAnchor;
    }

    public void compile(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, int color, PlayerModel<?> model, PlayerModel<?> anchorLayer, AbstractClientPlayer player, String renderKey, boolean usePlayerRotation) {
        if (!isBaked) {
            return;
        }
        compileWithAnchor(poseStack, buffer, packedLight, packedOverlay, color, anchorLayer, player, bakedCubesWithAnchor, renderKey, usePlayerRotation);
    }

    private void compileWithAnchor(GuiGraphics guiGraphics, int packedLight, int packedOverlay, int color, PlayerModel<?> model, AbstractClientPlayer player, Map<ClothingAnchorInfo, Model> bakedCubesWithAnchor, String renderKey) {
        PoseStack poseStack = guiGraphics.pose();
        MultiBufferSource buffer = guiGraphics.bufferSource();
        compileWithAnchor(poseStack, buffer, packedLight, packedOverlay, color, model, player, bakedCubesWithAnchor, renderKey, true);
    }

    private void compileWithAnchor(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay, int color, PlayerModel<?> model, AbstractClientPlayer player, Map<ClothingAnchorInfo, Model> bakedCubesWithAnchor, String renderKey, boolean usePlayerRotation) {
        for (Map.Entry<ClothingAnchorInfo, Model> entry : bakedCubesWithAnchor.entrySet()) {
            ClothingAnchorInfo info = entry.getKey();
            ClothingAnchor anchor = Anchors.getAnchor(info);
            poseStack.pushPose();
            boolean enabled = true;
            if (anchor != null) {
                if (!anchor.renderKey().equals(renderKey) && !anchor.renderKey().equals("all")) {
                    poseStack.popPose();
                    continue;
                }
                if (player != null && model != null) {
                    if (usePlayerRotation) {
                        anchor.transformForInventory(poseStack, model, player, info);
                    }else {
                        anchor.transform(poseStack, model, player, info);
                        enabled = anchor.isEnabled(player, model, info);
                    }
                }
                poseStack.scale(1, -1, 1);
                poseStack.translate(info.getAnchorOffsetX()/16.0F, info.getAnchorOffsetY()/16.0F, info.getAnchorOffsetZ()/16.0F);
            }
            if (enabled) {
                entry.getValue().compile(poseStack.last(), buffer, packedLight, packedOverlay, color);
            }
            poseStack.popPose();
        }
    }

    public void renderInInventory(GuiGraphics guiGraphics, int x, int y, int width, int height, float partialTick, int mouseX, int mouseY) {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(x + width / 2.0f, y + height / 2.0f, 100);
        poseStack.scale(1, -1, 1);
        poseStack.scale(30, 30, 30);
        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(45)));
        poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-20)));
        Lighting.setupFor3DItems();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(Minecraft.getInstance().player);
        compile(poseStack, buffer, 15728880, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF, renderer.getModel(), renderer.getModel(), Minecraft.getInstance().player, "generic", true);
        buffer.endBatch();
        poseStack.popPose();
    }

    public void renderEntityInInventoryFollowsMouse(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int scale, float yOffset, float mouseX, float mouseY) {
        float f = (float)(x1 + x2) / 2.0F;
        float f1 = (float)(y1 + y2) / 2.0F;
        float f2 = (float)Math.atan((double)((f - mouseX) / 40.0F));
        float f3 = (float)Math.atan((double)((f1 - mouseY) / 40.0F));
        renderEntityInInventoryFollowsAngle(guiGraphics, x1, y1, x2, y2, scale, yOffset, f2, f3);
    }

    public void renderEntityInInventoryFollowsAngle(GuiGraphics p_282802_, int p_275688_, int p_275245_, int p_275535_, int p_294406_, int p_294663_, float p_275604_, float angleXComponent, float angleYComponent) {
        float f = (float)(p_275688_ + p_275535_) / 2.0F;
        float f1 = (float)(p_275245_ + p_294406_) / 2.0F;
        p_282802_.enableScissor(p_275688_, p_275245_, p_275535_, p_294406_);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ((float)Math.PI);
        Quaternionf quaternionf1 = (new Quaternionf()).rotateX(angleYComponent * 20.0F * ((float)Math.PI / 180F));
        quaternionf.mul(quaternionf1);
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            return;
        }
        float f4 = localPlayer.yBodyRot;
        float f5 = localPlayer.getYRot();
        float f6 = localPlayer.getXRot();
        float f7 = localPlayer.yHeadRotO;
        float f8 = localPlayer.yHeadRot;
        localPlayer.yBodyRot = 180.0F + angleXComponent * 20.0F;
        localPlayer.setYRot(180.0F + angleXComponent * 40.0F);
        localPlayer.setXRot(-angleYComponent * 20.0F);
        localPlayer.yHeadRot = localPlayer.getYRot();
        localPlayer.yHeadRotO = localPlayer.getYRot();
        float f9 = localPlayer.getScale();
        Vector3f vector3f = new Vector3f(0.0F, localPlayer.getBbHeight() / 2.0F + p_275604_ * f9, 0.0F);
        float f10 = (float)p_294663_ / f9;
        renderEntityInInventory(p_282802_, f, f1, f10, vector3f, quaternionf, quaternionf1, localPlayer);
        localPlayer.yBodyRot = f4;
        localPlayer.setYRot(f5);
        localPlayer.setXRot(f6);
        localPlayer.yHeadRotO = f7;
        localPlayer.yHeadRot = f8;
        p_282802_.disableScissor();
    }

    public void renderEntityInInventory(GuiGraphics guiGraphics, float x, float y, float scale, Vector3f translate, Quaternionf pose, @Nullable Quaternionf cameraOrientation, LocalPlayer entity) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((double)x, (double)y-scale-10, (double)50.0F);
        guiGraphics.pose().scale(scale, scale, -scale);
        guiGraphics.pose().translate(translate.x, translate.y, translate.z);
        guiGraphics.pose().mulPose(pose);
        guiGraphics.pose().mulPose(new Quaternionf().rotateXYZ((float)Math.PI, 0.0F, 0.0F));
        Lighting.setupForEntityInInventory();
        PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
        compileWithAnchor(guiGraphics, 15728880, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF, renderer.getModel(), entity, bakedCubesWithAnchor, "generic");
        guiGraphics.bufferSource().endBatch();
        guiGraphics.pose().popPose();
        Lighting.setupFor3DItems();
    }
}
