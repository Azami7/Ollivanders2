package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Centaur divination — celestial observation and augury, with a maximum accuracy of 80% (the highest of any divination
 * method).
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Divination">Harry Potter Wiki - Divination</a>
 */
public class CENTAUR_DIVINATION extends O2Divination {
    /**
     * Create a centaur divination prophecy about the target.
     *
     * @param plugin     a callback to the plugin
     * @param prophet    the player performing the divination
     * @param target     the player the prophecy is about
     * @param experience the prophet's experience level with this divination
     */
    public CENTAUR_DIVINATION(@NotNull Ollivanders2 plugin, @NotNull Player prophet, @NotNull Player target, int experience) {
        super(plugin, prophet, target, experience);

        divinationType = O2DivinationType.CENTAUR_DIVINATION;
        maxAccuracy = 80;

        prophecyPrefix.add("Through careful study of the skies it is learned that");
        prophecyPrefix.add("Celestial portents reveal that");
        prophecyPrefix.add("Mars, bringer of battle, shines brightly above us, suggesting that");
        prophecyPrefix.add("The burning of leaves and herbs has revealed that");
        prophecyPrefix.add("By observing fume and flame, it is seen that");
    }
}
