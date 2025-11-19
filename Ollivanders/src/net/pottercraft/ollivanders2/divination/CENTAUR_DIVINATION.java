package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Centaur Divination spell implementation using celestial observation and smoke reading.
 * <p>
 * Centaur divination is performed by wise centaur scholars who use celestial observations (studying the stars,
 * planets, and astronomical phenomena) combined with augury techniques like observing smoke and flames from burning
 * herbs and leaves. This method is considered highly accurate, as centaurs possess deep astronomical knowledge and
 * spiritual insight into natural portents.
 * </p>
 * <p>
 * This class implements the centaur divination method, generating randomized prophecies based on celestial signs
 * and smoke/flame readings. The prophecies are created by combining randomly selected prefixes (which reference
 * astronomical observations, celestial portents, and augury practices) with divination text from the parent
 * {@link O2Divination} class. Centaur divination has the highest maximum accuracy (80 points) of all divination
 * methods in Ollivanders2, reflecting the superior knowledge and mystical attunement of centaur scholars.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Divination">Harry Potter Wiki - Divination</a>
 */
public class CENTAUR_DIVINATION extends O2Divination {
    /**
     * Constructor that initializes a centaur divination prophecy.
     * <p>
     * Creates a new centaur divination instance and populates it with celestial and augury prophecy prefixes.
     * Sets the divination type to CENTAUR_DIVINATION with a maximum accuracy of 80 points (the highest of all
     * divination methods). The prophecy prefixes reference celestial observations (star/planet study), astronomical
     * phenomena, and augury practices (smoke and flame reading from burning herbs).
     * </p>
     *
     * @param plugin     a callback to the plugin for accessing configuration and other resources
     * @param prophet    the player who is performing the divination (casting the centaur divination spell)
     * @param target     the player who is the subject of the divination prophecy
     * @param experience the experience level of the prophet, affecting prophecy accuracy and strength
     */
    public CENTAUR_DIVINATION(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        // Set divination type and accuracy threshold. Centaur divination has the highest accuracy (80) of all divination methods
        divinationType = O2DivinationType.CENTAUR_DIVINATION;
        maxAccuracy = 80;

        // Populate prophecy prefixes with centaur divination phrases. These prefixes are randomly selected
        // when generating a prophecy and combined with divination text to create the final prophecy message.
        // The prefixes reference two main centaur divination techniques:
        // 1. Celestial observation - studying the stars, planets, and astronomical portents
        // 2. Augury - reading omens from smoke and flames of burning herbs and leaves
        // These methods reflect centaur wisdom and deep attunement to natural and celestial signs.
        prophecyPrefix.add("Through careful study of the skies it is learned that");
        prophecyPrefix.add("Celestial portents reveal that");
        prophecyPrefix.add("Mars, bringer of battle, shines brightly above us, suggesting that");
        prophecyPrefix.add("The burning of leaves and herbs has revealed that");
        prophecyPrefix.add("By observing fume and flame, it is seen that");
    }
}
