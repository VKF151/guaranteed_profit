package vance.profit.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import vance.profit.client.entity.state.RouletteTableRenderState;

@Environment(EnvType.CLIENT)
public class RouletteTableModel extends EntityModel<RouletteTableRenderState> {
    public RouletteTableModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();
        root.addOrReplaceChild(
                "roulette_table",
                CubeListBuilder.create().texOffs(0, 0).addBox(-31.0F, -4.0F, -1.0F, 64.0F, 4.0F, 32.0F, new CubeDeformation(-0.005F)),
                PartPose.offset(23.0F, 4.0F, -7.0F)
        );
        return LayerDefinition.create(meshDefinition, 256, 64);
    }
}
