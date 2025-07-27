package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Weakness potion add the Weakness potion effect.
 *
 * @author Azami7
 * @since 2.21
 */
public class WEAKNESS_POTION extends O2Potion {
    public WEAKNESS_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        // potion config
        potionType = O2PotionType.WEAKNESS_POTION;
        potionColor = Color.fromRGB(244, 164, 96); // light brown
        duration = Ollivanders2Common.ticksPerMinute * 5;

        // ingredients
        ingredients.put(O2ItemType.FLOBBERWORM_MUCUS, 1);
        ingredients.put(O2ItemType.VALERIAN_ROOT, 2);
        ingredients.put(O2ItemType.LEECH_JUICE, 1);
        ingredients.put(O2ItemType.DOXY_VENOM, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        // spellbook text
        text = "The weakness potion will drain the drinker of strength.";

        // Weakness II
        minecraftPotionEffect = new PotionEffect(PotionEffectType.WEAKNESS, duration, 1);
    }

    /**
     * Drink this potion and do effects
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        player.sendMessage(Ollivanders2.chatColor + "You feel drained of strength.");
    }
}
