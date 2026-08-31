package vance.profit.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import vance.profit.components.ModComponents;
import vance.profit.item.ModItems;

import java.util.*;


public class CrazySlotsItem extends Item {
    public CrazySlotsItem(Properties settings) {
        super(settings);
    }

    @Override
    public @NonNull InteractionResult use(Level world, @NonNull Player player, @NonNull InteractionHand hand) {

        if (!world.isClientSide()) {
            Random random = new Random();
            int randomNumber = random.nextInt(6) + 1;
            int luck = (int) Math.floor(Objects.requireNonNull(player.getAttribute(Attributes.LUCK)).getValue());
            if (luck >= 0) {
                randomNumber = Math.min((luck + randomNumber), 6);
            }
            ItemStack newItem = getWeaponForNumber(randomNumber);

            ItemEnchantments enchants = player.getItemInHand(hand).get(DataComponents.ENCHANTMENTS);
            ItemEnchantments[] d = {enchants,enchants,enchants,enchants,enchants,enchants};
            ArrayList<ItemEnchantments> defaultEnchants = new ArrayList<>(Arrays.asList(d));
            List<ItemEnchantments> previousEnchants = player.getItemInHand(hand).get(ModComponents.TRANSFORMABLE_ENCHANTS);
            List<ItemStack> ammo = new ArrayList<>(1);
            ammo.add(new ItemStack(Items.SPECTRAL_ARROW));

            if (randomNumber <= 3) {
                if (isHealthLow(player)) {
                    ItemStack newLuckyItem = getWeaponForNumber(randomNumber + 3);
                    player.setItemInHand(hand, newLuckyItem);
                    player.getItemInHand(hand).set(ModComponents.WEAPON_ID, randomNumber + 3);
                } else if (randomNumber == 3) {
                    player.setItemInHand(hand, newItem);
                    player.getItemInHand(hand).set(DataComponents.ITEM_NAME, Component.translatable("item.guaranteed_profit.crazy_crossbow"));
                    player.getItemInHand(hand).set(DataComponents.RARITY, Rarity.RARE);
                    player.getItemInHand(hand).set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.ofNonEmpty(ammo));
                    player.getItemInHand(hand).set(ModComponents.WEAPON_ID, randomNumber);

                } else {player.setItemInHand(hand, newItem);
                    player.getItemInHand(hand).set(ModComponents.WEAPON_ID, randomNumber);
                }

            } else {player.setItemInHand(hand, newItem);
                player.getItemInHand(hand).set(ModComponents.WEAPON_ID, randomNumber);
            }

            player.getItemInHand(hand).set(ModComponents.ORIGINALITEM, ModItems.CRAZY_SLOTS.getDefaultInstance());
            player.getItemInHand(hand).set(ModComponents.TRANSFORMABLE, false);
            player.getItemInHand(hand).set(DataComponents.UNBREAKABLE, Unit.INSTANCE);

            player.getItemInHand(hand).set(ModComponents.TRANSFORMABLE_ENCHANTS, Objects.requireNonNullElse(previousEnchants, defaultEnchants));

            if (previousEnchants != null && !previousEnchants.isEmpty()) {
                ItemEnchantments selectedEnchants = previousEnchants.get(randomNumber -1);
                player.getItemInHand(hand).set(DataComponents.ENCHANTMENTS, selectedEnchants);
            }

            world.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 0.55f, 1.25f);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }




    private ItemStack getWeaponForNumber(int number) {
        return switch (number) {
            case 1 -> new ItemStack(ModItems.CRAZY_AXE);
            case 2 -> new ItemStack(ModItems.CRAZY_TRIDENT);
            case 3 -> new ItemStack(Items.CROSSBOW);
            case 4 -> new ItemStack(ModItems.CRAZY_MACE);
            case 5 -> new ItemStack(ModItems.CRAZY_SCYTHE);
            case 6 -> new ItemStack(ModItems.CRAZY_SWORD);
            default -> ItemStack.EMPTY;
        };
    }

    /*
            case 1 -> new ItemStack(ModItems.CRAZY_AXE);
            case 2 -> new ItemStack(ModItems.CRAZY_TRIDENT);
            case 3 -> new ItemStack(Items.CROSSBOW);
            case 4 -> new ItemStack(ModItems.CRAZY_MACE);
            case 5 -> new ItemStack(ModItems.CRAZY_SCYTHE);
            case 6 -> new ItemStack(ModItems.CRAZY_SWORD);
     */

    private Boolean isHealthLow(Player player) {
        return player.getHealth() <= 8.0F;
    }
}




