package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2SpellType;

/**
 * Omens, Oracles & the Goat was a book by the celebrated magical historian, Bathilda Bagshot, covering a historical overview of Divination practises.
 * http://harrypotter.wikia.com/wiki/Omens,_Oracles_%26_the_Goat
 *
 * @author Azami7
 * @since 2.2.9
 */
public class OMENS_ORACLES_AND_THE_GOAT extends O2Book
{
    public OMENS_ORACLES_AND_THE_GOAT (Ollivanders2 plugin)
    {
        super(plugin);

        shortTitle = title = "Omens, Oracles & the Goat";
        author = "Bathilda Bagshot";
        branch = O2MagicBranch.DIVINATION;

        spells.add(O2SpellType.OVOGNOSIS);
       spells.add(O2SpellType.CARTOMANCIE);
    }
}
