package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.effect.AWAKE;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Wideye Potion (Awakening Potion) - grants immunity to sleep effects.
 *
 * <p>When consumed, this potion applies the AWAKE effect to the player, preventing them from
 * being put to sleep by sleep-inducing potions such as the Sleeping Draught or Draught of Living
 * Death. While the AWAKE effect is active, the player is immune to all sleep-related magical
 * effects and cannot be forced into sleep.</p>
 *
 * <p>This potion serves as a critical counter to sleeping potions and is essential for survival
 * in situations where sleep effects might be used as an attack or punishment. Players with the
 * AWAKE effect remain fully conscious and able to act.</p>
 *
 * @author Azami7
 * @since 2.2.8
 */
public class WIDEYE_POTION extends O2Potion {
    /**
     * Constructor for Wideye Potion (Awakening Potion).
     *
     * <p>Initializes the potion with its ingredients (Ground Snake Fangs, Billywig Sting Slime,
     * Wolfsbane, and Standard Potion Ingredients), description text, and potion color. Sets up
     * the AWAKE effect that will be applied when the potion is consumed to grant sleep immunity.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public WIDEYE_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.WIDEYE_POTION;

        ingredients.put(O2ItemType.GROUND_SNAKE_FANGS, 3);
        ingredients.put(O2ItemType.BILLYWIG_STING_SLIME, 6);
        ingredients.put(O2ItemType.WOLFSBANE, 2);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 4);

        potionColor = Color.fromRGB(13, 55, 13);
        potionSuccessMessage = "You feel alert.";

        text = "The Wideye Potion, also known as the Awakening Potion, is used to prevent the drinker from falling asleep.";
    }

    /**
     * Drink the Wideye Potion and become immune to sleep effects.
     *
     * <p>Applies the AWAKE effect to the player for the default potion duration. While this effect
     * is active, the player is completely immune to all sleep-inducing potions and effects,
     * including the Sleeping Draught and Draught of Living Death. The AWAKE effect is a critical
     * defensive tool for countering sleep-based attacks and protecting against magical incapacitation.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        AWAKE effect = new AWAKE(p, duration, false, player.getUniqueId());
        Ollivanders2API.getPlayers().playerEffects.addEffect(effect);

        player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
    }
}
