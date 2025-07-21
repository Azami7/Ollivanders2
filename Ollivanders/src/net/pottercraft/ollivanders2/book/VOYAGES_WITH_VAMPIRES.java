package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Voyages with Vampires - 2nd year Defense Against the Dark Arts book
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.OBLIVIATE}
 * {@link net.pottercraft.ollivanders2.spell.LUMOS_CAERULEUM}
 * </p>
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Voyages_with_Vampires">https://harrypotter.fandom.com/wiki/Voyages_with_Vampires</a>
 * @author Azami7
 * @since 2.2.4
 */
public class VOYAGES_WITH_VAMPIRES extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public VOYAGES_WITH_VAMPIRES(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.VOYAGES_WITH_VAMPIRES;

        spells.add(O2SpellType.OBLIVIATE);
        spells.add(O2SpellType.LUMOS_CAERULEUM);
        // todo Peskipiksi Pesternomi - https://harrypotter.fandom.com/wiki/Peskipiksi_Pesternomi
    }
}
