package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Extreme Incantations - Charms book for 2nd year.
 * <p>
 * Contents:<br>{@link net.pottercraft.ollivanders2.spell.LUMOS_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.BOMBARDA}<br>
 * {@link net.pottercraft.ollivanders2.spell.BOMBARDA_MAXIMA}<br>
 * {@link net.pottercraft.ollivanders2.spell.COLOVARIA}<br>
 * {@link net.pottercraft.ollivanders2.spell.LUMOS_MAXIMA}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Extreme_Incantations">https://harrypotter.fandom.com/wiki/Extreme_Incantations</a>
 */
public class EXTREME_INCANTATIONS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public EXTREME_INCANTATIONS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.EXTREME_INCANTATIONS;

        spells.add(O2SpellType.LUMOS_DUO);
        spells.add(O2SpellType.BOMBARDA);
        spells.add(O2SpellType.BOMBARDA_MAXIMA);
        spells.add(O2SpellType.COLOVARIA);
        spells.add(O2SpellType.LUMOS_MAXIMA);
    }
}
