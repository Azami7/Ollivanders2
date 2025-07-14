package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 2
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.ENGORGIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.REDUCIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.FINITE_INCANTATEM}<br>
 * {@link net.pottercraft.ollivanders2.spell.ARRESTO_MOMENTUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.APARECIUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.FLIPENDO}<br>
 * {@link net.pottercraft.ollivanders2.spell.EXPELLIARMUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.SPONGIFY}
 * </p>
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_2">https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_2</a>
 * @author Azami7
 * @since 2.2.4
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_2 extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public STANDARD_BOOK_OF_SPELLS_GRADE_2(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_2;

        //todo skurge - gets rid of slime
        //todo Tarantallegra - https://harrypotter.fandom.com/wiki/Dancing_Feet_Spell
        //todo Rictusempra - https://harrypotter.fandom.com/wiki/Tickling_Charm
        spells.add(O2SpellType.ENGORGIO);
        spells.add(O2SpellType.REDUCIO);
        spells.add(O2SpellType.FINITE_INCANTATEM);
        spells.add(O2SpellType.ARRESTO_MOMENTUM);
        spells.add(O2SpellType.APARECIUM);
        spells.add(O2SpellType.CELATUM);
        spells.add(O2SpellType.FLIPENDO);
        spells.add(O2SpellType.EXPELLIARMUS);
        spells.add(O2SpellType.SPONGIFY);
    }
}
