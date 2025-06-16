package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Confronting the Faceless - 6th year Defense Against the Dark Arts book
 *
 * @link https://harrypotter.fandom.com/wiki/Confronting_the_Faceless
 * @author Azami7
 * @since 2.2.4
 */
public class CONFRONTING_THE_FACELESS extends O2Book {
    public CONFRONTING_THE_FACELESS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.CONFRONTING_THE_FACELESS;

        spells.add(O2SpellType.EPISKEY);
        spells.add(O2SpellType.PROTEGO_MAXIMA);
        spells.add(O2SpellType.FLAGRANTE);
        spells.add(O2SpellType.LEGILIMENS);
        // todo Expecto Patronum - https://harrypotter.fandom.com/wiki/Patronus_Charm
        spells.add(O2SpellType.LEVICORPUS);
        spells.add(O2SpellType.LIBERACORPUS);
        spells.add(O2SpellType.OPPUGNO);
        // todo mov fotia rework - purple sparks + pacify
        // todo hex breaker
        spells.add(O2SpellType.VERDIMILLIOUS_TRIA);
    }
}
