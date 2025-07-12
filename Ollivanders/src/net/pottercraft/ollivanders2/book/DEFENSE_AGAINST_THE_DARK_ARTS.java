package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Defence Against the Dark Arts by Galatea Merrythought - 7th year Defense against the dark arts book
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.ASCENDIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.MORTUOS_SUSCITATE}<br>
 * {@link net.pottercraft.ollivanders2.spell.INCENDIO_TRIA}<br>
 * {@link net.pottercraft.ollivanders2.spell.NULLUM_APPAREBIT}<br>
 * {@link net.pottercraft.ollivanders2.spell.NULLUM_EVANESCUNT}<br>
 * {@link net.pottercraft.ollivanders2.spell.PRIOR_INCANTATO}<br>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO_TOTALUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO_HORRIBILIS}
 * </p>
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Defence_Against_the_Dark_Arts_(book)">https://harrypotter.fandom.com/wiki/Defence_Against_the_Dark_Arts_(book)</a>
 * @author Azami7
 * @since 2.21
 */
public class DEFENSE_AGAINST_THE_DARK_ARTS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public DEFENSE_AGAINST_THE_DARK_ARTS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.DEFENSE_AGAINST_THE_DARK_ARTS;

        spells.add(O2SpellType.ASCENDIO);
        spells.add(O2SpellType.MORTUOS_SUSCITATE);
        // todo counter to kill zombies
        spells.add(O2SpellType.INCENDIO_TRIA);
        spells.add(O2SpellType.NULLUM_APPAREBIT);
        spells.add(O2SpellType.NULLUM_EVANESCUNT);
        spells.add(O2SpellType.PRIOR_INCANTATO);
        spells.add(O2SpellType.PROTEGO_TOTALUM);
        spells.add(O2SpellType.PROTEGO_HORRIBILIS);
        // todo Repello Inimicum - https://harrypotter.fandom.com/wiki/Repello_Inimicum
        // todo langlock - https://harrypotter.fandom.com/wiki/Langlock
        // todo sea urchin jinx - https://harrypotter.fandom.com/wiki/Sea_Urchin_Jinx
        // todo cave inmicum - https://harrypotter.fandom.com/wiki/Cave_inimicum
    }
}
