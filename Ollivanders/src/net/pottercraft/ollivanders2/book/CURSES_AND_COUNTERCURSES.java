package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.GEMINO;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Curses and Counter-Curses
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.PETRIFICUS_TOTALUS}<br>
 * {@link GEMINO}<br>
 * {@link net.pottercraft.ollivanders2.spell.LOQUELA_INEPTIAS}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.wikia.com/wiki/Curses_and_Counter-Curses">https://harrypotter.wikia.com/wiki/Curses_and_Counter-Curses</a>
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

        spells.add(O2SpellType.PETRIFICUS_TOTALUS);
        spells.add(O2SpellType.GEMINO);
        spells.add(O2SpellType.LOQUELA_INEPTIAS);
        // todo counter-curse - https://harrypotter.fandom.com/wiki/Counter-curse
        // todo jelly-legs curse - https://harrypotter.fandom.com/wiki/Jelly-Legs_Curse
        // todo leg-locking curse - https://harrypotter.fandom.com/wiki/Leg-Locker_Curse
        // todo mimblewinble - https://harrypotter.fandom.com/wiki/Tongue-Tying_Curse
        // todo confringo - https://harrypotter.fandom.com/wiki/Blasting_Curse
        // todo conjuntivitis curse - https://harrypotter.fandom.com/wiki/Conjunctivitis_Curse
        // todo expluso - https://harrypotter.fandom.com/wiki/Expulso_Curse
        // slugus eructo - https://harrypotter.fandom.com/wiki/Slug-Vomiting_Charm
    }
}
