package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Non-cannon book written by George Weasley on firework making.
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class ADVANCED_FIREWORKS extends O2Book {
	public ADVANCED_FIREWORKS(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.ADVANCED_FIREWORKS;

		spells.add(O2SpellType.BOTHYNUS_DUO);
		spells.add(O2SpellType.COMETES_DUO);
		spells.add(O2SpellType.PERICULUM_DUO);
		spells.add(O2SpellType.PORFYRO_ASTERI_DUO);
		spells.add(O2SpellType.VERDIMILLIOUS_DUO);
		spells.add(O2SpellType.BOTHYNUS_TRIA);
		spells.add(O2SpellType.PORFYRO_ASTERI_TRIA);
	}
}
