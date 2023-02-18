package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Standard Book of Spells Grade 3
 * <p>
 * Reference:
 * https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_3
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_3 extends O2Book {
	public STANDARD_BOOK_OF_SPELLS_GRADE_3(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_3;

		spells.add(O2SpellType.EXPELLIARMUS);
		spells.add(O2SpellType.DRACONIFORS);
		spells.add(O2SpellType.IMMOBULUS);
		spells.add(O2SpellType.LUMOS_DUO);
		spells.add(O2SpellType.REPARO);
		spells.add(O2SpellType.CARPE_RETRACTUM);
		spells.add(O2SpellType.PACK);
		spells.add(O2SpellType.LAPIFORS);
		spells.add(O2SpellType.SNUFFLIFORS);
		// 10
		// 11
	}
}
