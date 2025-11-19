package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Modern Magical Transportation
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.VOLATUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.MOLLIARE}<br>
 * {@link net.pottercraft.ollivanders2.spell.ALIQUAM_FLOO}<br>
 * {@link net.pottercraft.ollivanders2.spell.PORTUS}
 * </p>
 *
 * @author Azami7
 */
public class MODERN_MAGICAL_TRANSPORTATION extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public MODERN_MAGICAL_TRANSPORTATION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.MODERN_MAGICAL_TRANSPORTATION;

        openingPage = "Understanding magical transportation is important for every witch and wizard. In this book we will learn three primary means of transport - Brooms, Floo Powder, and Portkeys.";

        spells.add(O2SpellType.VOLATUS);
        spells.add(O2SpellType.MOLLIARE);
        spells.add(O2SpellType.ALIQUAM_FLOO);
        spells.add(O2SpellType.PORTUS);
    }
}
