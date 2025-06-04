package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * A Guide to Advanced Transfiguration - N.E.W.T level Transfiguration book
 * <p>
 * Topics:
 * Conjuration
 * Human Transfiguration
 * Untransfiguration
 * <p>
 * Missing Spell:
 * Homorphus Charm - https://github.com/Azami7/Ollivanders2/issues/39
 *
 * @author Azami7
 * @since 2.2.4
 */
public class ADVANCED_TRANSFIGURATION extends O2Book {
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
        // due to dragons only being huge mobs in MC, this is now a NEWT-level spell
        spells.add(O2SpellType.DRACONIFORS);
    }
}
