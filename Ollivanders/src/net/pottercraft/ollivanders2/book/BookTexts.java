package net.pottercraft.ollivanders2.book;

import java.util.Map;
import java.util.HashMap;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.potion.O2Potion;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.potion.O2Potions;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.spell.O2Spells;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * The text and flavor text for all Ollivanders2 magic.  This can be used for writing books, creating lessons, or
 * other in-game magic learning.
 *
 * @since 2.2.4
 * @author Azami7
 */
public final class BookTexts
{
   Ollivanders2Common common;

   private static class BookText
   {
      String name;
      String text;
      String flavorText;

      BookText(@NotNull String n, @NotNull String t, @Nullable String f)
      {
         name = n;
         text = t;

         flavorText = f;
      }

      @NotNull
      public String getName ()
      {
         return name;
      }

      @NotNull
      public String getText ()
      {
         return text;
      }

      @Nullable
      public String getFlavorText ()
      {
         return flavorText;
      }
   }

   private final Map<String, BookText> O2MagicTextMap = new HashMap<>();

   private final Ollivanders2 p;

   /**
    * Constructor.
    *
    * @param plugin the MC plugin
    */
   BookTexts(@NotNull Ollivanders2 plugin)
   {
      p = plugin;
      common = new Ollivanders2Common(p);
   }

   /**
    * Add all spells and potions when the plugin is enabled.
    */
   public void onEnable()
   {
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
      for (O2SpellType spellType : O2Spells.getAllSpellTypes())
      {
         O2Spell spell;
         Class<?> spellClass = spellType.getClassName();

         try
         {
            spell = (O2Spell)spellClass.getConstructor(Ollivanders2.class).newInstance(p);
         }
         catch (Exception e)
         {
            common.printDebugMessage("Exception trying to add book text for " + spellType.toString(), e, null, true);
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
            common.printDebugMessage("Exception getting book text for " + spellType.toString(), e, null, true);
         }

         if (text == null)
         {
            common.printDebugMessage("No book text for " + spellType.toString(), null, null, false);
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
      for (O2PotionType potionType : O2Potions.getAllPotionTypes())
      {
         Class<?> potionClass = potionType.getClassName();
         O2Potion potion;

         try
         {
            potion = (O2Potion) potionClass.getConstructor(Ollivanders2.class).newInstance(p);
         }
         catch (Exception e)
         {
            common.printDebugMessage("Exception trying to add book text for " + potionType.toString(), e, null, true);
            continue;
         }

         String text = potion.getText();
         String flavorText = potion.getFlavorText();

         String name = Ollivanders2API.common.firstLetterCapitalize(Ollivanders2API.common.enumRecode(potionType.toString().toLowerCase()));

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
   @Nullable
   String getFlavorText(@NotNull String magic)
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
   @Nullable
   String getText(@NotNull String magic)
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
   @Nullable
   public String getName(@NotNull String magic)
   {
      String name = null;

      if (O2MagicTextMap.containsKey(magic))
         name = O2MagicTextMap.get(magic).getName();

      return name;
   }
}
