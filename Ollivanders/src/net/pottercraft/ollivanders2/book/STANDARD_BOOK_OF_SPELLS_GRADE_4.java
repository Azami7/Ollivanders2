package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 4
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.ACCIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.BOMBARDA}<br>
 * {@link net.pottercraft.ollivanders2.spell.MELOFORS}<br>
 * {@link net.pottercraft.ollivanders2.spell.DIAMAS_REPARO}<br>
 * {@link net.pottercraft.ollivanders2.spell.DIFFINDO}<br>
 * {@link net.pottercraft.ollivanders2.spell.LUMOS_MAXIMA}<br>
 * {@link net.pottercraft.ollivanders2.spell.TERGEO}<br>
 * {@link net.pottercraft.ollivanders2.spell.COLOVARIA}<br>
 * {@link net.pottercraft.ollivanders2.spell.AVIFORS}<br>
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_4">https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_4</a>
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_4 extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
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

