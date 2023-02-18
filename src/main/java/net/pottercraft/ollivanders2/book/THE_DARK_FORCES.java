package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * The Dark Forces: A Guide to Self-Protection - 1st year Defense Against the
 * Dark Arts
 *
 * Missing Spells: Flipendo - https://github.com/Azami7/Ollivanders2/issues/37
 *
 * @since 2.2.4
 * @author Azami7
 */
public class THE_DARK_FORCES extends O2Book {
	public THE_DARK_FORCES(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.THE_DARK_FORCES;

		spells.add(O2SpellType.LUMOS);
		spells.add(O2SpellType.FINITE_INCANTATEM);
		spells.add(O2SpellType.FUMOS);
		spells.add(O2SpellType.PERICULUM);
		spells.add(O2SpellType.VERDIMILLIOUS);
		spells.add(O2SpellType.FLIPENDO);
	}
}
