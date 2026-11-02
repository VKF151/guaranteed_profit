package vance.profit.client.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import vance.profit.Guaranteed_profit;
import vance.profit.entity.decoration.RouletteTable;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class RouletteTableRenderState extends EntityRenderState {
    private static final Identifier DEFAULT_TEXTURE = Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "textures/entity/roulette_table.png");
    public Direction direction = Direction.NORTH;
    public Identifier texture = DEFAULT_TEXTURE;
    public final ItemStackRenderState item = new ItemStackRenderState();
    public RouletteTable.LocationCoordinate locationCoordinate = new RouletteTable.LocationCoordinate(0, 0);
    public Map<RouletteTable.LocationCoordinate, String> coordToBet;
    public int result;
}
