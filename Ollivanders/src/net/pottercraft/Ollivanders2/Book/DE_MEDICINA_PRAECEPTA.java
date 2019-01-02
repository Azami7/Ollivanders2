package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;

/**
 * 2nd Century Roman healing text.
 *
 * @author Azami7
 * @sinze 2.2.9
 */
public class DE_MEDICINA_PRAECEPTA extends O2Book
{
   public DE_MEDICINA_PRAECEPTA (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "De Medicina Praecepta";
      author = "Quintus Serenus Sammonicus";
      branch = O2MagicBranch.HEALING;

      openingPage = "Phoebus, protect this health-giving song, which I composed and let this manifest favour be an attendant to the art you discovered.";

      spells.add(O2SpellType.REPARIFORS);
      //Abracadabra
      //Mithridates Antidotum - a powerful antidote to poison
   }
}
