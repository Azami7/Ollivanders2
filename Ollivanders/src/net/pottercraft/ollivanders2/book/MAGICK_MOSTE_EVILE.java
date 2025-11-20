package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Magick Moste Evile - O2Book of Dark Magic written in the Middle Ages
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.FIENDFYRE}<br>
 * {@link net.pottercraft.ollivanders2.spell.AVADA_KEDAVRA}<br>
 * {@link net.pottercraft.ollivanders2.spell.FLAGRANTE}<br>
 * {@link net.pottercraft.ollivanders2.spell.LEGILIMENS}<br>
 * {@link net.pottercraft.ollivanders2.spell.SCUTO_CONTERAM}<br>
 * {@link net.pottercraft.ollivanders2.spell.AMATO_ANIMO_ANIMATO_ANIMAGUS}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Magick_Moste_Evile">https://harrypotter.fandom.com/wiki/Magick_Moste_Evile</a>
 */
public class MAGICK_MOSTE_EVILE extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public MAGICK_MOSTE_EVILE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.MAGICK_MOSTE_EVILE;

        spells.add(O2SpellType.FIENDFYRE);
        spells.add(O2SpellType.AVADA_KEDAVRA);
        spells.add(O2SpellType.FLAGRANTE);
        spells.add(O2SpellType.LEGILIMENS);
        spells.add(O2SpellType.SCUTO_CONTERAM);
        spells.add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
        // todo protego diabolica - https://harrypotter.fandom.com/wiki/Protego_Diabolica
        // todo Homorphus Charm - https://github.com/Azami7/Ollivanders2/issues/39

        closingPage = "\n\nOf the Horcrux, wickedest of magical inventions, we shall not speak nor give direction.";
    }
}
