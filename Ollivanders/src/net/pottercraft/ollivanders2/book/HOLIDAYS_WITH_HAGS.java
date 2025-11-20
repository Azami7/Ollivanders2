package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Holidays with Hags - 2nd year Defense Against the Dark Arts book
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.LUMOS_FERVENS}
 * {@link net.pottercraft.ollivanders2.spell.OBLIVIATE}
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Holidays_with_Hags">https://harrypotter.fandom.com/wiki/Holidays_with_Hags</a>
 */
public class HOLIDAYS_WITH_HAGS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public HOLIDAYS_WITH_HAGS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.HOLIDAYS_WITH_HAGS;

        spells.add(O2SpellType.LUMOS_FERVENS);
        spells.add(O2SpellType.OBLIVIATE);
        // todo some spell related to witches
    }
}
