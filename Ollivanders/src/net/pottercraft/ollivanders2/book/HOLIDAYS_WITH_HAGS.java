package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Holidays with Hags - 2nd year Defense Against the Dark Arts book
 *
 * @author Azami7
 * @since 2.2.4
 */
public class HOLIDAYS_WITH_HAGS extends O2Book {
    public HOLIDAYS_WITH_HAGS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.HOLIDAYS_WITH_HAGS;

        spells.add(O2SpellType.OBLIVIATE);
        // todo some spell related to witches
    }
}
