package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Secrets of the Darkest Art - The only known book that explains how to make a Horcrux.
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.ET_INTERFICIAM_ANIMAM_LIGAVERIS}<br>
 * {@link net.pottercraft.ollivanders2.spell.VENTO_FOLIO}<br>
 * {@link net.pottercraft.ollivanders2.spell.SCUTO_CONTERAM}
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Secrets_of_the_Darkest_Art">https://harrypotter.fandom.com/wiki/Secrets_of_the_Darkest_Art</a>
 */
public class SECRETS_OF_THE_DARKEST_ART extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public SECRETS_OF_THE_DARKEST_ART(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.SECRETS_OF_THE_DARKEST_ART;

        spells.add(O2SpellType.ET_INTERFICIAM_ANIMAM_LIGAVERIS);
        spells.add(O2SpellType.VENTO_FOLIO);
        spells.add(O2SpellType.SCUTO_CONTERAM);
    }
}
