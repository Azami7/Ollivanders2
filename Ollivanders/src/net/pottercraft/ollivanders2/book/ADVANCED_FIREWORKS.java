package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Non-cannon book written by George Weasley on firework making.
 *
 * @author Azami7
 * @since 2.2.4
 */
public final class ADVANCED_FIREWORKS extends O2Book {
    public ADVANCED_FIREWORKS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.ADVANCED_FIREWORKS;

        spells.add(O2SpellType.BOTHYNUS_DUO);
        spells.add(O2SpellType.COMETES_DUO);
        spells.add(O2SpellType.PERICULUM_DUO);
        spells.add(O2SpellType.PORFYRO_ASTERI_DUO);
        spells.add(O2SpellType.MEGA_PYRO_PRASINA);
        spells.add(O2SpellType.BOTHYNUS_TRIA);
        spells.add(O2SpellType.PORFYRO_ASTERI_TRIA);
    }
}
