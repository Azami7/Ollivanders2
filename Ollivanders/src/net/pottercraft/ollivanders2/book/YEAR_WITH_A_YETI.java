package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Year with the Yeti - 2nd year Defense Against the Dark Arts book
 *
 * @link https://harrypotter.fandom.com/wiki/Year_with_the_Yeti
 * @author Azami7
 * @since 2.2.4
 */
public class YEAR_WITH_A_YETI extends O2Book {
    public YEAR_WITH_A_YETI(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.YEAR_WITH_A_YETI;

        spells.add(O2SpellType.ARANIA_EXUMAI);
        spells.add(O2SpellType.OBLIVIATE);
        // todo Mimblewimble - https://github.com/Azami7/Ollivanders2/issues/54
    }
}
