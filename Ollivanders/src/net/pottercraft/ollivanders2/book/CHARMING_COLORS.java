package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Charming Colors - spells for changing colors
 *
 * @author Azami7
 * @since 2.2.4
 */
public final class CHARMING_COLORS extends O2Book {
    public CHARMING_COLORS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.CHARMING_COLORS;

        openingPage = "\"Mere color, unspoiled by meaning, and unallied with definite form, can speak to the soul in a thousand different ways.\"";

        spells.add(O2SpellType.COLOVARIA);
        spells.add(O2SpellType.COLOVARIA_ALBUM);
        spells.add(O2SpellType.COLOVARIA_AURANTIACO);
        spells.add(O2SpellType.COLOVARIA_CAERULUS);
        spells.add(O2SpellType.COLOVARIA_FLAVO);
        spells.add(O2SpellType.COLOVARIA_OSTRUM);
        spells.add(O2SpellType.COLOVARIA_VERIDI);
        spells.add(O2SpellType.COLOVARIA_VERMICULO);
    }
}
