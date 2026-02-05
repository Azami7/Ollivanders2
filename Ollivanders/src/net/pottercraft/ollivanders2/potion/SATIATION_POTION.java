package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.SATIATION;
import net.pottercraft.ollivanders2.item.O2ItemType;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Satiation Potion - restores food saturation and satisfies hunger.
 *
 * <p>When consumed, this potion applies the SATIATION effect to the player for
 * 5 minutes. The Satiation effect restores the player's food saturation level without consuming
 * actual food, allowing them to go longer between meals. This is particularly useful for survival
 * scenarios or extended activities where food is limited.</p>
 *
 * <p>Unlike eating food, saturation effects prevent hunger decay and provide nourishment without
 * requiring food items. The saturation from this potion works in tandem with the food bar to
 * prevent health loss from starvation.</p>
 *
 * @author Azami7
 * @since 2.21
 */
public class SATIATION_POTION extends O2Potion {
    /**
     * Constructor for Satiation Potion.
     *
     * <p>Initializes the potion with its ingredients (Chopped Mallow Leaves, Knotgrass, Honeywater,
     * and Standard Potion Ingredients), description text, potion color, and the Saturation I potion
     * effect. Sets up the 5-minute duration for the saturation effect.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public SATIATION_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        // potion config
        potionType = O2PotionType.SATIATION_POTION;

        // ingredients
        ingredients.put(O2ItemType.CHOPPED_MALLOW_LEAVES, 2);
        ingredients.put(O2ItemType.KNOTGRASS, 1);
        ingredients.put(O2ItemType.HONEYWATER, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 3);

        potionColor = Color.fromRGB(248, 125, 35); // warm orange
        duration = Ollivanders2Common.ticksPerMinute * 5;
        potionSuccessMessage = "You feel completely satisfied and nourished.";

        // spellbook text
        text = "The satiation potion will satisfy the drinker's hunger and restore their energy.";
    }

    /**
     * Drink the Satiation Potion and restore food saturation.
     *
     * <p>Applies the Satiation effect to the player for 5 minutes. This effect
     * restores and maintains the player's food saturation level, preventing hunger decay and
     * starvation damage. The player will feel completely satisfied and nourished while the
     * effect is active, allowing them to focus on other tasks without worrying about hunger.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        SATIATION effect = new SATIATION(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}