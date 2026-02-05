package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.WEAKNESS;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Weakness Potion - significantly reduces the drinker's physical strength.
 *
 * <p>When consumed, this potion applies the WEAKNESS II effect to the player for 5 minutes,
 * drastically reducing their melee damage output and overall physical strength. This is a
 * debilitating potion that makes combat much more difficult and is useful for handicapping
 * opponents or creating challenging gameplay scenarios.</p>
 *
 * <p>While weakened, players deal significantly less damage with all melee attacks and may
 * struggle with physically demanding tasks. The weakness effect can be strategically used in
 * PvP situations or as an obstacle in puzzles and challenges.</p>
 *
 * @author Azami7
 * @since 2.21
 */
public class WEAKNESS_POTION extends O2Potion {
    /**
     * Constructor for Weakness Potion.
     *
     * <p>Initializes the potion with its ingredients (Flobberworm Mucus, Valerian Root, Leech
     * Juice, Doxy Venom, and Standard Potion Ingredients), description text, potion color, and
     * the Weakness II potion effect. Sets up the 5-minute duration for the weakness effect.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public WEAKNESS_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        // potion config
        potionType = O2PotionType.WEAKNESS_POTION;

        potionColor = Color.fromRGB(244, 164, 96); // light brown
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "You feel drained of strength.";

        // ingredients
        ingredients.put(O2ItemType.FLOBBERWORM_MUCUS, 1);
        ingredients.put(O2ItemType.VALERIAN_ROOT, 2);
        ingredients.put(O2ItemType.LEECH_JUICE, 1);
        ingredients.put(O2ItemType.DOXY_VENOM, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        // spellbook text
        text = "The weakness potion will drain the drinker of strength.";
    }

    /**
     * Drink the Weakness Potion and suffer reduced strength.
     *
     * <p>Applies the WEAKNESS II effect to the player for 5 minutes. This effect significantly
     * reduces the player's melee damage output, making all attacks deal much less damage. While
     * weakened, players will struggle in combat situations and may have difficulty with
     * physically demanding tasks. The weakness effect is temporary and will wear off after
     * the duration expires.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        WEAKNESS effect = new WEAKNESS(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}
