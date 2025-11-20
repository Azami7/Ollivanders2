package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Tasseomancy divination spell implementation using tea-leaf reading and interpretation.
 * <p>
 * Tasseomancy (also known as tea-leaf reading or tessomancy) is the art of interpreting the patterns formed
 * by tea leaves settled at the bottom of a cup. After drinking tea, a diviner examines the remaining leaves to
 * identify symbolic shapes—falcons, skulls, rings, snakes, mountains, and other forms—each carrying specific
 * meanings and omens. While popular and accessible, tasseomancy relies heavily on subjective interpretation,
 * making it less precise than more systematic divination methods.
 * </p>
 * <p>
 * This class implements the tasseomancy divination method, generating randomized prophecies based on tea-leaf
 * symbol interpretations. The prophecies are created by combining randomly selected prefixes (which reference
 * specific tea-leaf symbols and their traditional meanings) with divination text from the parent
 * {@link O2Divination} class. Tasseomancy has the lowest maximum accuracy (20 points) of all divination
 * methods in Ollivanders2, reflecting its reliance on subjective interpretation and pattern recognition rather
 * than more objective techniques.
 * </p>
 *
 * @author Azami7
 * @see <a href="http://harrypotter.wikia.com/wiki/Tessomancy">Harry Potter Wiki - Tessomancy</a>
 */
public class TASSEOMANCY extends O2Divination {
    /**
     * Constructor that initializes a tasseomancy divination prophecy.
     * <p>
     * Creates a new tasseomancy divination instance and populates it with tea leaf symbol prophecy prefixes.
     * Sets the divination type to TASSEOMANCY with a maximum accuracy of 20 points (the lowest of all divination
     * methods). The prophecy prefixes reference specific tea leaf symbols and their traditional interpretations,
     * such as falcons (enemies), clubs (attacks), skulls (danger), and the Grim (death).
     * </p>
     *
     * @param plugin     a callback to the plugin for accessing configuration and other resources
     * @param prophet    the player who is performing the divination (casting the tasseomancy spell)
     * @param target     the player who is the subject of the divination prophecy
     * @param experience the experience level of the prophet, affecting prophecy accuracy and strength
     */
    public TASSEOMANCY(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        // Set divination type and accuracy threshold. Tasseomancy has the lowest accuracy (20) of all divination methods,
        // reflecting its subjective nature and reliance on pattern interpretation
        divinationType = O2DivinationType.TASSEOMANCY;
        maxAccuracy = 20;

        // Populate prophecy prefixes with tea leaf symbol interpretations. These prefixes are randomly selected
        // when generating a prophecy and combined with divination text to create the final prophecy message.
        // Each symbol represents a specific omen or message based on traditional tea leaf reading interpretations:
        // - Falcon: represents a deadly enemy, danger from a powerful foe
        // - Club: represents an attack or aggressive action
        // - Skull: represents danger or peril
        // - The Grim: represents death or a grave omen (specific to Harry Potter divination)
        // - Ring: represents confusion or entanglement
        // - Snake: represents enmity or betrayal
        // - Acorn: represents good fortune and positive outcomes
        // - Mountain: represents a hindrance or obstacle to overcome
        // - Cross: represents trials and suffering to endure
        // - Sun: represents great happiness and joy
        prophecyPrefix.add("The falcon ... a deadly enemy,");
        prophecyPrefix.add("The club ... an attack,");
        prophecyPrefix.add("The skull ... danger,");
        prophecyPrefix.add("The Grim ... death,");
        prophecyPrefix.add("The ring ... confusion,");
        prophecyPrefix.add("The snake ... enmity,");
        prophecyPrefix.add("The acorn ... good fortune,");
        prophecyPrefix.add("The mountain ... a hindrance,");
        prophecyPrefix.add("The cross ... trials and suffering,");
        prophecyPrefix.add("The sun ... great happiness,");
    }
}
