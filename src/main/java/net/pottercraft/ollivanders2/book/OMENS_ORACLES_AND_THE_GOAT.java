package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Omens, Oracles & the Goat was a book by the celebrated magical historian,
 * Bathilda Bagshot, covering a historical overview of Divination practises.
 * http://harrypotter.wikia.com/wiki/Omens,_Oracles_%26_the_Goat
 *
 * @author Azami7
 * @since 2.2.9
 */
public class OMENS_ORACLES_AND_THE_GOAT extends O2Book {
	public OMENS_ORACLES_AND_THE_GOAT(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.OMENS_ORACLES_AND_THE_GOAT;

		spells.add(O2SpellType.OVOGNOSIS);
		spells.add(O2SpellType.CARTOMANCIE);
		spells.add(O2SpellType.MANTEIA_KENTAVROS);
	}
}
