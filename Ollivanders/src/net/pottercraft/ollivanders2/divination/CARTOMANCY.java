package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Cartomancy divination spell implementation using tarot card reading.
 * <p>
 * Cartomancy is the art of reading cards (typically tarot cards) to gain insight into future events,
 * personality traits, and circumstances.
 * </p>
 * <p>
 * This class implements the cartomancy divination method, generating randomized prophecies based on tarot
 * card readings. The prophecies are created by combining randomly selected prefixes (which reference specific
 * tarot cards and their traditional meanings) with divination text from the parent {@link O2Divination} class.
 * Cards used include spades (associated with challenges and misfortune) and their interpretations based on
 * classical tarot traditions.
 * </p>
 *
 * @author Azami7
 * @see <a href="http://harrypotter.wikia.com/wiki/Cartomancy">Harry Potter Wiki - Cartomancy</a>
 */
public class CARTOMANCY extends O2Divination {
    /**
     * Constructor that initializes a cartomancy divination prophecy.
     * <p>
     * Creates a new cartomancy divination instance and populates it with tarot card prophecy prefixes.
     * Sets the divination type to CARTOMANCY with a maximum accuracy of 25 points (higher than other divination methods).
     * The prophecy prefixes reference specific tarot cards and their traditional meanings (particularly spades,
     * which are associated with challenges, conflict, and misfortune).
     * </p>
     *
     * @param plugin     a callback to the plugin for accessing configuration and other resources
     * @param prophet    the player who is performing the divination (casting the cartomancy spell)
     * @param target     the player who is the subject of the divination prophecy
     * @param experience the experience level of the prophet, affecting prophecy accuracy and strength
     */
    public CARTOMANCY(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        // Set divination type and accuracy threshold. Cartomancy has higher accuracy (25) than astrology (20)
        divinationType = O2DivinationType.CARTOMANCY;
        maxAccuracy = 25;

        // Populate prophecy prefixes with tarot card references. These prefixes are randomly selected
        // when generating a prophecy and combined with divination text to create the final prophecy message.
        // The prefixes reference:
        // 1. General card reading phrases (generic fortune-telling intros)
        // 2. Specific tarot cards from the suit of spades (associated with conflict and misfortune)
        // Each card carries traditional tarot meanings that are incorporated into the prophecy.
        prophecyPrefix.add("The cards have revaled that");
        prophecyPrefix.add("The reading of the cards says that");
        prophecyPrefix.add("Two of spades: conflict,");
        prophecyPrefix.add("Seven of spades: an ill omen,");
        prophecyPrefix.add("Ten of spades: violence,");
        prophecyPrefix.add("Ten of spades: violence. Knave of spades: a dark young man, possibly troubled, one who dislikes the questioner,");
    }
}
