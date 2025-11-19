package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Non-cannon book written by George Weasley on firework making.
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.BOTHYNUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.COMETES}<br>
 * {@link net.pottercraft.ollivanders2.spell.PERICULUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.PORFYRO_ASTERI}<br>
 * {@link net.pottercraft.ollivanders2.spell.PYRO_PRASINA}
 * </p>
 *
 * @author Azami7
 */
public final class BASIC_FIREWORKS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public BASIC_FIREWORKS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.BASIC_FIREWORKS;

        spells.add(O2SpellType.BOTHYNUS);
        spells.add(O2SpellType.COMETES);
        spells.add(O2SpellType.PERICULUM);
        spells.add(O2SpellType.PORFYRO_ASTERI);
        spells.add(O2SpellType.PYRO_PRASINA);
    }
}
