package net.pottercraft.ollivanders2.divination;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Divination is a branch of magic that involves attempting to foresee the future, or gather insights into past, present
 * and future events.
 *
 * <p>Reference: https://harrypotter.fandom.com/wiki/Divination</p>
 *
 * @author Azami7
 * @since 2.2.9
 */
public class CENTAUR_DIVINATION extends O2Divination {
    public CENTAUR_DIVINATION(@NotNull Ollivanders2 plugin, @NotNull Player pro, @NotNull Player tar, int exp) {
        super(plugin, pro, tar, exp);

        divinationType = O2DivinationType.CENTAUR_DIVINATION;
        maxAccuracy = 80;

        prophecyPrefix.add("Through careful study of the skies it is learned that");
        prophecyPrefix.add("Celestial portents reveal that");
        prophecyPrefix.add("Mars, bringer of battle, shines brightly above us, suggesting that");
        prophecyPrefix.add("The burning of leaves and herbs has revealed that");
        prophecyPrefix.add("By observing fume and flame, it is seen that");
    }
}
