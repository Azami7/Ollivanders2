package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Achievements in Charming - Charms book for 1st year.
 *
 * @author Azami7
 * @since 2.2.4
 */
public class ACHIEVEMENTS_IN_CHARMING extends O2Book {
    public ACHIEVEMENTS_IN_CHARMING(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.ACHIEVEMENTS_IN_CHARMING;

        spells.add(O2SpellType.LUMOS);
        spells.add(O2SpellType.WINGARDIUM_LEVIOSA);
        spells.add(O2SpellType.SPONGIFY);
    }
}
