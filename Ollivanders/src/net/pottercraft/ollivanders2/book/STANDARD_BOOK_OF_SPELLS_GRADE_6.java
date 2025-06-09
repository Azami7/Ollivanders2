package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 6
 *
 * @link https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_6
 * @author Azami7
 * @since 2.2.4
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_6 extends O2Book {
    public STANDARD_BOOK_OF_SPELLS_GRADE_6(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_6;

        spells.add(O2SpellType.AGUAMENTI);
        spells.add(O2SpellType.CONFUNDUS_DUO);
        spells.add(O2SpellType.DEFODIO);
        spells.add(O2SpellType.GLACIUS_TRIA);
        spells.add(O2SpellType.MOLLIARE);
        spells.add(O2SpellType.PARTIS_TEMPORUS);
        // todo - vinegar to wine - https://harrypotter.fandom.com/wiki/Vinegar_to_wine_spell
        // todo - knitting charm - https://harrypotter.fandom.com/wiki/Knitting_Charm
        // todo - bubble head charm - https://harrypotter.fandom.com/wiki/Bubble-Head_Charm
        // todo - blue sparks - https://harrypotter.fandom.com/wiki/Blue_Sparks
        spells.add(O2SpellType.PYROSVESTIRAS);
        spells.add(O2SpellType.EVANESCO);
        spells.add(O2SpellType.ALARTE_ASCENDARE);
        spells.add(O2SpellType.APPARATE);
    }
}
