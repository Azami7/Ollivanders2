package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Hellenistic astrology book that became the basis for all western astrology, added as a "rare" book for
 * more powerful spells.
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.ASTROLOGIA}
 * {@link net.pottercraft.ollivanders2.spell.PROPHETEIA}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://en.wikipedia.org/wiki/Tetrabiblos">https://en.wikipedia.org/wiki/Tetrabiblos</a>
 */
public class TETRABIBLIOS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public TETRABIBLIOS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.TETRABIBLIOS;

        spells.add(O2SpellType.ASTROLOGIA);
        spells.add(O2SpellType.PROPHETEIA);
    }
}
