package net.pottercraft.Ollivanders2.Book;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;

import net.pottercraft.Ollivanders2.Ollivanders2Common;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Spell.O2Spell;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * The text and flavor text for all Ollivanders2 spells.  This can be used for writing books, creating lessons, or
 * other in-game spell learning.
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class SpellText
{
   private static Map <Spells, String> O2FlavorTextMap = new HashMap<>();
   private static Map <Spells, String> O2TextMap = new HashMap<>();

   private Ollivanders2 p;

   /**
    * Constructor.
    *
    * @param plugin
    */
   public SpellText (Ollivanders2 plugin)
   {
      p = plugin;

      // add all spell's texts
      addSpells();
   }

   /**
    * Add the spell text for every registered spell projectile.
    */
   private void addSpells ()
   {
      for (Spells s : Spells.values())
      {
         if (Ollivanders2Common.libsDisguisesSpells.contains(s) && !Ollivanders2.libsDisguisesEnabled)
         {
            continue;
         }
         
         String spellName = "net.pottercraft.Ollivanders2.Spell." + s.toString();

         Constructor c = null;

         try
         {
            c = Class.forName(spellName).getConstructor();
         }
         catch (Exception e)
         {
            p.getLogger().warning("Exception trying to add spell text for " + spellName);
            e.printStackTrace();

            continue;
         }

         O2Spell spell = null;
         try
         {
            spell = (O2Spell)c.newInstance();
         }
         catch (Exception e)
         {
            p.getLogger().warning("Exception trying to create a new instance of " + spellName);
            e.printStackTrace();

            continue;
         }

         String text = null;
         String flavorText = null;

         try
         {
            text = spell.getText();
            flavorText = spell.getFlavorText();
         }
         catch (Exception e)
         {
            p.getLogger().warning("Exception trying to get the spell text for " + spellName);
            e.printStackTrace();

            continue;
         }

         if (text == null)
         {
            p.getLogger().warning("No spell text for " + spellName);
            continue;
         }

         if (!O2TextMap.containsKey(s))
            O2TextMap.put(s, text);

         if (!O2FlavorTextMap.containsKey(s))
            O2FlavorTextMap.put(s, flavorText);
      }
   }

   /**
    * Get the flavor text for a specific spell.
    *
    * @param spell
    * @return the flavor text for that spell or null if it has none.
    */
   public static String getFlavorText (Spells spell)
   {
      String flavorText = O2FlavorTextMap.get(spell);

      return flavorText;
   }

   /**
    * Get the description text for a specific spell.
    *
    * @param spell
    * @return the description text for this spell
    */
   public static String getText (Spells spell)
   {
      String text = O2TextMap.get(spell);

      return text;
   }
}
