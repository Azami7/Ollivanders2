package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * The Essential Defence Against the Dark Arts. 3rd and 4th year defense against the dark arts
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.LUMOS_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.INCENDIO_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.IMMOBULUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.ARANIA_EXUMAI}<br>
 * {@link net.pottercraft.ollivanders2.spell.ALARTE_ASCENDARE}<br>
 * {@link net.pottercraft.ollivanders2.spell.AQUA_ERUCTO}<br>
 * {@link net.pottercraft.ollivanders2.spell.FUMOS_DUO}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/The_Essential_Defence_Against_the_Dark_Arts">https://harrypotter.fandom.com/wiki/The_Essential_Defence_Against_the_Dark_Arts</a>
 */
public class ESSENTIAL_DARK_ARTS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public ESSENTIAL_DARK_ARTS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.ESSENTIAL_DARK_ARTS;

        // 3rd year
        // todo Riddikulus - https://harrypotter.fandom.com/wiki/Boggart-Banishing_Spell
        spells.add(O2SpellType.LUMOS_DUO);
        spells.add(O2SpellType.INCENDIO_DUO);
        spells.add(O2SpellType.IMMOBULUS);
        // todo spell that slows/injures skeletons
        spells.add(O2SpellType.ARANIA_EXUMAI);
        // todo curse deflection

        // 4th year
        // todo salvio hexia - https://harrypotter.fandom.com/wiki/Hex-deflection
        // todo jinx deflection
        spells.add(O2SpellType.ALARTE_ASCENDARE);
        spells.add(O2SpellType.AQUA_ERUCTO);
        spells.add(O2SpellType.FUMOS_DUO);
        // todo knockback wolf
        // todo set fire to zombie (short duration)
        // todo kill small slime
        // todo curse breaker
    }
}
