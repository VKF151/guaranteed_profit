package vance.profit.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import vance.profit.Guaranteed_profit;
import vance.profit.client.entity.state.RouletteTableRenderState;
import vance.profit.client.model.RouletteTableModel;
import vance.profit.client.model.RouletteWheelModel;
import vance.profit.entity.decoration.RouletteTable;

import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class RouletteTableRenderer extends EntityRenderer<RouletteTable, RouletteTableRenderState> {
    private final RouletteTableModel model;
    private final RouletteWheelModel wheelModel;
    private final ItemModelResolver itemModelResolver;
    public static final ModelLayerLocation ROULETTE_TABLE = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "roulette_table"), "main");
    public static final ModelLayerLocation ROULETTE_WHEEL = new ModelLayerLocation(Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "roulette_wheel"), "main");

    public RouletteTableRenderer(final EntityRendererProvider.Context context) {
        super(context);
        this.model = new RouletteTableModel(context.bakeLayer(ROULETTE_TABLE));
        this.wheelModel = new RouletteWheelModel(context.bakeLayer(ROULETTE_WHEEL));
        this.itemModelResolver = context.getItemModelResolver();
    }

    public void extractRenderState(final @NonNull RouletteTable rouletteTable, final @NonNull RouletteTableRenderState state, final float partialTicks) {
        super.extractRenderState(rouletteTable, state, partialTicks);
        state.direction = Direction.fromYRot(rouletteTable.getYRot());
        ItemStack itemStack = rouletteTable.getItem();
        this.itemModelResolver.updateForNonLiving(state.item, itemStack, ItemDisplayContext.FIXED, rouletteTable);
        state.locationCoordinate = rouletteTable.getGridCoords();
        rouletteTable.makeBets();
        state.coordToBet = rouletteTable.getCoordToBet();
        state.result = rouletteTable.getResult();
    }
    // Make text render the slot # / name when crosshair is over it

    @Override
    public void submit(RouletteTableRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, @NonNull CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, 180.0F - state.direction.toYRot());
        poseStack.rotateDegrees(Axis.XP, 180.0F);
        poseStack.translate(0.0, -0.25, 0.0);
        submitNodeCollector.submitModel(
                this.model, state, poseStack, this.model.renderType(state.texture), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor
        );
        poseStack.pushPose();
        poseStack.translate(0.25, 0.061, 0.3125);
        submitNodeCollector.submitModel(
                this.wheelModel, state, poseStack, this.wheelModel.renderType(state.texture), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor
        );
        poseStack.popPose();
        submitResult(state, poseStack, submitNodeCollector, state.result);

        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 4; j++) {
                RouletteTable.LocationCoordinate l = new RouletteTable.LocationCoordinate(i + 1, j + 2);
                submitText(state, poseStack, submitNodeCollector, l, false, null);
            }
        }
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(0, 0), true, new Vec3(-0.03125, 0.0625, 0.0));
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(0, 2), true, new Vec3(-0.03125, 0.0625, 0.0));
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(0, 4), true, new Vec3(-0.03125, 0.0625, 0.0));

        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(1, 0), false, new Vec3(0.09375, 0.0, 0.0));
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(3, 0), false, new Vec3(0.125, 0.0, 0.0));
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(6, 0), false, new Vec3(0.0625, 0.0, 0.0));
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(8, 0), false, new Vec3(0.09375, 0.0, 0.0));

        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(2, 1), false, null);
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(5, 1), false, null);
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(8, 1), false, null);

        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(10, 0), true, new Vec3(-0.03125, 0.0675, 0.0));
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(10, 2), false, null);
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(10, 3), false, null);
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(10, 4), false, null);
        submitText(state, poseStack, submitNodeCollector, new RouletteTable.LocationCoordinate(10, 5), false, null);

        if (!state.item.isEmpty()) {
            poseStack.translate(getTranslationFromCoords(state.locationCoordinate, false) + 0.125, 0.0, getTranslationFromCoords(state.locationCoordinate, true) + 0.125);
            poseStack.rotateDegrees(Axis.XP, 90.0F);
            poseStack.scale(0.125F, 0.125F, 0.125F);
            state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        }
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

    @Override
    public @NonNull RouletteTableRenderState createRenderState() {
        return new RouletteTableRenderState();
    }

    public double getTranslationFromCoords(RouletteTable.LocationCoordinate coordinate, boolean y) {
        return y ? ((coordinate.y() * 3) - 2) * 0.0625 : ((coordinate.x() * 3) + 21) * 0.0625;
    }

    public void submitText(RouletteTableRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, RouletteTable.LocationCoordinate coordinate, boolean sidewaysSlot, @Nullable Vec3 optionalTranslate) {
                poseStack.pushPose();
                poseStack.rotateDegrees(Axis.XP, 90.0F);
                poseStack.translate(getTranslationFromCoords(coordinate, false) + 0.125, getTranslationFromCoords(coordinate, true) + 0.15, 0.0 );
                if (optionalTranslate != null) {
                    poseStack.translate(optionalTranslate);
                }
                poseStack.scale(0.007F, -0.007F, 0.007F);
                String s = state.coordToBet.get(coordinate);
                FormattedCharSequence sequence = Component.literal(s).getVisualOrderText();
                float textWidth = this.getFont().width(sequence);
                if (sidewaysSlot) {
                    poseStack.rotateDegrees(Axis.ZN, 90.0F);
                }
                submitNodeCollector.submitText(poseStack, -textWidth/2.0F, 0.0F, sequence, true, Font.DisplayMode.NORMAL, state.lightCoords, 0xFFFFFFFF, 0, 0);
                poseStack.popPose();
    }
    public void submitResult(RouletteTableRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int  result) {
        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.XP, 90.0F);
        poseStack.translate(0.46875, 0.71875, 0.006);
        poseStack.scale(0.06F, -0.06F, 0.06F);
        int color;
        String resultString = String.valueOf(result);
        if (result == 0 || result == 37) {
            color = 0xFF277e44;
        } else {
            color = Arrays.asList(1, 3, 5 ,7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36).contains(result) ? 0xFFFF0000 : 0xFF111111;
        }
        FormattedCharSequence sequence = Component.literal(result == 37 ? "00" : resultString).getVisualOrderText();
        float textWidth = this.getFont().width(sequence);
        submitNodeCollector.submitText(poseStack, -textWidth/2.0F, 0.0F, sequence, false, Font.DisplayMode.NORMAL, state.lightCoords, color, 0, 0);
        poseStack.popPose();

    }
}
