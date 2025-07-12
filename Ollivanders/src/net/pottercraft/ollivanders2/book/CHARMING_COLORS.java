package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Charming Colors - spells for changing colors
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.COLOVARIA}<br>
 * {@link net.pottercraft.ollivanders2.spell.COLOVARIA_ALBUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.COLOVARIA_AURANTIACO}<br>
 * {@link net.pottercraft.ollivanders2.spell.COLOVARIA_CAERULUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.COLOVARIA_FLAVO}<br>
 * {@link net.pottercraft.ollivanders2.spell.COLOVARIA_OSTRUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.COLOVARIA_VERIDI}<br>
 * {@link net.pottercraft.ollivanders2.spell.COLOVARIA_VERMICULO}
 * </p>
 *
 * @author Azami7
 * @since 2.2.4
 */
public final class CHARMING_COLORS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
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
