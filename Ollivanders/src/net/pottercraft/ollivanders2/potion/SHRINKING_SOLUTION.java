package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.SHRINKING;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Shrinking Solution - causes the drinker to shrink in size.
 *
 * <p>When consumed, this potion applies the SHRINKING effect to the player for 5 minutes,
 * causing them to become smaller in physical size. The shrinking effect can impact movement,
 * interaction with the world, and visibility. This potion is primarily a transformative effect
 * useful for puzzle-solving, exploration, or roleplay scenarios.</p>
 *
 * <p>The shrinking effect is temporary and will wear off after the duration expires, returning
 * the player to their normal size.</p>
 *
 * @author Azami7
 */
public class SHRINKING_SOLUTION extends O2Potion {
    /**
     * Constructor for Shrinking Solution potion.
     *
     * <p>Initializes the potion with its ingredients (Standard Potion Ingredients), potion color,
     * and the SHRINKING effect. Sets up the 5-minute duration for the shrinking effect that will
     * be applied when the potion is consumed.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public SHRINKING_SOLUTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        // ingredients
        ingredients.put(O2ItemType.RAT_SPLEEN, 1);
        ingredients.put(O2ItemType.SLICED_CATERPILLARS, 2);
        ingredients.put(O2ItemType.SHRIVELIG, 2);
        ingredients.put(O2ItemType.LEECH_JUICE, 1);
        ingredients.put(O2ItemType.DAISY_ROOTS, 2);
        ingredients.put(O2ItemType.INFUSION_OF_COWBANE, 1);
        ingredients.put(O2ItemType.INFUSION_OF_WORMWOOD, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        // potion config
        potionType = O2PotionType.SHRINKING_SOLUTION;

        potionColor = Color.fromRGB(218, 165, 32); // goldenrod
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "You feel yourself compressing.";

        // spellbook text
        text = "";
        flavorText.add("");
    }

    /**
     * Drink the Shrinking Solution and become smaller in size.
     *
     * <p>Applies the SHRINKING effect to the player for 5 minutes. This effect causes the player
     * to physically shrink in size, which can affect their movement, interactions with the world,
     * and visibility. The shrinking effect is temporary and will wear off after the duration expires.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        // add the shrinking effect
        SHRINKING effect = new SHRINKING(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}
