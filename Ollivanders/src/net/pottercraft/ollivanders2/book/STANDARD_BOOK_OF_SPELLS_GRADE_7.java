package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 7
 * https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_7
 *
 * @author Azami7
 * @since 2.2.4
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_7 extends O2Book {
    public STANDARD_BOOK_OF_SPELLS_GRADE_7(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_7;

        spells.add(O2SpellType.BOMBARDA_MAXIMA);
        spells.add(O2SpellType.HERBIVICUS);
        spells.add(O2SpellType.INFORMOUS);
        spells.add(O2SpellType.LUMOS_SOLEM);
        spells.add(O2SpellType.OBLIVIATE);
        spells.add(O2SpellType.REPELLO_MUGGLETON);
        // todo anti-alohomora - https://harrypotter.fandom.com/wiki/Anti-Alohomora_Charm
        // todo impervious - https://harrypotter.fandom.com/wiki/Impervius_Charm
        // todo Imperturbable Charm - like muffliato but not a stationary spell, less range, targets self - https://harrypotter.fandom.com/wiki/Imperturbable_Charm
    }
}
