package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Secrets of Wandlore
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.FRANGE_LIGNEA}<br>
 * {@link net.pottercraft.ollivanders2.spell.LIGATIS_COR}
 * </p>
 *
 * @author Azami7
 */
public final class SECRETS_OF_WANDLORE extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public SECRETS_OF_WANDLORE(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.SECRETS_OF_WANDLORE;

        openingPage = "Wandlore is an ancient, complex, and mysterious branch of magic dealing with the history, abilities, and actions of wands, quasi-sentient magical tools used by wizards and witches to cast spells.";

        spells.add(O2SpellType.FRANGE_LIGNEA);
        spells.add(O2SpellType.LIGATIS_COR);
    }
}
