package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Cartomancy Tarot divination spell implementation using major arcana cards.
 * <p>
 * Tarot cartomancy is a specialized form of cartomancy that uses a complete tarot deck, particularly
 * the 22 major arcana cards. These cards have deeper symbolic meaning and are considered more accurate
 * than regular playing card divination. Each major arcana card represents a significant life principle
 * or archetypal concept (The Fool, The Magician, The High Priestess, Death, The Tower, etc.).
 * </p>
 * <p>
 * This class implements the tarot cartomancy divination method, generating randomized prophecies based on
 * major arcana card readings. The prophecies are created by combining randomly selected prefixes (which reference
 * specific tarot major arcana cards and their symbolic meanings) with divination text from the parent
 * {@link O2Divination} class. Tarot divination has higher maximum accuracy (35 points) compared to regular
 * cartomancy (25 points), reflecting the greater power and precision of tarot-based prophecies.
 * </p>
 *
 * @author Azami7
 * @see <a href="http://harrypotter.wikia.com/wiki/Cartomancy">Harry Potter Wiki - Cartomancy</a>
 */
public class CARTOMANCY_TAROT extends O2Divination {
    /**
     * Constructor that initializes a tarot cartomancy divination prophecy.
     * <p>
     * Creates a new tarot cartomancy divination instance and populates it with major arcana card prophecy prefixes.
     * Sets the divination type to CARTOMANCY_TAROT with a maximum accuracy of 35 points (higher than regular
     * cartomancy at 25 points). The prophecy prefixes reference the 22 major arcana cards and their archetypal
     * symbolic meanings, which represent significant life principles and transformative forces.
     * </p>
     *
     * @param plugin     a callback to the plugin for accessing configuration and other resources
     * @param prophet    the player who is performing the divination (casting the tarot cartomancy spell)
     * @param target     the player who is the subject of the divination prophecy
     * @param experience the experience level of the prophet, affecting prophecy accuracy and strength
     */
    public CARTOMANCY_TAROT(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        // Set divination type and accuracy threshold. Tarot cartomancy has higher accuracy (35) than regular cartomancy (25)
        divinationType = O2DivinationType.CARTOMANCY_TAROT;
        maxAccuracy = 35;

        // Populate prophecy prefixes with tarot major arcana cards. These prefixes are randomly selected
        // when generating a prophecy and combined with divination text to create the final prophecy message.
        // The prefixes reference the 22 major arcana cards from a traditional tarot deck.
        // Each major arcana card represents a fundamental life principle or archetypal force:
        // - The Fool: new beginnings, innocence, risk
        // - The Magician: manifestation, resourcefulness, power
        // - The High Priestess: intuition, mystery, inner knowledge
        // - The Emperor: authority, leadership, father figure
        // - The Empress: femininity, beauty, abundance
        // - The Hierophant: tradition, convention, morality
        // - The Chariot: control, willpower, determination
        // - The Hanged Man: pause, surrender, perspective
        // - Death: transformation, endings, renewal
        // - Wheel of Fortune: luck, destiny, turning point
        // - The Devil: bondage, materialism, playfulness
        // - The Tower (Lightning-Struck Tower): upheaval, sudden change, revelation
        // - The Star: hope, spirituality, inspiration
        // - The Moon: illusion, fear, intuition
        // - Judgement: awakening, renewal, inner calling
        prophecyPrefix.add("The cards have revaled that");
        prophecyPrefix.add("The reading of the cards says that");
        prophecyPrefix.add("The Lightning-Struck Tower is revealed,");
        prophecyPrefix.add("The Star is revealed,");
        prophecyPrefix.add("The High Priestess is revealed,");
        prophecyPrefix.add("The Hanged Man is revealed,");
        prophecyPrefix.add("Judgement is revealed,");
        prophecyPrefix.add("The Emperor is revealed,");
        prophecyPrefix.add("Wheel of Fortune is revealed,");
        prophecyPrefix.add("The Chariot is revealed,");
        prophecyPrefix.add("Death is revealed,");
        prophecyPrefix.add("The Hierophant is revealed,");
        prophecyPrefix.add("The Hanged Man is revealed,");
        prophecyPrefix.add("The Moon is revealed,");
        prophecyPrefix.add("The Empress is revealed,");
        prophecyPrefix.add("The Devil is revealed,");
        prophecyPrefix.add("The Fool is revealed,");
    }
}
