package de.tsgscraft.advancedclothing.client.simpleClothing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.tsgscraft.advancedclothing.client.anchor.ClothingAnchorInfo;
import net.minecraft.client.model.geom.PartPose;

import java.util.List;

public class CubeListBuilder {
    private final List<CubeDefinition> cubes = Lists.newArrayList();
    private ModelCube.UVData uvData;
    private boolean mirror;

    public CubeListBuilder mirror() {
        return this.mirror(true);
    }

    public CubeListBuilder mirror(boolean mirror) {
        this.mirror = mirror;
        return this;
    }

    public CubeListBuilder texOffs(ModelCube.UVData uvData) {
        this.uvData = uvData;
        return this;
    }

    public CubeListBuilder addBox(float originX, float originY, float originZ, int dimensionX, int dimensionY, int dimensionZ, CubeDeformation cubeDeformation, ModelCube.UVData uvData) {
        this.texOffs(uvData);
        this.cubes.add(new CubeDefinition(uvData, originX, originY, originZ, (float)dimensionX, (float)dimensionY, (float)dimensionZ, cubeDeformation, CubeRotation.NONE, PartPose.ZERO));
        return this;
    }

    public CubeListBuilder addBox(float originX, float originY, float originZ, int dimensionX, int dimensionY, int dimensionZ, ModelCube.UVData uvData) {
        this.texOffs(uvData);
        this.cubes.add(new CubeDefinition(uvData, originX, originY, originZ, (float)dimensionX, (float)dimensionY, (float)dimensionZ, CubeDeformation.NONE, CubeRotation.NONE, PartPose.ZERO));
        return this;
    }

    public CubeListBuilder addBox(float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ) {
        this.texOffs(uvData);
        this.cubes.add(new CubeDefinition(this.uvData, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, CubeDeformation.NONE, CubeRotation.NONE, PartPose.ZERO));
        return this;
    }

    public CubeListBuilder addBox(float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, CubeRotation rotation, PartPose partPose) {
        this.texOffs(uvData);
        this.cubes.add(new CubeDefinition(this.uvData, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, CubeDeformation.NONE, rotation, partPose));
        return this;
    }

    public CubeListBuilder addBox(float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, CubeDeformation cubeDeformation) {
        this.texOffs(uvData);
        this.cubes.add(new CubeDefinition(this.uvData, originX, originY, originZ, dimensionX, dimensionY, dimensionZ, cubeDeformation, CubeRotation.NONE, PartPose.ZERO));
        return this;
    }

    public List<CubeDefinition> getCubes() {
        return ImmutableList.copyOf(this.cubes);
    }
}
