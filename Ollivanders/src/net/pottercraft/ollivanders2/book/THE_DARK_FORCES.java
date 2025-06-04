package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Dark Forces: A Guide to Self-Protection - 1st and 2nd year Defense Against the Dark Arts
 * https://harrypotter.fandom.com/wiki/The_Dark_Forces:_A_Guide_to_Self-Protection
 *
 * @author Azami7
 * @since 2.2.4
 */
public class THE_DARK_FORCES extends O2Book {
    public THE_DARK_FORCES(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.THE_DARK_FORCES;

        // 1st year
        spells.add(O2SpellType.LUMOS);
        spells.add(O2SpellType.NOX);
        spells.add(O2SpellType.FLIPENDO);
        spells.add(O2SpellType.FUMOS);
        spells.add(O2SpellType.VERDIMILLIOUS);
        spells.add(O2SpellType.MUCUS_AD_NAUSEAM);
        spells.add(O2SpellType.VERMILLIOUS);

        // 2nd year
        spells.add(O2SpellType.EXPELLIARMUS);
        spells.add(O2SpellType.FINITE_INCANTATEM);
        spells.add(O2SpellType.VERDIMILLIOUS_DUO);
        spells.add(O2SpellType.VERMILLIOUS_DUO);
        spells.add(O2SpellType.PETRIFICUS_TOTALUS);
        spells.add(O2SpellType.SPONGIFY);
    }
}
