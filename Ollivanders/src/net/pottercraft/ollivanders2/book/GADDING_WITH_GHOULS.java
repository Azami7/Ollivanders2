package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Gadding with Ghouls - 2nd year Defense Against the Dark Arts book
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.OBLIVIATE}
 * </p>
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Gadding_with_Ghouls">https://harrypotter.fandom.com/wiki/Gadding_with_Ghouls</a>
 * @author Azami7
 * @since 2.2.4
 */
public class GADDING_WITH_GHOULS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public GADDING_WITH_GHOULS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.GADDING_WITH_GHOULS;

        spells.add(O2SpellType.OBLIVIATE);
        // todo Transmogrifian Torture curse
        // todo Transmogrifian Torture counter-curse - https://harrypotter.fandom.com/wiki/Counter-curse_for_the_Transmogrifian_Torture
    }
}
