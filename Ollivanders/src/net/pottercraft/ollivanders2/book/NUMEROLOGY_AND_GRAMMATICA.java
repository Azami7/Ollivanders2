package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Numerology and Grammatica - Arithmancy text
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.INFORMOUS}<br>
 * {@link net.pottercraft.ollivanders2.spell.POINT_ME}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Numerology_and_Grammatica">https://harrypotter.fandom.com/wiki/Numerology_and_Grammatica</a>
 */
public class NUMEROLOGY_AND_GRAMMATICA extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public NUMEROLOGY_AND_GRAMMATICA(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.NUMEROLOGY_AND_GRAMMATICA;

        openingPage = "The study of Arithmancy is not for the weak of mind. With work and dedication, one can learn the secrets of the Universe by understanding the language of numbers.";

        spells.add(O2SpellType.INFORMOUS);
        spells.add(O2SpellType.POINT_ME);
    }
}
