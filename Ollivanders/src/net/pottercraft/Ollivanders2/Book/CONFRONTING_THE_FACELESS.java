package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

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
   public CONFRONTING_THE_FACELESS ()
   {
      shortTitle = title = "Confronting the Faceless";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spellList.add(Spells.MORTUOS_SUSCITATE);
      spellList.add(Spells.OPPUGNO);
      spellList.add(Spells.REDUCTO);
      spellList.add(Spells.LEGILIMENS);
      spellList.add(Spells.FLAGRANTE);
      spellList.add(Spells.PRAEPANDO);
      //spellList.add(Spells.CRUCIO);
      //spellList.add(Spells.IMPERIO);
      //spellList.add(Spells.EXPECTO_PATRONUM);
   }
}
