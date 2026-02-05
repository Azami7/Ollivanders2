package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.HIGHER_SKILL;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Baruffio's Brain Elixir - a potion that doubles the power of all spells cast.
 *
 * <p>When consumed, this potion applies the HIGHER_SKILL effect to the player, which
 * doubles the modifier used when calculating spell power and success rates. This makes
 * all spells cast during the effect duration significantly more effective and more likely
 * to succeed.</p>
 *
 * @author Azami7
 * @author cakenggt
 */
public final class BARUFFIOS_BRAIN_ELIXIR extends O2Potion {
    /**
     * Constructor for Baruffio's Brain Elixir potion.
     *
     * <p>Initializes the potion with its ingredients (Runespoor Egg, Ginger Root, and Standard
     * Potion Ingredients), description text, flavor text, and potion color. Sets up the recipe
     * and the HIGHER_SKILL effect that will be applied when the potion is consumed.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public BARUFFIOS_BRAIN_ELIXIR(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.BARUFFIOS_BRAIN_ELIXIR;

        ingredients.put(O2ItemType.RUNESPOOR_EGG, 1);
        ingredients.put(O2ItemType.GINGER_ROOT, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        potionColor = Color.fromRGB(255, 251, 222);
        potionSuccessMessage = "You feel clarity of thought.";

        text = "Baruffio's Brain Elixir is a potion that increases the taker's brain power. All spells cast are twice as powerful.";
        flavorText.add("\"I've performed tests on the potion sample you collected. My best guess is that it was supposed to be Baruffio's Brain Elixir. Now, that's a potion which doesn't work at the best of times, but whoever brewed this was seriously incompetent! Forget boosting one's brain; this concoction would most likely melt it!\" —Gethsemane Prickle");
    }

    /**
     * Drink Baruffio's Brain Elixir and gain temporary spell power enhancement.
     *
     * <p>Applies the HIGHER_SKILL effect to the player for the duration defined by the potion's
     * duration field. The HIGHER_SKILL effect doubles the player's skill modifier, which increases
     * the power of all spells cast and doubles their success rate during the effect's duration.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        HIGHER_SKILL effect = new HIGHER_SKILL(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}