package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Harmonious Connections
 *
 * @since 2.2.4
 * @author Azami7
 */
public class HARMONIOUS_CONNECTIONS extends Book
{
   public HARMONIOUS_CONNECTIONS ()
   {
      shortTitle = "Harmonious Connections";
      title = "Harmonious Connections";
      author = "Unknown";
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
            + "\n\nYou can now walk in to the cabinet and appear in the other.  Objects placed in the cabinet will also be transported.";

      branch = O2MagicBranch.CHARMS;

      spellList.add(Spells.HARMONIA_NECTERE_PASSUS);
   }
}
