package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Curses and Counter-Curses
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.METELOJINX}<br>
 * {@link net.pottercraft.ollivanders2.spell.METELOJINX_RECANTO}<br>
 * {@link net.pottercraft.ollivanders2.spell.SILENCIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.PETRIFICUS_TOTALUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.GEMINIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.LOQUELA_INEPTIAS}
 * </p>
 *
 * @see <a href = "https://harrypotter.wikia.com/wiki/Curses_and_Counter-Curses">https://harrypotter.wikia.com/wiki/Curses_and_Counter-Curses</a>
 * @author Azami7
 * @since 2.2.4
 */
public class CURSES_AND_COUNTERCURSES extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public CURSES_AND_COUNTERCURSES(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.CURSES_AND_COUNTERCURSES;

        openingPage = "Bewitch your friends and befuddle your enemies with the latest revenges: Hair loss, Jelly-Legs, Tongue-Tying, and much, much more.";

        spells.add(O2SpellType.METELOJINX);
        spells.add(O2SpellType.METELOJINX_RECANTO);
        spells.add(O2SpellType.SILENCIO);
        spells.add(O2SpellType.PETRIFICUS_TOTALUS);
        spells.add(O2SpellType.GEMINIO);
        spells.add(O2SpellType.LOQUELA_INEPTIAS);
        // todo counter-curse - https://harrypotter.fandom.com/wiki/Counter-curse
    }
}
