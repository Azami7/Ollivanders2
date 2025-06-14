package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Curses and Counter-Curses
 * http://harrypotter.wikia.com/wiki/Curses_and_Counter-Curses
 *
 * @author Azami7
 * @since 2.2.4
 */
public class CURSES_AND_COUNTERCURSES extends O2Book {
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
