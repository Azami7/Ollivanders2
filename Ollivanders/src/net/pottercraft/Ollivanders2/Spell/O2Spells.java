package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.HashMap;
import java.util.Map;

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
         O2Spell spell = getSpellFromType(spellType);

         if (spell != null)
         {
            O2SpellMap.put(spell.getName(), spellType);
         }
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
    * @param name the name of the potion
    * @return the type if found, null otherwise
    */
   public O2SpellType getSpellTypeByName (String name)
   {
      if (O2SpellMap.containsKey(name))
         return O2SpellMap.get(name);
      else
         return null;
   }

   /**
    * Get potion name by type
    *
    * @param spellType the potion type
    * @return the name if found, null otherwise
    */
   public String getSpellNameByType (O2SpellType spellType)
   {
      if (O2SpellMap.containsValue(spellType))
      {
         for (Map.Entry<String, O2SpellType> e : O2SpellMap.entrySet())
         {
            if (e.getValue().equals(spellType))
               return e.getKey();
         }
      }

      return null;
   }
}
