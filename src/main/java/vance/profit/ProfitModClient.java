package vance.profit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import vance.profit.client.entity.RouletteTableRenderer;
import vance.profit.client.model.RouletteTableModel;
import vance.profit.client.model.RouletteWheelModel;
import vance.profit.entity.ModEntityTypes;

public class ProfitModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLayerRegistry.registerModelLayer(RouletteTableRenderer.ROULETTE_TABLE, RouletteTableModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(RouletteTableRenderer.ROULETTE_WHEEL, RouletteWheelModel::createBodyLayer);
        EntityRenderers.register(ModEntityTypes.ROULETTE_TABLE, RouletteTableRenderer::new);
    }
}
