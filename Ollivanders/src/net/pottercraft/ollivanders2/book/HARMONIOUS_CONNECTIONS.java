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
 * @since 2.2.4
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
                + "Vanishing cabinets are made as twins and are used to connect to specific locations to each other."
                + "\n\nVanishing cabinet construction is very precise and the cabinets will only work if the directions are followed exactly."
                + "\n\nStep 1 - determine the XYZ coordinates for the cabinets. These must be in whole numbers."
                + "\n\nStep 2 - create a sign at each XYZ coordinate that contains the coordinates for the other cabinet."
                + "Write the world name on the first line, the X coordinate on the second, Y on the third, and Z on the fourth."
                + "\n\nStep 3 - place any type of solid block 2-blocks high on each side, in front, and in back of the sign - 4 blocks total."
                + "\n\nStep 4 - leave an air block above the sign and place any type of solid block above that air block."
                + "\n\nStep 5 - construct the other cabinet following steps 3 and 4."
                + "\n\nStep 6 - cast the vanishing cabinet repair spell, Harmonia Nectere Passus, at either of the two signs."
                + "\n\nYou can now walk in to the cabinet and appear in the other.";

        spells.add(O2SpellType.HARMONIA_NECTERE_PASSUS);
    }
}
