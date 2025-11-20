package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Fantastic Beasts and Where to Find Them
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Fantastic_Beasts_and_Where_to_Find_Them">https://harrypotter.fandom.com/wiki/Fantastic_Beasts_and_Where_to_Find_Them</a>
 */
public class FANTASTIC_BEASTS extends O2Book {
    // todo make creature summoning and banishment charms

    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public FANTASTIC_BEASTS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        // bookType = O2BookType.FANTASTIC_BEASTS;
    }
}
