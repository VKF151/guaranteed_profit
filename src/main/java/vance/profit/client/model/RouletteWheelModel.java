package vance.profit.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import vance.profit.client.entity.state.RouletteTableRenderState;

@Environment(EnvType.CLIENT)
public class RouletteWheelModel extends EntityModel<RouletteTableRenderState> {
    public RouletteWheelModel(ModelPart root) {super(root);}

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        root.addOrReplaceChild(
                "roulette_wheel",
                CubeListBuilder.create().texOffs(0, 36).addBox(-31.0F, -4.0F, -1.0F, 22.0F, 0.1F, 22.0F, new CubeDeformation(1.0F)),
                PartPose.offset(23.0F, 4.01F, -7.0F)
        );
        return LayerDefinition.create(meshDefinition, 256, 64);
    }

}
