package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Ovomancy divination spell implementation using egg pattern interpretation.
 * <p>
 * Ovomancy is a form of divination that involves cracking open eggs and observing the patterns formed by the
 * yolks and whites as they spill. The shapes and formations of the egg contents—whether they resemble bells,
 * serpents, boats, or other symbolic forms—reveal insights into future events. This ancient divination method,
 * rooted in classical traditions like those taught by Orpheus, interprets these natural patterns as messages
 * from fate.
 * </p>
 * <p>
 * This class implements the ovomancy divination method, generating randomized prophecies based on egg pattern
 * readings. The prophecies are created by combining randomly selected prefixes (which reference egg shapes,
 * yolk formations, and symbolic omens) with divination text from the parent {@link O2Divination} class. Ovomancy
 * has relatively high maximum accuracy (40 points), making it more reliable than crystal ball and most basic
 * divination methods, approaching the power of tarot divination.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Ovomancy">Harry Potter Wiki - Ovomancy</a>
 */
public class OVOMANCY extends O2Divination {
    /**
     * Constructor that initializes an ovomancy divination prophecy.
     * <p>
     * Creates a new ovomancy divination instance and populates it with egg pattern prophecy prefixes.
     * Sets the divination type to OVOMANCY with a maximum accuracy of 40 points. The prophecy prefixes reference
     * the shapes and patterns formed by cracking eggs (bell, serpent, boat shapes) and the symbolic interpretation
     * of these formations, rooted in classical divination traditions taught by Orpheus.
     * </p>
     *
     * @param plugin     a callback to the plugin for accessing configuration and other resources
     * @param prophet    the player who is performing the divination (casting the ovomancy spell)
     * @param target     the player who is the subject of the divination prophecy
     * @param experience the experience level of the prophet, affecting prophecy accuracy and strength
     */
    public OVOMANCY(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        // Set divination type and accuracy threshold. Ovomancy has relatively high accuracy (40), more reliable
        // than crystal ball (30) and basic methods, approaching tarot (35) in power
        divinationType = O2DivinationType.OVOMANCY;
        maxAccuracy = 40;

        // Populate prophecy prefixes with ovomancy divination phrases. These prefixes are randomly selected
        // when generating a prophecy and combined with divination text to create the final prophecy message.
        // The prefixes reference egg pattern interpretation, where yolk and white formations take symbolic shapes:
        // - Bell shape: represents clarity and communication
        // - Snake shape: represents transformation and wisdom
        // - Boat shape: represents journey and change
        // These natural patterns are interpreted according to classical traditions, particularly those of Orpheus.
        prophecyPrefix.add("The shape of the egg whites means that");
        prophecyPrefix.add("Through the teachings of Orpheus, it is foretold that");
        prophecyPrefix.add("The omen of the egg reveals that");
        prophecyPrefix.add("Egg whites and yolks take form,");
        prophecyPrefix.add("The egg forms the shape of a bell,");
        prophecyPrefix.add("The egg forms the shape of a snake,");
        prophecyPrefix.add("The egg takes the shape of a boat,");
    }
}
