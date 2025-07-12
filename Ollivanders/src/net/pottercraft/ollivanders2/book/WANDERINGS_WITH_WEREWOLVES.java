package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Wanderings with Werewolves - 2nd year Defense Against the Dark Arts book
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.CONFUNDUS_DUO}<br>
 * {@link net.pottercraft.ollivanders2.spell.OBLIVIATE}
 * </p>
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Wanderings_with_Werewolves">https://harrypotter.fandom.com/wiki/Wanderings_with_Werewolves</a>
 * @author Azami7
 * @since 2.2.4
 */
public class WANDERINGS_WITH_WEREWOLVES extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public WANDERINGS_WITH_WEREWOLVES(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.WANDERINGS_WITH_WEREWOLVES;

        spells.add(O2SpellType.CONFUNDUS_DUO);
        spells.add(O2SpellType.OBLIVIATE);
        // todo homorphus - https://harrypotter.fandom.com/wiki/Homorphus_Charm
    }
}
