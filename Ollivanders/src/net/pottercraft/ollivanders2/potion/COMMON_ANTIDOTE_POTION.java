package net.pottercraft.ollivanders2.potion;

import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Common Antidote Potion - counteracts ordinary poisons and toxins.
 *
 * <p>This potion removes the poison effect from a player when consumed. It is effective
 * against ordinary poisons such as creature bites and stings (Minecraft POISON effects).
 * If no poison is currently affecting the player, the potion has no mechanical effect
 * but provides flavor text feedback.</p>
 *
 * @see <a href="http://harrypotter.wikia.com/wiki/Antidote_to_Common_Poisons">Harry Potter Wiki - Antidote to Common Poisons</a>
 *
 * @author Azami7
 * @since 2.2.7
 */
public final class COMMON_ANTIDOTE_POTION extends O2Potion {
    // todo lower strength so that we can also have the stronger antidote to uncommon poisons for longer duration poison

    /**
     * Message sent to player when potion has no effect.
     *
     * <p>This message is displayed when the player drinks the potion but is not currently poisoned.
     * It provides flavor text feedback while having no mechanical effect.</p>
     */
    String potionDoNothingMessage = "You smell a faint odor that reminds you of winter, then it is gone.";
    /**
     * Constructor for Common Antidote Potion.
     *
     * <p>Initializes the potion with its ingredients (Mistletoe Berries, Bezoar, Unicorn Hair,
     * and Standard Potion Ingredients), description text, and potion color. Sets up the recipe
     * for brewing this antidote potion.</p>
     *
     * @param plugin a callback to the plugin instance
     */
    public COMMON_ANTIDOTE_POTION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        potionType = O2PotionType.COMMON_ANTIDOTE_POTION;

        ingredients.put(O2ItemType.MISTLETOE_BERRIES, 2);
        ingredients.put(O2ItemType.BEZOAR, 1);
        ingredients.put(O2ItemType.UNICORN_HAIR, 1);
        ingredients.put(O2ItemType.STANDARD_POTION_INGREDIENT, 2);

        potionColor = Color.TEAL;
        potionSuccessMessage = "A cool feeling flows through your body as the poison is removed.";

        text = "Counteracts ordinary poisons, such as creature bites and stings.";
    }

    /**
     * Drink the Common Antidote Potion and remove poison effects.
     *
     * <p>If the player is currently poisoned (has the POISON effect), it is immediately removed
     * and the player receives confirmation feedback. If the player is not poisoned, the potion
     * still consumes but provides only flavor text feedback with no mechanical effect.</p>
     *
     * @param player the player who drank the potion
     */
    @Override
    public void drink(@NotNull Player player) {
        if (player.hasPotionEffect(PotionEffectType.POISON)) {
            player.removePotionEffect(PotionEffectType.POISON);

            player.sendMessage(Ollivanders2.chatColor + potionSuccessMessage);
        }
        else {
            player.sendMessage(Ollivanders2.chatColor + potionDoNothingMessage);
        }
    }

    /**
     * Get the message displayed when the potion has no effect.
     *
     * <p>Returns the flavor text message shown to the player when they drink this potion
     * while not currently poisoned.</p>
     *
     * @return the no-effect message string
     */
    public String getPotionDoNothingMessage() {
        return potionDoNothingMessage;
    }
}