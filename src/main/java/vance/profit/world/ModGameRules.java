package vance.profit.world;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import vance.profit.Guaranteed_profit;

public class ModGameRules {
    public static final GameRule<Integer> SLOT_MACHINE_CHANCE_GAMERULE = GameRuleBuilder
            .forInteger(5)
            .category(GameRuleCategory.MISC)
            .buildAndRegister(Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "slot_machine_chance")
            );

    public static void initialize() {}
}
