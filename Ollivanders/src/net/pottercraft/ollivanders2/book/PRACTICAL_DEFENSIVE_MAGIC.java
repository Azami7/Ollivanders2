package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Practical Defensive Magic - sent to Harry by Sirius and Lupin in his 5th year
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.DEPRIMO}<br>
 * {@link net.pottercraft.ollivanders2.spell.PROTEGO}<br>
 * {@link net.pottercraft.ollivanders2.spell.DISSENDIUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.EXPELLIARMUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.FIANTO_DURI}<br>
 * {@link net.pottercraft.ollivanders2.spell.REDUCTO}<br>
 * {@link net.pottercraft.ollivanders2.spell.IMPEDIMENTA}<br>
 * {@link net.pottercraft.ollivanders2.spell.LACARNUM_INFLAMARI}<br>
 * {@link net.pottercraft.ollivanders2.spell.MUFFLIATO}<br>
 * {@link net.pottercraft.ollivanders2.spell.VERMILLIOUS_TRIA}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Practical_Defensive_Magic_and_Its_Use_Against_the_Dark_Arts">https://harrypotter.fandom.com/wiki/Practical_Defensive_Magic_and_Its_Use_Against_the_Dark_Arts</a>
 */
public class PRACTICAL_DEFENSIVE_MAGIC extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public PRACTICAL_DEFENSIVE_MAGIC(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.PRACTICAL_DEFENSIVE_MAGIC;

        spells.add(O2SpellType.DEPRIMO);
        spells.add(O2SpellType.PROTEGO);
        spells.add(O2SpellType.DISSENDIUM);
        spells.add(O2SpellType.EXPELLIARMUS);
        spells.add(O2SpellType.FIANTO_DURI);
        spells.add(O2SpellType.REDUCTO);
        spells.add(O2SpellType.IMPEDIMENTA);
        spells.add(O2SpellType.LACARNUM_INFLAMARI);
        // todo snake vanishing soell - https://harrypotter.fandom.com/wiki/Snake-Vanishing_Spell
        // todo Everte Statum - make different than flippendo, maybe players vs entities - https://harrypotter.fandom.com/wiki/Everte_Statum
        // todo Homenum Revelio - https://harrypotter.fandom.com/wiki/Human-presence-revealing_Spell
        // todo jinx breaker
        spells.add(O2SpellType.MUFFLIATO);
        spells.add(O2SpellType.VERMILLIOUS_TRIA);
    }
}