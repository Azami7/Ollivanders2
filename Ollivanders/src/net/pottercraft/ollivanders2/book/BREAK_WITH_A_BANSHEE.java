package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Break with a Banshee - 2nd year Defense Against the Dark Arts book
 *
 * @link https://harrypotter.fandom.com/wiki/Break_with_a_Banshee
 * @author Azami7
 * @since 2.2.4
 */
public class BREAK_WITH_A_BANSHEE extends O2Book {
    public BREAK_WITH_A_BANSHEE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.BREAK_WITH_A_BANSHEE;

        spells.add(O2SpellType.OBLIVIATE);
        // todo banshee banishment charm
    }
}
