package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Quintessence: A Quest - 6th year Charms book
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.CONFUNDUS_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.APARECIUM}<br>
 * {@link net.pottercraft.ollivanders2.spell.GLACIUS_TRIA}<br>
 * {@link net.pottercraft.ollivanders2.spell.MOLLIARE}
 * </p>
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Quintessence:_A_Quest">https://harrypotter.fandom.com/wiki/Quintessence:_A_Quest</a>
 * @author Azami7
 * @since 2.2.4
 */
public class QUINTESSENCE_A_QUEST extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public QUINTESSENCE_A_QUEST(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.QUINTESSENCE_A_QUEST;

        spells.add(O2SpellType.CONFUNDUS_DUO);
        spells.add(O2SpellType.APARECIUM);
        spells.add(O2SpellType.CELATUM);
        spells.add(O2SpellType.GLACIUS_TRIA);
        spells.add(O2SpellType.MOLLIARE);
    }
}
