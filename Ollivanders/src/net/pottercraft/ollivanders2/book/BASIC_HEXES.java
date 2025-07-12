package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Basic Hexes for the Busy and Vexed
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.MUCUS_AD_NAUSEAM}<br>
 * {@link net.pottercraft.ollivanders2.spell.IMPEDIMENTA}<br>
 * {@link net.pottercraft.ollivanders2.spell.IMMOBULUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.OBSCURO}
 * </p>
 *
 * @see <a href="https://harrypotter.wikia.com/wiki/Basic_Hexes_for_the_Busy_and_Vexed">https://harrypotter.wikia.com/wiki/Basic_Hexes_for_the_Busy_and_Vexed</a>
 */
public class BASIC_HEXES extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public BASIC_HEXES(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.BASIC_HEXES;

        spells.add(O2SpellType.MUCUS_AD_NAUSEAM);
        spells.add(O2SpellType.IMPEDIMENTA);
        spells.add(O2SpellType.IMMOBULUS);
        spells.add(O2SpellType.OBSCURO);
        // todo pepper breath - https://harrypotter.fandom.com/wiki/Pepper_Breath
        // todo counter-hex spell
        // todo weakening hex - https://harrypotter.fandom.com/wiki/Weakening_Hex
        // todo hurling hex - https://harrypotter.fandom.com/wiki/Hurling_Hex
    }
}
