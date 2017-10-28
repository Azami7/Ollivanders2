package net.pottercraft.Ollivanders2.Book;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Standard Book of Spells Grade 5
 *
 * Missing spells:
 * Substantive Charm - https://github.com/Azami7/Ollivanders2/issues/96
 * Rictusempra - https://github.com/Azami7/Ollivanders2/issues/92
 *
 * @since 2.2.4
 * @author Azami7
 */
public class STANDARD_BOOK_OF_SPELLS_GRADE_5 extends Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_5 ()
   {
      title = shortTitle = "Standard Book of Spells Grade 5";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spellList.add(Spells.DEPULSO);
      spellList.add(Spells.EXPELLIARMUS);
      spellList.add(Spells.INCENDIO);
      spellList.add(Spells.LEVICORPUS);
      spellList.add(Spells.LIBERACORPUS);
      spellList.add(Spells.PROTEGO);
      spellList.add(Spells.STUPEFY);
      spellList.add(Spells.INCENDIO_DUO);
      //spellList.add(Spells.RICTUSEMPRA);
      //spellList.add(Spells.SUBSTANTIVUM);
   }
}
