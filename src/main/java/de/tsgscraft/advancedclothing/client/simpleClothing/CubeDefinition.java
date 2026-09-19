package de.tsgscraft.advancedclothing.client.simpleClothing;

import net.minecraft.client.model.geom.PartPose;
import org.joml.Vector3f;

import java.util.Map;

public class CubeDefinition {
    private final Vector3f origin;
    private final Vector3f dimensions;
    private final CubeDeformation grow;
    private final CubeRotation rotation;
    private final PartPose partPose;
    private final ModelCube.UVData uvData;

    protected CubeDefinition(ModelCube.UVData uvData, float originX, float originY, float originZ, float dimensionX, float dimensionY, float dimensionZ, CubeDeformation grow, CubeRotation rotation, PartPose partPose) {
        this.origin = new Vector3f(originX, originY, originZ);
        this.dimensions = new Vector3f(dimensionX, dimensionY, dimensionZ);
        this.grow = grow;
        this.rotation = rotation;
        this.partPose = partPose;
        this.uvData = uvData;
    }

    public ModelCube bake(Map<String, TextureData> textureDataMap) {
        return new ModelCube(uvData, origin.x(), origin.y(), origin.z(), dimensions.x(), dimensions.y(), dimensions.z(), this.grow.growX, this.grow.growY, this.grow.growZ, rotation, partPose, textureDataMap);
    }

    public CubeDefinition cloneCube() {
        return new CubeDefinition(uvData, origin.x(), origin.y(), origin.z(), dimensions.x(), dimensions.y(), dimensions.z(), grow, rotation, partPose);
    }
}
