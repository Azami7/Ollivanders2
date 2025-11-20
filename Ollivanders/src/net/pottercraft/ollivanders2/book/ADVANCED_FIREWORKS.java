package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Non-cannon book written by George Weasley on firework making
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.BOTHYNUS_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.COMETES_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.PERICULUM_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.PORFYRO_ASTERI_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.MEGA_PYRO_PRASINA}<br>
 * {@link net.pottercraft.ollivanders2.spell.BOTHYNUS_TRIA}<br>
 * {@link net.pottercraft.ollivanders2.spell.PORFYRO_ASTERI_TRIA}
 * </p>
 *
 * @author Azami7
 */
public final class ADVANCED_FIREWORKS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
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
