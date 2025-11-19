package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * A Guide to Advanced Transfiguration - N.E.W.T level Transfiguration book.
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.AVIS}<br>
 * {@link net.pottercraft.ollivanders2.spell.PULLUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.FELIS}<br>
 * {@link net.pottercraft.ollivanders2.spell.CANIS}<br>
 * {@link net.pottercraft.ollivanders2.spell.AMATO_ANIMO_ANIMATO_ANIMAGUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.BOS}<br>
 * {@link net.pottercraft.ollivanders2.spell.EQUUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.LAMA}<br>
 * {@link net.pottercraft.ollivanders2.spell.URSUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.SUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.DRACONIFORS}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/A_Guide_to_Advanced_Transfiguration">https://harrypotter.fandom.com/wiki/A_Guide_to_Advanced_Transfiguration</a>
 */
public class ADVANCED_TRANSFIGURATION extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public ADVANCED_TRANSFIGURATION(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.ADVANCED_TRANSFIGURATION;

        // 6th year
        spells.add(O2SpellType.AVIS);
        // todo other small animal conjuration like snake summoning spell - https://harrypotter.fandom.com/wiki/Snake_Summons_Spell
        spells.add(O2SpellType.PULLUS);
        spells.add(O2SpellType.FELIS);
        spells.add(O2SpellType.CANIS);
        // todo frog
        // todo rabbit
        // todo sheep
        // todo turtle
        // todo fox
        // todo goat
        spells.add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);

        // 7th year
        spells.add(O2SpellType.BOS);
        spells.add(O2SpellType.EQUUS);
        spells.add(O2SpellType.LAMA);
        spells.add(O2SpellType.URSUS);
        spells.add(O2SpellType.SUS);
        // todo camel
        // todo donkey
        // todo goat
        // due to dragons being huge mobs in MC, this is now a NEWT-level spell
        spells.add(O2SpellType.DRACONIFORS);
    }
}
