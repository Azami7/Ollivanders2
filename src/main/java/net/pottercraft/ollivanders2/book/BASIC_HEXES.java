package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Basic Hexes for the Busy and Vexed Reference:
 * http://harrypotter.wikia.com/wiki/Basic_Hexes_for_the_Busy_and_Vexed
 */
public class BASIC_HEXES extends O2Book {
	public BASIC_HEXES(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.BASIC_HEXES;

		spells.add(O2SpellType.MUCUS_AD_NAUSEAM);
		spells.add(O2SpellType.IMPEDIMENTA);
		spells.add(O2SpellType.IMMOBULUS);
		spells.add(O2SpellType.OBSCURO);
		spells.add(O2SpellType.LOQUELA_INEPTIAS);
		// pepper breath
	}
}
