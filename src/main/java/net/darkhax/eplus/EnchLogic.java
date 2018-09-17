package net.darkhax.eplus;

import java.util.ArrayList;
import java.util.List;

import net.darkhax.eplus.api.event.EnchantmentCostEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public final class EnchLogic {

    public static int calculateNewEnchCost (Enchantment enchantment, int level) {

        // Base cost is equal to roughly 2.5 levels of EXP.
        int cost = 25;

        // Cost is multiplied up to 10, based on rarity of the enchant.
        // Rarer the enchant, higher the cost.
        cost *= Math.max(11 - enchantment.getRarity().getWeight(), 1);

        // Linear cost increase based on level.
        cost *= level;

        // The cost factor is applied. Default is 1.5.
        cost *= ConfigurationHandler.costFactor;

        // Curses cost even more to apply
        if (enchantment.isCurse()) {

            cost *= ConfigurationHandler.curseFactor;
        }

        // Treasures cost more to apply
        if (enchantment.isTreasureEnchantment()) {

            cost *= ConfigurationHandler.treasureFactor;
        }

        final EnchantmentCostEvent event = new EnchantmentCostEvent(cost, enchantment, level);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCost();
    }

    public static List<Enchantment> getValidEnchantments (ItemStack stack) {

        // TODO add whitelist and blacklist
        final List<Enchantment> enchList = new ArrayList<>();

        if (!stack.isEmpty() && (stack.isItemEnchantable() || stack.isItemEnchanted())) {

            for (final Enchantment enchantment : Enchantment.REGISTRY) {

                if (enchantment.canApply(stack) && !enchantment.isCurse() && !enchantment.isTreasureEnchantment()) {

                    enchList.add(enchantment);
                }
            }
        }

        return enchList;
    }

    public static void removeExperience (EntityPlayer player, int amount) {

        player.experience -= (float) amount / (float) player.xpBarCap();

        for (player.experienceTotal -= amount; player.experience <= 0.0F; player.experience /= player.xpBarCap()) {
            player.experience = (player.experience + 1.0F) * player.xpBarCap();
            player.addExperienceLevel(-1);
        }
    }
}