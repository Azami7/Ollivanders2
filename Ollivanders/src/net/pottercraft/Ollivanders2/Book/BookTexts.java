package net.pottercraft.Ollivanders2.Book;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Constructor;

import net.pottercraft.Ollivanders2.Ollivanders2Common;
import net.pottercraft.Ollivanders2.Spell.Spells;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Teachable;
import net.pottercraft.Ollivanders2.Potion.O2Potion;

/**
 * The text and flavor text for all Ollivanders2 magic.  This can be used for writing books, creating lessons, or
 * other in-game magic learning.
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class BookTexts
{
   private class SpellText
   {
      String name;
      String text;
      String flavorText;

      SpellText (String n, String t, String f)
      {
         name = n;
         text = t;
         flavorText = f;
      }

      public String getName ()
      {
         return name;
      }

      public String getText ()
      {
         return text;
      }

      public String getFlavorText ()
      {
         return flavorText;
      }
   }

   private Map <String, SpellText> O2MagicTextMap = new HashMap<>();

   private Ollivanders2 p;

   /**
    * Constructor.
    *
    * @param plugin the MC plugin
    */
   BookTexts (Ollivanders2 plugin)
   {
      p = plugin;

      // add all spell's texts
      addSpells();
      // add all potion's texts
      addPotions();
   }

   /**
    * Add the spell text for every registered spell projectile.
    */
   private void addSpells ()
   {
      for (Spells s : Spells.values())
      {
         if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libsDisguisesSpells.contains(s))
         {
            continue;
         }
         
         String spellName = "net.pottercraft.Ollivanders2.Spell." + s.toString();

         Constructor c;

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

         Teachable spell;
         try
         {
            spell = (Teachable)c.newInstance();
         }
         catch (Exception e)
         {
            p.getLogger().warning("Exception trying to create a new instance of " + spellName);
            e.printStackTrace();

            continue;
         }

         String text;
         String flavorText;

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

         String name = Ollivanders2Common.firstLetterCapitalize(Ollivanders2Common.enumRecode(s.toString().toLowerCase()));

         SpellText sText = new SpellText(name, text, flavorText);
         O2MagicTextMap.put(s.toString(), sText);
      }
   }

   private void addPotions ()
   {
      for (O2Potion potion : p.getPotions().getPotions())
      {
         SpellText sText = new SpellText(potion.getName(), potion.getText(), potion.getFlavorText());
         O2MagicTextMap.put(potion.getName(), sText);
      }
   }

   /**
    * Get the flavor text for a specific magic.
    *
    * @param magic the name of the magic topic
    * @return the flavor text for that spell or null if it has none.
    */
   String getFlavorText (String magic)
   {
      String flavorText = null;

      if (O2MagicTextMap.containsKey(magic))
         flavorText = O2MagicTextMap.get(magic).getFlavorText();

      return flavorText;
   }

   /**
    * Get the description text for a specific magic.
    *
    * @param magic the name of the magic topic
    * @return the description text for this spell
    */
   String getText (String magic)
   {
      String text = null;

      if (O2MagicTextMap.containsKey(magic))
         text = O2MagicTextMap.get(magic).getText();

      return text;
   }

   /**
    * Get the printable name for a specific magic.
    *
    * @param magic the name of the magic topic
    * @return the printable name for this magic
    */
   public String getName (String magic)
   {
      String name = null;

      if (O2MagicTextMap.containsKey(magic))
         name = O2MagicTextMap.get(magic).getName();

      return name;
   }
}
