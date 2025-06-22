package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Omens, Oracles, and the Goat is a book by the celebrated magical historian, Bathilda Bagshot, covering a historical
 * overview of Divination practises.
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.CARTOMANCIE}<br>
 * {@link net.pottercraft.ollivanders2.spell.MANTEIA_KENTAVROS}
 * </p>
 *
 * @see <a href = "http://harrypotter.wikia.com/wiki/Omens,_Oracles_%26_the_Goat">http://harrypotter.wikia.com/wiki/Omens,_Oracles_%26_the_Goat</a>
 * @author Azami7
 * @since 2.2.9
 */
public class OMENS_ORACLES_AND_THE_GOAT extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public OMENS_ORACLES_AND_THE_GOAT(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.OMENS_ORACLES_AND_THE_GOAT;

        spells.add(O2SpellType.CARTOMANCIE);
        spells.add(O2SpellType.MANTEIA_KENTAVROS);
        // todo rune stones - https://harrypotter.fandom.com/wiki/Rune_stone
    }
}
