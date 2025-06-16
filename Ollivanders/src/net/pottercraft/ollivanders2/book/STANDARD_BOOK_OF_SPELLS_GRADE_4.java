package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 4
 *
 * @link https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_4
 * @author Azami7
 * @since 2.2.4
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_4 extends O2Book {
    public STANDARD_BOOK_OF_SPELLS_GRADE_4(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_4;

        spells.add(O2SpellType.ACCIO);
        spells.add(O2SpellType.BOMBARDA);
        spells.add(O2SpellType.MELOFORS);
        spells.add(O2SpellType.DIAMAS_REPARO);
        spells.add(O2SpellType.DIFFINDO);
        spells.add(O2SpellType.LUMOS_MAXIMA);
        spells.add(O2SpellType.TERGEO);
        spells.add(O2SpellType.COLOVARIA);
        // todo Orchideous - https://github.com/Azami7/Ollivanders2/issues/56
        spells.add(O2SpellType.AVIFORS);
    }
}

