package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Non-cannon book written by Gellert Grindelwald that is the only location of Morsmordre appears, which makes it
 * Grindelwald's mark that Voldemort took for his own purposes.
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.MORSMORDRE}<br>
 * {@link net.pottercraft.ollivanders2.spell.IMMOBULUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.LEVICORPUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.LIBERACORPUS}
 * </p>
 *
 * @author Azami7
 * @since 2.2.8
 */
public class FOR_THE_GREATER_GOOD extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public FOR_THE_GREATER_GOOD(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.FOR_THE_GREATER_GOOD;

        openingPage = "We've lived in the shadows for far too long, scuttling like rats in the gutter, forced to hide lest we be discovered, forced to conceal our true nature. I refuse to bow down any longer.";

        spells.add(O2SpellType.MORSMORDRE);
        spells.add(O2SpellType.IMMOBULUS);
        spells.add(O2SpellType.LEVICORPUS);
        spells.add(O2SpellType.LEGILIMENS);
        // todo Crucio
        // todo Imperio
        // todo protego diabolica - https://harrypotter.fandom.com/wiki/Protego_Diabolica
    }
}
