package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Standard Book of Spells Grade 5
 *
 * Missing spells: Substantive Charm -
 * https://github.com/Azami7/Ollivanders2/issues/96 Rictusempra -
 * https://github.com/Azami7/Ollivanders2/issues/92
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_5 extends O2Book {
	public STANDARD_BOOK_OF_SPELLS_GRADE_5(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_5;

		spells.add(O2SpellType.DEPULSO);
		spells.add(O2SpellType.EXPELLIARMUS);
		spells.add(O2SpellType.INCENDIO);
		spells.add(O2SpellType.LEVICORPUS);
		spells.add(O2SpellType.LIBERACORPUS);
		spells.add(O2SpellType.PROTEGO);
		spells.add(O2SpellType.STUPEFY);
		spells.add(O2SpellType.INCENDIO_DUO);
		// spells.add(O2SpellType.RICTUSEMPRA);
		// spells.add(O2SpellType.SUBSTANTIVUM);
		// 11
	}
}
