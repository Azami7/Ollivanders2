package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Hellenistic astrology book that became the basis for all western astrology,
 * added as a "rare" book for more powerful spells.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class TETRABIBLIOS extends O2Book {
	public TETRABIBLIOS(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.TETRABIBLIOS;

		spells.add(O2SpellType.ASTROLOGIA);
		spells.add(O2SpellType.PROPHETEIA);
	}
}
