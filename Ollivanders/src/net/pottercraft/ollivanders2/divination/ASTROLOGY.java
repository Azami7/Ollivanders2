package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Astrology divination spell implementation using celestial influences.
 * <p>
 * Astrology is the system of using the relative positions of celestial bodies (including the sun, moon, and planets) to
 * try to predict future events or gain insight into personality, relationships, and health.
 * </p>
 * <p>
 * This class implements the astrology divination method, generating randomized prophecies based on astrological
 * concepts like planetary influences, house positions, and birth chart interpretations. The prophecies are created
 * by combining randomly selected prefixes (which reference specific planets, houses, or astrological aspects) with
 * divination text from the parent {@link O2Divination} class.
 * </p>
 *
 * @author Azami7
 * @see <a href="http://harrypotter.wikia.com/wiki/Astrology">Harry Potter Wiki - Astrology</a>
 */
public class ASTROLOGY extends O2Divination {
    /**
     * Constructor that initializes an astrology divination prophecy.
     * <p>
     * Creates a new astrology divination instance and populates it with astrological prophecy prefixes.
     * Sets the divination type to ASTROLOGY with a maximum accuracy of 20 points.
     * The prophecy prefixes reference planetary influences, astrological houses, and birth chart positions
     * to create varied, thematic predictions.
     * </p>
     *
     * @param plugin     a callback to the plugin for accessing configuration and other resources
     * @param prophet    the player who is performing the divination (using the astrology spell)
     * @param target     the player who is the subject of the divination prophecy
     * @param experience the experience level of the prophet, affecting prophecy accuracy and strength
     */
    public ASTROLOGY(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);


        // Set divination type and accuracy threshold
        divinationType = O2DivinationType.ASTROLOGY;
        maxAccuracy = 20;

        // Populate prophecy prefixes with astrological phrases. These prefixes are randomly selected
        // when generating a prophecy and combined with divination text to create the final prophecy message.
        // The prefixes are organized into three categories:
        // 1. Planetary influences (Mars, Mercury, Venus, Jupiter, Saturn, Moon)
        // 2. Astrological angles/aspects (planets in aspect to each other)
        // 3. House positions (planets in specific astrological houses)
        // 4. Birth chart positions (planets in the target's natal chart)
        prophecyPrefix.add("Because of the influence of Mars,");
        prophecyPrefix.add("Due to the influence of Mercury,");
        prophecyPrefix.add("From the influence of Venus,");
        prophecyPrefix.add("Due to the influence of Jupiter,");
        prophecyPrefix.add("Because of the influence of Saturn,");
        prophecyPrefix.add("Due to the influence of the Moon,");
        prophecyPrefix.add("The angle of Saturn and Venus means that");
        prophecyPrefix.add("The angle of Mercury and Mars means");
        prophecyPrefix.add("The angle of Jupiter and the Moon predicts that");
        prophecyPrefix.add("The angle of the Moon and Saturn reveals that");
        prophecyPrefix.add("Mars in the 6th house fortells that");
        prophecyPrefix.add("Mercury in the 3rd house fortells that");
        prophecyPrefix.add("The Moon in the 7th house will cause");
        prophecyPrefix.add("Saturn in the 5th house indicates that");
        prophecyPrefix.add("Jupiter in the 4th house predicts that");
        prophecyPrefix.add("Because of the position of the Moon in their birth chart,");
        prophecyPrefix.add("The position of Mercury in their birth chart reveals that");
        prophecyPrefix.add("The position of Venus in their birth chart predicts that");
        prophecyPrefix.add("From the position of Mars in their birth chart indicates that");
        prophecyPrefix.add("The position of Jupiter in their birth chart assures that");
        prophecyPrefix.add("The position of Saturn in their birth chart means");
    }
}
