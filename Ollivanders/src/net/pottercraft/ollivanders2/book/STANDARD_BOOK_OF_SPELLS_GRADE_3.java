package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 3
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.GLACIUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.DEPULSO}<br>
 * {@link net.pottercraft.ollivanders2.spell.GLACIUS_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.PACK}<br>
 * {@link net.pottercraft.ollivanders2.spell.HERBIFORS}<br>
 * {@link net.pottercraft.ollivanders2.spell.CARPE_RETRACTUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.DURO}<br>
 * {@link net.pottercraft.ollivanders2.spell.IMMOBULUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.LUMOS_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.EXPELLIARMUS}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_3">https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_3</a>
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_3 extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
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
