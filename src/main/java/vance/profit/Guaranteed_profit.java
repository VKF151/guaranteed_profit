package vance.profit;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vance.profit.block.ModBlocks;
import vance.profit.block.custom.entity.ModBlockEntities;
import vance.profit.client.entity.RouletteTableRenderer;
import vance.profit.client.model.RouletteTableModel;
import vance.profit.client.model.RouletteWheelModel;
import vance.profit.codec.AcceptedCurrencies;
import vance.profit.components.ModComponents;
import vance.profit.entity.ModEntityTypes;
import vance.profit.events.CrossbowFireCallback;
import vance.profit.item.ModItems;
import vance.profit.world.ModGameRules;
import vance.profit.world.SavedGamblingData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Guaranteed_profit implements ModInitializer {
	public static final String MOD_ID = "guaranteed_profit";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final SimpleParticleType FULL_SWEEP_PARTICLE = FabricParticleTypes.simple();
	public static final AttachmentType<Boolean> AMPUTEE = AttachmentRegistry.create(
			Identifier.fromNamespaceAndPath(MOD_ID, "amputee"),
			booleanBuilder -> booleanBuilder
					.initializer(() -> false)
					.syncWith(ByteBufCodecs.BOOL,
							AttachmentSyncPredicate.all())
					.persistent(Codec.BOOL)
					.copyOnDeath()

	);
	public static final RenderStateDataKey<Boolean> FLESH_WOUND = RenderStateDataKey.create(() -> "flesh_wound");

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		AcceptedCurrencies.loadCurrencies();
		ModComponents.initialize();
		ModGameRules.initialize();
		ModEntityTypes.initialize();

		Registry.register(BuiltInRegistries.PARTICLE_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "full_sweep"), FULL_SWEEP_PARTICLE);

		AttackEntityCallback.EVENT.register((playerEntity, world, hand, entity, entityHitResult) -> {
			ItemStack stack = playerEntity.getMainHandItem();
			if (stack.get(ModComponents.ORIGINALITEM) != null && !playerEntity.isSpectator()) {
				stack.set(ModComponents.TRANSFORMABLE, true);
			}
			return InteractionResult.PASS;
		});

		UseItemCallback.EVENT.register((playerEntity, world, hand) -> {
			ItemStack stack = playerEntity.getMainHandItem();
			ItemStack originalItem = stack.get(ModComponents.ORIGINALITEM);
			Integer weaponId = stack.get(ModComponents.WEAPON_ID);
			ItemEnchantments weaponEnchants = stack.getEnchantments();


			if (Boolean.TRUE.equals(stack.get(ModComponents.TRANSFORMABLE)) && !playerEntity.isSpectator()) {
					List<ItemEnchantments> enchantsList = new ArrayList<>();
					List<ItemEnchantments> existing = stack.get(ModComponents.TRANSFORMABLE_ENCHANTS);
					if (existing != null) {
						enchantsList.addAll(existing);
					}

					if (weaponId != null) {
						if (weaponId >= 0 && weaponId <= enchantsList.size()) {
							enchantsList.set(weaponId -1, weaponEnchants);
						}
					}
                    assert originalItem != null;
                    playerEntity.setItemInHand(hand, originalItem);
                    originalItem.set(ModComponents.TRANSFORMABLE_ENCHANTS, enchantsList);

					world.playSound(null, playerEntity.blockPosition(),
							SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.PLAYERS,
							0.35f, 1.0f);
			}
			return InteractionResult.PASS;
		});

		CrossbowFireCallback.EVENT.register((player, level, hand) -> {
			ItemStack mainHandItem = player.getMainHandItem();
			ItemStack originalItem = mainHandItem.get(ModComponents.ORIGINALITEM);
			Integer weaponId = mainHandItem.get(ModComponents.WEAPON_ID);
			ItemEnchantments weaponEnchants = mainHandItem.getEnchantments();


			if (mainHandItem.get(ModComponents.TRANSFORMABLE) != null && !player.isSpectator()) {
				List<ItemEnchantments> enchantsList = new ArrayList<>();
				List<ItemEnchantments> existing = mainHandItem.get(ModComponents.TRANSFORMABLE_ENCHANTS);
				if (existing != null) {
					enchantsList.addAll(existing);
				}

				if (weaponId != null) {
					if (weaponId >= 0 && weaponId <= enchantsList.size()) {
						enchantsList.set(weaponId -1, weaponEnchants);
					}
				}
				assert originalItem != null;
				player.setItemInHand(hand, originalItem);
				originalItem.set(ModComponents.TRANSFORMABLE_ENCHANTS, enchantsList);

				level.playSound(null, player.blockPosition(),
						SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.PLAYERS,
						0.35f, 1.0f);

			}
			return InteractionResult.PASS;
		});

		ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipFlag, lines) -> {
			if (stack.has(ModComponents.HOARD_AMOUNT)) {
				lines.add(Component.literal(String.valueOf(stack.get(ModComponents.HOARD_AMOUNT))));
			}
		});

	}
}