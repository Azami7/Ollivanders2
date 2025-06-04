package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Quintessence: A Quest - 6th year Charms book
 *
 * @author Azami7
 * @since 2.2.4
 */
public class QUINTESSENCE_A_QUEST extends O2Book {
    public QUINTESSENCE_A_QUEST(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.QUINTESSENCE_A_QUEST;

        spells.add(O2SpellType.CONFUNDUS_DUO);
        spells.add(O2SpellType.APARECIUM);
        spells.add(O2SpellType.GLACIUS_TRIA);
        spells.add(O2SpellType.MOLLIARE);
    }
}
