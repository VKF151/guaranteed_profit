package vance.profit.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface CrossbowFireCallback {
    Event<CrossbowFireCallback> EVENT = EventFactory.createArrayBacked(CrossbowFireCallback.class,
            (listeners) -> (player, level, hand) -> {
                for (CrossbowFireCallback listener : listeners){
                    InteractionResult result = listener.interact(player, level, hand);

                    if (result != InteractionResult.PASS){ return result; }
                }

                return InteractionResult.PASS;
            });

    InteractionResult interact(Player player, Level level, InteractionHand hand);
}
