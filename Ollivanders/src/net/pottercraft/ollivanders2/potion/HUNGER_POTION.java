package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.HUNGER;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Hunger Potion - causes severe hunger and exhaustion in the drinker.
 *
 * <p>When consumed, this potion applies the HUNGER effect (Hunger I) to the player for 3 minutes.
 * The Hunger effect causes rapid food depletion, forcing the player to consume food frequently
 * to avoid starvation. This is primarily a negative/debilitating potion effect useful for
 * challenging gameplay scenarios.</p>
 *
 * <p>The hunger effect reduces the player's food saturation and can lead to damage if not
 * counteracted with food consumption during the effect duration.</p>
 *
 * @author Azami7
 * @since 2.21
 */
public class HUNGER_POTION extends O2Potion {
    /**
     * Constructor for Hunger Potion.
     *
     * <p>Initializes the potion with its ingredients (Rotten Flesh, Ground Scarab Beetle, Crushed
     * Fire Seeds, Crushed Gurdyroot, Infusion of Wormwood, and Standard Potion Ingredients),
     * description text, potion color, and the Hunger I potion effect. Sets up the 3-minute
     * duration for the hunger effect.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public HUNGER_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.HUNGER_POTION;

        // ingredients
        ingredients.put(O2ItemType.ROTTEN_FLESH, 2);
        ingredients.put(O2ItemType.GROUND_SCARAB_BEETLE, 1);
        ingredients.put(O2ItemType.CRUSHED_FIRE_SEEDS, 1);
        ingredients.put(O2ItemType.CRUSHED_GURDYROOT, 1);
        ingredients.put(O2ItemType.INFUSION_OF_WORMWOOD, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        potionColor = Color.fromRGB(88, 118, 83); // dark green
        duration = Ollivanders2Common.ticksPerMinute * 3;
        potionSuccessMessage = "You feel ravenously hungry.";

        // spellbook text
        text = "The hunger potion will cause the drinker to feel ravenous.";
    }

    /**
     * Drink the Hunger Potion and suffer from the hunger effect.
     *
     * <p>Applies the HUNGER effect (Hunger I) to the player for 3 minutes. This effect causes
     * rapid food depletion and food bar exhaustion. The player will need to consume food
     * frequently to avoid taking damage from starvation.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        HUNGER effect = new HUNGER(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}