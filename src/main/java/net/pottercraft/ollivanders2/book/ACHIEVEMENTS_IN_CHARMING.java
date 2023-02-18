package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Achievements in Charming - Charms book for 1st year.
 *
 * @since 2.2.4
 * @author Azami7
 */
public class ACHIEVEMENTS_IN_CHARMING extends O2Book {
	public ACHIEVEMENTS_IN_CHARMING(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.ACHIEVEMENTS_IN_CHARMING;

		spells.add(O2SpellType.LUMOS);
		spells.add(O2SpellType.WINGARDIUM_LEVIOSA);
		spells.add(O2SpellType.SPONGIFY);
		spells.add(O2SpellType.PYROSVESTIRAS);
	}
}
