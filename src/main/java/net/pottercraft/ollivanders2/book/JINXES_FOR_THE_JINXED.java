package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Jinxes for the Jinxed
 * <p>
 * Reference: https://harrypotter.fandom.com/wiki/Jinxes_for_the_Jinxed
 */
public class JINXES_FOR_THE_JINXED extends O2Book {
	public JINXES_FOR_THE_JINXED(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.JINXES_FOR_THE_JINXED;

		openingPage = "Learn some jinxes to add to your arsenal with this handy volume.";

		spells.add(O2SpellType.ENTOMORPHIS);
		spells.add(O2SpellType.IMPEDIMENTA);
		spells.add(O2SpellType.LEVICORPUS);
		spells.add(O2SpellType.LACARNUM_INFLAMARI);
		spells.add(O2SpellType.LAGOMORPHA);
		// 6
		// 7
		// 8
		// 9
		// 10
		// 11
	}
}
