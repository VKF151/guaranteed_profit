package vance.profit.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;
import vance.profit.Guaranteed_profit;

public class TisButAScratchItem extends Item {
    public TisButAScratchItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack itemStack, @NonNull Player player, @NonNull LivingEntity target, @NonNull InteractionHand type) {
            target.setAttached(Guaranteed_profit.AMPUTEE, !target.getAttachedOrElse(Guaranteed_profit.AMPUTEE, false));

            return InteractionResult.SUCCESS;
    }
}
