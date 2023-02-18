package net.pottercraft.ollivanders2.book;

import org.jetbrains.annotations.NotNull;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Break with a Banshee - 2nd year Defense Against the Dark Arts book
 *
 * @since 2.2.4
 * @author Azami7
 */
public class BREAK_WITH_A_BANSHEE extends O2Book {
	public BREAK_WITH_A_BANSHEE(@NotNull Ollivanders2 plugin) {
		super(plugin);

		bookType = O2BookType.BREAK_WITH_A_BANSHEE;

		spells.add(O2SpellType.EXPELLIARMUS);
		spells.add(O2SpellType.OBLIVIATE);
	}
}
