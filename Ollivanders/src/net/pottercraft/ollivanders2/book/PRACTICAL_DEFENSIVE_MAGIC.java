package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Practical Defensive Magic - sent to Harry by Sirius and Lupin in his 5th year
 *
 * @link https://harrypotter.fandom.com/wiki/Practical_Defensive_Magic_and_Its_Use_Against_the_Dark_Arts
 * @author Azami7
 * @since 2.2.4
 */
public class PRACTICAL_DEFENSIVE_MAGIC extends O2Book {
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