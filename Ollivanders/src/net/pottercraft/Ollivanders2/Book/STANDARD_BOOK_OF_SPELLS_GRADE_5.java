package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Standard O2Book of Spells Grade 5
 *
 * Missing spells:
 * Substantive Charm - https://github.com/Azami7/Ollivanders2/issues/96
 * Rictusempra - https://github.com/Azami7/Ollivanders2/issues/92
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_5 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_5 (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard O2Book of Spells Grade 5";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spells.add(Spells.DEPULSO);
      spells.add(Spells.EXPELLIARMUS);
      spells.add(Spells.INCENDIO);
      spells.add(Spells.LEVICORPUS);
      spells.add(Spells.LIBERACORPUS);
      spells.add(Spells.PROTEGO);
      spells.add(Spells.STUPEFY);
      spells.add(Spells.INCENDIO_DUO);
      //spells.add(Spells.RICTUSEMPRA);
      //spells.add(Spells.SUBSTANTIVUM);
      //11
   }
}
