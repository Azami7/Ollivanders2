package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Crystal Ball divination spell implementation using scrying and clairvoyant vision.
 * <p>
 * Crystal-gazing (also known as scrying) is the art of looking into a crystal ball or crystalline orb to gain
 * insight into future events and hidden truths. A skilled diviner gazes into the crystal's depths, where shadowy
 * images and clairvoyant vibrations reveal glimpses of what is to come. This method requires focus and mystical
 * attunement to perceive the subtle visions within the crystal.
 * </p>
 * <p>
 * This class implements the crystal ball divination method, generating randomized prophecies based on visions
 * and clairvoyant readings. The prophecies are created by combining randomly selected prefixes (which reference
 * the crystal orb, clairvoyant vibrations, and shadowy portents) with divination text from the parent
 * {@link O2Divination} class. Crystal ball divination has moderate maximum accuracy (30 points), making it more
 * reliable than basic divination methods like astrology and cartomancy, but less powerful than tarot or centaur divination.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Crystal-gazing">Harry Potter Wiki - Crystal-gazing</a>
 */
public class CRYSTAL_BALL extends O2Divination {
    /**
     * Constructor that initializes a crystal ball divination prophecy.
     * <p>
     * Creates a new crystal ball divination instance and populates it with scrying and clairvoyant prophecy prefixes.
     * Sets the divination type to CRYSTAL_BALL with a maximum accuracy of 30 points. The prophecy prefixes reference
     * the crystal orb, clairvoyant vibrations, and shadowy visions revealed through scrying into the crystal's depths.
     * </p>
     *
     * @param plugin     a callback to the plugin for accessing configuration and other resources
     * @param prophet    the player who is performing the divination (casting the crystal ball spell)
     * @param target     the player who is the subject of the divination prophecy
     * @param experience the experience level of the prophet, affecting prophecy accuracy and strength
     */
    public CRYSTAL_BALL(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        // Set divination type and accuracy threshold. Crystal ball divination has moderate accuracy (30), between
        // basic methods (astrology=20, cartomancy=25) and advanced methods (tarot=35, centaur=80)
        divinationType = O2DivinationType.CRYSTAL_BALL;
        maxAccuracy = 30;

        // Populate prophecy prefixes with crystal ball divination phrases. These prefixes are randomly selected
        // when generating a prophecy and combined with divination text to create the final prophecy message.
        // The prefixes reference the technique of scrying into a crystal orb, where clairvoyant abilities allow
        // the diviner to perceive shadowy images and visions of future events within the crystal's depths.
        prophecyPrefix.add("The crystal ball has revealed that");
        prophecyPrefix.add("The clairvoyant vibrations of the orb show that");
        prophecyPrefix.add("The shadowy portents have been read,");
        prophecyPrefix.add("The orb tells the future,");
    }
}
