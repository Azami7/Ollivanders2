package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Break with a Banshee - 2nd year Defense Against the Dark Arts book
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.OBLIVIATE}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Break_with_a_Banshee">https://harrypotter.fandom.com/wiki/Break_with_a_Banshee</a>
 */
public class BREAK_WITH_A_BANSHEE extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public BREAK_WITH_A_BANSHEE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.BREAK_WITH_A_BANSHEE;

        spells.add(O2SpellType.OBLIVIATE);
        // todo banshee banishment charm
    }
}
