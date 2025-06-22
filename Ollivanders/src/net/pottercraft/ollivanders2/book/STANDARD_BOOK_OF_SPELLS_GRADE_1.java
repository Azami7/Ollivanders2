package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 1
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.LUMOS}<br>
 * {@link net.pottercraft.ollivanders2.spell.NOX}<br>
 * {@link net.pottercraft.ollivanders2.spell.ALOHOMORA}<br>
 * {@link net.pottercraft.ollivanders2.spell.COLLOPORTUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.REPARO}<br>
 * {@link net.pottercraft.ollivanders2.spell.INCENDIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.SPONGIFY}<br>
 * {@link net.pottercraft.ollivanders2.spell.CISTERN_APERIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.WINGARDIUM_LEVIOSA}
 * </p>
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_1">https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_1</a>
 * @author Azami7
 * @since 2.2.4
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_1 extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public STANDARD_BOOK_OF_SPELLS_GRADE_1(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1;

        spells.add(O2SpellType.LUMOS);
        spells.add(O2SpellType.NOX);
        spells.add(O2SpellType.ALOHOMORA);
        spells.add(O2SpellType.COLLOPORTUS);
        spells.add(O2SpellType.REPARO);
        spells.add(O2SpellType.INCENDIO);
        spells.add(O2SpellType.SPONGIFY);
        spells.add(O2SpellType.CISTERN_APERIO);
        spells.add(O2SpellType.WINGARDIUM_LEVIOSA);
    }
}
