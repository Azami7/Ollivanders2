package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Practical Defensive Magic - sent to Harry by Sirius and Lupin in his 5th year
 *
 * @since 2.2.4
 * @author Azami7
 */
public class PRACTICAL_DEFENSIVE_MAGIC extends O2Book {
	public PRACTICAL_DEFENSIVE_MAGIC(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.PRACTICAL_DEFENSIVE_MAGIC;

		spells.add(O2SpellType.EXPELLIARMUS);
		spells.add(O2SpellType.PROTEGO);
		spells.add(O2SpellType.PROTEGO_MAXIMA);
		spells.add(O2SpellType.MUFFLIATO);
		spells.add(O2SpellType.ARANIA_EXUMAI);
		spells.add(O2SpellType.FIANTO_DURI);
		spells.add(O2SpellType.PRIOR_INCANTATO);
	}
}