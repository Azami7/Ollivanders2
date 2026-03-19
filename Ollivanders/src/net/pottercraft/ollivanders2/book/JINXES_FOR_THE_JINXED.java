package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.AGNIFORS_MAXIMA;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Jinxes for the Jinxed
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.ENTOMORPHIS}<br>
 * {@link net.pottercraft.ollivanders2.spell.IMPEDIMENTA}<br>
 * {@link net.pottercraft.ollivanders2.spell.LEVICORPUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.LACARNUM_INFLAMARI}<br>
 * {@link net.pottercraft.ollivanders2.spell.EBUBLIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.METELOJINX}<br>
 * {@link net.pottercraft.ollivanders2.spell.METELOJINX_RECANTO}<br>
 * {@link net.pottercraft.ollivanders2.spell.LAPIFORS_MAXIMA}<br>
 * {@link net.pottercraft.ollivanders2.spell.CANIFORS_MAXIMA}<br>
 * {@link net.pottercraft.ollivanders2.spell.FELIFORS_MAXIMA}<br>
 * {@link net.pottercraft.ollivanders2.spell.BOVIFORS_MAXIMA}<br>
 * {@link net.pottercraft.ollivanders2.spell.SUIFORS_MAXIMA}<br>
 * {@link AGNIFORS_MAXIMA}<br>
 * {@link net.pottercraft.ollivanders2.spell.EQUIFORS_MAXIMA}<br>
 * {@link net.pottercraft.ollivanders2.spell.URSIFORS_MAXIMA}<br>
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Jinxes_for_the_Jinxed">https://harrypotter.fandom.com/wiki/Jinxes_for_the_Jinxed</a>
 */
public class JINXES_FOR_THE_JINXED extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public JINXES_FOR_THE_JINXED(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.JINXES_FOR_THE_JINXED;

        openingPage = "Learn some jinxes to add to your arsenal with this handy volume.";

        spells.add(O2SpellType.ENTOMORPHIS);
        spells.add(O2SpellType.IMPEDIMENTA);
        spells.add(O2SpellType.LEVICORPUS);
        spells.add(O2SpellType.LACARNUM_INFLAMARI);
        spells.add(O2SpellType.EBUBLIO);
        spells.add(O2SpellType.METELOJINX);
        spells.add(O2SpellType.METELOJINX_RECANTO);
        spells.add(O2SpellType.DUCKLIFORS_MAXIMA);
        spells.add(O2SpellType.LAPIFORS_MAXIMA);
        spells.add(O2SpellType.CANIFORS_MAXIMA);
        spells.add(O2SpellType.FELIFORS_MAXIMA);
        // todo jelly legs jinx https://harrypotter.fandom.com/wiki/Jelly-Legs_Jinx
        // todo trip jinx - https://harrypotter.fandom.com/wiki/Trip_Jinx
        // todo anti-jinx spell
        spells.add(O2SpellType.BOVIFORS_MAXIMA);
        spells.add(O2SpellType.SUIFORS_MAXIMA);
        spells.add(O2SpellType.AGNIFORS_MAXIMA);
        spells.add(O2SpellType.EQUIFORS_MAXIMA);
        spells.add(O2SpellType.URSIFORS_MAXIMA);
    }
}
