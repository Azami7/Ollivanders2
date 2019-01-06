package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Manages all spells
 *
 * @author Azami7
 * @since 2.2.8
 */
public class O2Spells
{
   private Ollivanders2 p;

   private HashMap<String, O2SpellType> O2SpellMap = new HashMap<>();

   public static final ArrayList<O2SpellType> wandlessSpells = new ArrayList<O2SpellType>()
   {{
      add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
      add(O2SpellType.ASTROLOGIA);
      add(O2SpellType.OVOGNOSIS);
      add(O2SpellType.INTUEOR);
      add(O2SpellType.MANTEIA_KENTAVROS);
   }};

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    */
   public O2Spells (Ollivanders2 plugin)
   {
      p = plugin;

      for (O2SpellType spellType : O2SpellType.values())
      {
         if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libsDisguisesSpells.contains(spellType))
            continue;

         O2SpellMap.put(spellType.getSpellName().toLowerCase(), spellType);
      }
   }

   /**
    * Get an O2Potions object from its type.
    *
    * @param spellType the type of potion to get
    * @return the O2Spell object, if it could be created, or null otherwise
    */
   private O2Spell getSpellFromType (O2SpellType spellType)
   {
      O2Spell spell;

      Class spellClass = spellType.getClassName();

      try
      {
         spell = (O2Spell)spellClass.getConstructor().newInstance();
      }
      catch (Exception exception)
      {
         p.getLogger().info("Exception trying to create new instance of " + spellType.toString());
         if (Ollivanders2.debug)
            exception.printStackTrace();

         return null;
      }

      return spell;
   }

   /**
    * Get a spell type by name.
    *
    * @param name the name of the spell or potion
    * @return the type if found, null otherwise
    */
   public O2SpellType getSpellTypeByName (String name)
   {
      if (O2SpellMap.containsKey(name.toLowerCase()))
         return O2SpellMap.get(name.toLowerCase());
      else
         return null;
   }
}
