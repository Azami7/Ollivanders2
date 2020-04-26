package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;

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
public class CONFRONTING_THE_FACELESS extends O2Book
{
   public CONFRONTING_THE_FACELESS (Ollivanders2 plugin)
   {
      super(plugin);

      shortTitle = title = "Confronting the Faceless";
      author = "Unknown";
      branch = O2MagicBranch.DARK_ARTS;

      spells.add(O2SpellType.MORTUOS_SUSCITATE);
      spells.add(O2SpellType.OPPUGNO);
      spells.add(O2SpellType.REDUCTO);
      spells.add(O2SpellType.LEGILIMENS);
      spells.add(O2SpellType.FLAGRANTE);
      spells.add(O2SpellType.PRAEPANDO);
      //spells.add(O2SpellType.CRUCIO);
      //spells.add(O2SpellType.IMPERIO);
      //spells.add(O2SpellType.EXPECTO_PATRONUM);
   }
}
