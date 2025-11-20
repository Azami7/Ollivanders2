package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Harmonious Connections
 * <p>
 * Contents:<br>
 * {@link net.pottercraft.ollivanders2.spell.HARMONIA_NECTERE_PASSUS}
 * </p>
 *
 * @author Azami7
 */
public class HARMONIOUS_CONNECTIONS extends O2Book {
    /**
     * Constructor
     *
     * @param plugin a callback to the plugin
     */
    public HARMONIOUS_CONNECTIONS(@NotNull Ollivanders2 plugin) {
        super(plugin);

        bookType = O2BookType.HARMONIOUS_CONNECTIONS;

        openingPage = "Vanishing cabinets are an old but powerful form of magical transportation. "
                + "Vanishing cabinets are made as twins and are used to connect to specific locations to each other.";

        spells.add(O2SpellType.HARMONIA_NECTERE_PASSUS);
    }
}
