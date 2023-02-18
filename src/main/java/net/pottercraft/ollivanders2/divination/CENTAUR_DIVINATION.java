package net.pottercraft.ollivanders2.divination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Divination is a branch of magic that involves attempting to foresee the
 * future, or gather insights into past, present and future events.
 * https://harrypotter.fandom.com/wiki/Divination
 *
 * @author Azami7
 * @since 2.2.9
 */
public class CENTAUR_DIVINATION extends O2Divination {
	public CENTAUR_DIVINATION(@NotNull Ollivanders2 plugin, @NotNull Player pro, @NotNull Player tar, int exp) {
		super(plugin, pro, tar, exp);

		divintationType = O2DivinationType.CENTAUR_DIVINATION;
		maxAccuracy = 80;

		prophecyPrefix.add("Through careful study of the skies it is learned that");
		prophecyPrefix.add("Celestial portents reveal that");
		prophecyPrefix.add("Mars, bringer of battle, shines brightly above us, suggesting that");
		prophecyPrefix.add("The burning of leaves and herbs has revealed that");
		prophecyPrefix.add("By observing fume and flame, it is seen that");
	}
}
