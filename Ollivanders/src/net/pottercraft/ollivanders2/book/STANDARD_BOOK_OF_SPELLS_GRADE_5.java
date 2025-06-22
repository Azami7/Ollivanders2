package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Standard Book of Spells Grade 5
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.SILENCIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.MUFFLIATO}<br>
 * {@link net.pottercraft.ollivanders2.spell.CONFUNDO}<br>
 * {@link net.pottercraft.ollivanders2.spell.DELETRIUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.STUPEFY}<br>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO}<br>
 * {@link net.pottercraft.ollivanders2.spell.EXPELLIARMUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.INCENDIO_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.ARRESTO_MOMENTUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.STUPEFY}<br>
 * </p>
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_5">https://harrypotter.fandom.com/wiki/The_Standard_Book_of_Spells,_Grade_5</a>
 * @author Azami7
 * @since 2.2.4
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_5 extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public STANDARD_BOOK_OF_SPELLS_GRADE_5(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_5;

        spells.add(O2SpellType.SILENCIO);
        spells.add(O2SpellType.MUFFLIATO);
        spells.add(O2SpellType.CONFUNDO);
        // todo substantive charm - https://harrypotter.fandom.com/wiki/Substantive_Charm - maybe make armor or weapons stronger (next level up)
        spells.add(O2SpellType.DELETRIUS);
        spells.add(O2SpellType.STUPEFY);
        // todo Rennervate - https://harrypotter.fandom.com/wiki/Reviving_Spell
        spells.add(O2SpellType.PROTEGO);
        spells.add(O2SpellType.EXPELLIARMUS);
        spells.add(O2SpellType.INCENDIO_DUO);
        spells.add(O2SpellType.ARRESTO_MOMENTUM);
        spells.add(O2SpellType.STUPEFY);
    }
}
