package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 3
 *
 * @link https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_3
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_3 extends O2Book {
    public STANDARD_BOOK_OF_SPELLS_GRADE_3(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_3;

        //todo cheering charm - https://harrypotter.fandom.com/wiki/Cheering_Charm
        spells.add(O2SpellType.GLACIUS);
        spells.add(O2SpellType.DEPULSO);
        spells.add(O2SpellType.GLACIUS_DUO);
        spells.add(O2SpellType.PACK);
        spells.add(O2SpellType.HERBIFORS);
        spells.add(O2SpellType.CARPE_RETRACTUM);
        spells.add(O2SpellType.DURO);
        spells.add(O2SpellType.IMMOBULUS);
        spells.add(O2SpellType.LUMOS_DUO);
        spells.add(O2SpellType.EXPELLIARMUS);
    }
}
