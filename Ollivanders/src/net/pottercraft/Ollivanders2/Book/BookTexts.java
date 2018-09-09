package net.pottercraft.Ollivanders2.Book;

import java.util.Map;
import java.util.HashMap;

import net.pottercraft.Ollivanders2.Ollivanders2Common;
import net.pottercraft.Ollivanders2.Potion.O2PotionType;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Teachable;

/**
 * The text and flavor text for all Ollivanders2 magic.  This can be used for writing books, creating lessons, or
 * other in-game magic learning.
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class BookTexts
{
   private class BookText
   {
      String name;
      String text;
      String flavorText;

      BookText (String n, String t, String f)
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

   private Map <String, BookText> O2MagicTextMap = new HashMap<>();

   private Ollivanders2 p;

   /**
    * Constructor.
    *
    * @param plugin the MC plugin
    */
   BookTexts (Ollivanders2 plugin)
   {
      p = plugin;

      // add all spells' texts
      addSpells();
      // add all potions' texts
      addPotions();
   }

   /**
    * Add the learnable text for every registered spell projectile.
    */
   private void addSpells ()
   {
      for (O2SpellType spellType : O2SpellType.values())
      {
         if (!Ollivanders2.libsDisguisesEnabled && p.common.libsDisguisesSpells.contains(spellType))
         {
            continue;
         }

         Teachable spell;
         Class spellClass = spellType.getClassName();

         try
         {
            spell = (Teachable)spellClass.getConstructor(O2SpellType.class).newInstance(spellType);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Exception trying to add book text for " + spellType.toString());
            e.printStackTrace();

            continue;
         }

         String text = spell.getText();
         String flavorText = spell.getFlavorText();

         if (text == null)
         {
            p.getLogger().warning("No book text for " + spellType.toString());
            continue;
         }

         String name = spell.getName();

         BookText sText = new BookText(name, text, flavorText);
         O2MagicTextMap.put(spellType.toString(), sText);
      }
   }

   /**
    * Add the learnable text for every potion.
    */
   private void addPotions ()
   {
      for (O2PotionType potionType : O2PotionType.values())
      {
         if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potionType))
         {
            continue;
         }

         Class potionClass = potionType.getClassName();
         Teachable potion;

         try
         {
            potion = (Teachable) potionClass.getConstructor(Ollivanders2.class, O2PotionType.class).newInstance(p, potionType);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Exception trying to add book text for " + potionType.toString());
            e.printStackTrace();

            continue;
         }

         String text = potion.getText();
         String flavorText = potion.getFlavorText();

         if (text == null)
         {
            p.getLogger().warning("No book text for " + potionType.toString());
            continue;
         }

         String name = p.common.firstLetterCapitalize(p.common.enumRecode(potionType.toString().toLowerCase()));

         BookText sText = new BookText(name, text, flavorText);
         O2MagicTextMap.put(potionType.toString(), sText);
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
