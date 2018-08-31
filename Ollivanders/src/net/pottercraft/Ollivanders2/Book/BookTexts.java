package net.pottercraft.Ollivanders2.Book;

import java.util.Map;
import java.util.HashMap;

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

   private Map <O2SpellType, BookText> O2SpellTextMap = new HashMap<>();
   private Map <O2PotionType, BookText> O2PotionTextMap = new HashMap<>();

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
            spell = (Teachable)spellClass.getConstructor().newInstance();
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
         O2SpellTextMap.put(spellType, sText);
      }
   }

   /**
    * Add the learnable text for every potion.
    */
   private void addPotions ()
   {
      for (O2PotionType potionType : O2PotionType.values())
      {
         Class potionClass = potionType.getClassName();
         Teachable spell;

         try
         {
            spell = (Teachable) potionClass.getConstructor(Ollivanders2.class).newInstance(p);
         }
         catch (Exception e)
         {
            p.getLogger().warning("Exception trying to add book text for " + potionType.toString());
            e.printStackTrace();

            continue;
         }

         String text = spell.getText();
         String flavorText = spell.getFlavorText();

         if (text == null)
         {
            p.getLogger().warning("No book text for " + potionType.toString());
            continue;
         }

         String name = p.common.firstLetterCapitalize(p.common.enumRecode(potionType.toString().toLowerCase()));

         BookText sText = new BookText(name, text, flavorText);
         O2PotionTextMap.put(potionType, sText);
      }
   }

   /**
    * Get the flavor text for a specific magic.
    *
    * @param magic the spellType of the magic topic
    * @return the flavor text for that spell or null if it has none.
    */
   String getFlavorText (String magic)
   {
      String flavorText = null;

      if (O2SpellTextMap.containsKey(magic))
         flavorText = O2SpellTextMap.get(magic).getFlavorText();
      else if (O2PotionTextMap.containsKey(magic))
         flavorText = O2PotionTextMap.get(magic).getFlavorText();

      return flavorText;
   }

   /**
    * Get the description text for a specific magic.
    *
    * @param magic the spellType of the magic topic
    * @return the description text for this spell
    */
   String getText (String magic)
   {
      String text = null;

      if (O2SpellTextMap.containsKey(magic))
         text = O2SpellTextMap.get(magic).getText();
      else if (O2PotionTextMap.containsKey(magic))
         text = O2PotionTextMap.get(magic).getText();

      return text;
   }

   /**
    * Get the printable spellType for a specific magic.
    *
    * @param magic the spellType of the magic topic
    * @return the printable spellType for this magic
    */
   public String getName (String magic)
   {
      String name = null;

      if (O2SpellTextMap.containsKey(magic))
         name = O2SpellTextMap.get(magic).getName();
      else if (O2PotionTextMap.containsKey(magic))
         name = O2PotionTextMap.get(magic).getName();

      return name;
   }
}
