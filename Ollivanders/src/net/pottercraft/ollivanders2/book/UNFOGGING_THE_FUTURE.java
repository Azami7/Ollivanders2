package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Unfogging the Future - 3rd and 4th year Divination textbook
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.BAO_ZHONG_CHA}<br>
 * {@link net.pottercraft.ollivanders2.spell.INTUEOR}<br>
 * {@link net.pottercraft.ollivanders2.spell.ASTROLOGIA}<br>
 * {@link net.pottercraft.ollivanders2.spell.CARTOMANCIE}<br>
 * {@link net.pottercraft.ollivanders2.spell.CHARTIA}<br>
 * {@link net.pottercraft.ollivanders2.spell.OVOGNOSIS}
 * </p>
 *
 * @see <a href = "http://harrypotter.wikia.com/wiki/Unfogging_the_Future">http://harrypotter.wikia.com/wiki/Unfogging_the_Future</a>
 * @author Azami7
 * @since 2.2.9
 */
public class UNFOGGING_THE_FUTURE extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public UNFOGGING_THE_FUTURE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.UNFOGGING_THE_FUTURE;

        // year 3
        spells.add(O2SpellType.BAO_ZHONG_CHA);
        spells.add(O2SpellType.INTUEOR);
        // todo fire omens - https://harrypotter.fandom.com/wiki/Fire-omens

        // year 4
        spells.add(O2SpellType.ASTROLOGIA);
        // todo xylomancy - https://harrypotter.fandom.com/wiki/Xylomancy
        // todo bibliomancy - https://harrypotter.fandom.com/wiki/Bibliomancy

        // year 5
        spells.add(O2SpellType.CARTOMANCIE);
        // todo rune stones

        // year 6
        spells.add(O2SpellType.CHARTIA);

        // year 7
        spells.add(O2SpellType.OVOGNOSIS);
        // todo Ornithomancy - https://harrypotter.fandom.com/wiki/Ornithomancy
        // todo Capnomancy - https://harrypotter.fandom.com/wiki/Capnomancy
    }
}
