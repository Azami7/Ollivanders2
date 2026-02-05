package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.effect.HEAL;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Cure for Boils - a potion that provides instant healing to cure boils and other conditions.
 *
 * <p>This potion cures boils and similar skin conditions, even those produced by the Pimple Jinx curse.
 * When consumed, it applies an Instant Health I effect (restores health) and is marked with a blue color
 * to indicate its healing properties. Despite being called a "cure" for boils, it functions as a general
 * healing potion.</p>
 *
 * <p>Reference: Harry Potter Potions, used to cure boils caused by spells and other sources.</p>
 *
 * @author Azami7
 * @since 2.2.8
 */
public class CURE_FOR_BOILS extends O2Potion {
    /**
     * Constructor for Cure for Boils potion.
     *
     * <p>Initializes the potion with its ingredients (Ground Snake Fangs, Dried Nettles, Ground
     * Porcupine Quills, Horned Slug Mucus, and Standard Potion Ingredients), description text,
     * flavor text, and the Instant Health I potion effect. Sets up the recipe and the healing
     * effect that will be applied when the potion is consumed.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public CURE_FOR_BOILS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.CURE_FOR_BOILS;

        ingredients.put(O2ItemType.GROUND_SNAKE_FANGS, 3);
        ingredients.put(O2ItemType.DRIED_NETTLES, 1);
        ingredients.put(O2ItemType.GROUND_PORCUPINE_QUILLS, 1);
        ingredients.put(O2ItemType.HORNED_SLUG_MUCUS, 4);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 4);

        potionColor = Color.fromRGB(65, 105, 225); // blue
        potionSuccessMessage = "You feel healthier.";

        text = "The Cure for Boils (also known as simply Boil Cure) is a potion which cures boils, even those produced by the Pimple Jinx.";
        flavorText.add("\"Being an effective remedie against pustules, hives, boils and many other scrofulous conditions. This is a robust potion of powerful character. Care should be taken when brewing. Prepared incorrectly this potion has been known to cause boils, rather than cure them...\" -Zygmunt Budge");
    }

    /**
     * Drink the Cure for Boils potion and receive healing.
     *
     * <p>Applies the HEAL effect to the player for the default potion duration. This effect restores
     * the player's health, curing boils and other skin conditions. The healing is instant and allows
     * the player to recover from damage or ailments caused by curses like the Pimple Jinx. The potion
     * displays a success message to confirm the healing has taken effect.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        HEAL effect = new HEAL(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}
