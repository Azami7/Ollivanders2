package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Year with the Yeti - 2nd year Defense Against the Dark Arts book
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.ARANIA_EXUMAI}<br>
 * {@link net.pottercraft.ollivanders2.spell.OBLIVIATE}<br>
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Year_with_the_Yeti">https://harrypotter.fandom.com/wiki/Year_with_the_Yeti</a>
 */
public class YEAR_WITH_A_YETI extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public YEAR_WITH_A_YETI(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.YEAR_WITH_A_YETI;

        spells.add(O2SpellType.ARANIA_EXUMAI);
        spells.add(O2SpellType.OBLIVIATE);
        // todo Mimblewimble - https://github.com/Azami7/Ollivanders2/issues/54
    }
}
