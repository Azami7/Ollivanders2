package net.pottercraft.ollivanders2.divination;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Cartomancy is the art of reading cards to gain insight into future events.
 * http://harrypotter.wikia.com/wiki/Cartomancy
 *
 * @author Azami7
 * @since 2.2.9
 */
public class CARTOMANCY extends O2Divination {
	public CARTOMANCY(@NotNull Ollivanders2 plugin, @NotNull Player pro, @NotNull Player tar, int exp) {
		super(plugin, pro, tar, exp);

		divintationType = O2DivinationType.CARTOMANCY;
		maxAccuracy = 25;

		prophecyPrefix.add("The cards have revaled that");
		prophecyPrefix.add("The reading of the cards says that");
		prophecyPrefix.add("Two of spades: conflict,");
		prophecyPrefix.add("Seven of spades: an ill omen,");
		prophecyPrefix.add("Ten of spades: violence,");
		prophecyPrefix.add(
				"Ten of spades: violence. Knave of spades: a dark young man, possibly troubled, one who dislikes the questioner,");
	}
}
