package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.IMPROVED_BOOK_LEARNING;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wit-Sharpening Potion - enhances mental clarity and learning ability.
 *
 * <p>When consumed, this potion applies the IMPROVED_BOOK_LEARNING effect to the player,
 * significantly enhancing their ability to learn from books and magical instruction. The potion
 * allows the drinker to think more clearly and comprehend complex information more easily. This
 * makes it useful for studying spells, potions, and other magical knowledge from books.</p>
 *
 * <p>The enhanced clarity and learning ability serve as a counteragent to confusion effects, helping
 * players overcome mental fog and disorientation. This potion is particularly valuable for
 * accelerating learning and improving study effectiveness.</p>
 *
 * @author Azami7
 */
public final class WIT_SHARPENING_POTION extends O2Potion {
    /**
     * Constructor for Wit-Sharpening Potion.
     *
     * <p>Initializes the potion with its ingredients (Ginger Root, Ground Scarab Beetle, Armadillo
     * Bile, and Standard Potion Ingredients), description text, flavor text, and potion color.
     * Sets up the IMPROVED_BOOK_LEARNING effect that will be applied when the potion is consumed
     * to enhance mental clarity and learning ability.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public WIT_SHARPENING_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.WIT_SHARPENING_POTION;

        ingredients.put(O2ItemType.GINGER_ROOT, 2);
        ingredients.put(O2ItemType.GROUND_SCARAB_BEETLE, 3);
        ingredients.put(O2ItemType.ARMADILLO_BILE, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        potionColor = Color.fromRGB(204, 102, 0);
        potionSuccessMessage = "You feel ready to learn.";

        text = "The Wit-Sharpening Potion is a potion which allows the drinker to think more clearly. Due to this, it acts "
                + "as a counteragent to the Confundus Charm.";
        flavorText.add("\"Some of you will benefit from today's assignment: Wit-Sharpening Potion. Perhaps you should begin immediately.\" -Severus Snape");
    }

    /**
     * Drink the Wit-Sharpening Potion and enhance mental clarity.
     *
     * <p>Applies the IMPROVED_BOOK_LEARNING effect to the player for the default potion duration.
     * This effect enhances the player's ability to learn from books and magical instruction,
     * allowing them to understand complex concepts more easily and gain knowledge more quickly.
     * The improved mental clarity helps overcome confusion and improves cognitive function.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        IMPROVED_BOOK_LEARNING effect = new IMPROVED_BOOK_LEARNING(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}