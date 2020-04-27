package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;

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
public class STANDARD_BOOK_OF_SPELLS_GRADE_5 extends O2Book
{
   public STANDARD_BOOK_OF_SPELLS_GRADE_5 (Ollivanders2 plugin)
   {
      super(plugin);

      title = shortTitle = "Standard Book of Spells Grade 5";
      author = "Miranda Goshawk";
      branch = O2MagicBranch.CHARMS;

      spells.add(O2SpellType.DEPULSO);
      spells.add(O2SpellType.EXPELLIARMUS);
      spells.add(O2SpellType.INCENDIO);
      spells.add(O2SpellType.LEVICORPUS);
      spells.add(O2SpellType.LIBERACORPUS);
      spells.add(O2SpellType.PROTEGO);
      spells.add(O2SpellType.STUPEFY);
      spells.add(O2SpellType.INCENDIO_DUO);
      //spells.add(O2SpellType.RICTUSEMPRA);
      //spells.add(O2SpellType.SUBSTANTIVUM);
      //11
   }
}
