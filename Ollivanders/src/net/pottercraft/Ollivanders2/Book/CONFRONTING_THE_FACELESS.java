package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Confronting the Faceless - N.E.W.T level Defense Agaist the Dark Arts book
 *
 * Topics:
 * Inferi
 * Cruciatus Curse
 * Dementors
 * Imperius Curse
 *
 * Missing Spells:
 * Crucio - https://github.com/Azami7/Ollivanders2/issues/88
 * Imperio
 * Dementors
 *
 * @since 2.2.4
 * @author Azami7
 */
public class CONFRONTING_THE_FACELESS extends Book
{
   public CONFRONTING_THE_FACELESS (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Confronting the Faceless";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(Spells.MORTUOS_SUSCITATE);
      spells.add(Spells.OPPUGNO);
      spells.add(Spells.REDUCTO);
      spells.add(Spells.LEGILIMENS);
      spells.add(Spells.FLAGRANTE);
      spells.add(Spells.PRAEPANDO);
      //spells.add(Spells.CRUCIO);
      //spells.add(Spells.IMPERIO);
      //spells.add(Spells.EXPECTO_PATRONUM);
   }
}
