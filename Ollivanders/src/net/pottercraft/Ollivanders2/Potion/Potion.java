package net.pottercraft.Ollivanders2.Potion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import net.pottercraft.Ollivanders2.Teachable;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Ollivander2 magical potion.
 *
 * @author Azami7
 * @since 2.2.7
 */
public abstract class Potion implements Teachable
{
   /**
    * The ingredients list for this potion.
    */
   protected Map<Material, Integer> ingredients = new HashMap<>();

   /**
    * The name of this potion as it should appear on the bottle.
    */
   protected String name = "Water";

   /**
    * The description text for this spell in spell books.  Required or spell cannot be written in a book.
    */
   protected String text = "";

   /**
    * Flavor text for this spell in spellbooks, etc.  Optional.
    */
   protected ArrayList<String> flavorText = new ArrayList<>();

   /**
    * Color of this potion.
    */
   protected Color potionColor = Color.PURPLE;

   protected PotionEffect effect = null;

   /**
    * Constructor
    */
   public Potion () { }

   protected String getIngredientsText ()
   {
      String s = "\n\nIngredients:";

      for (Entry<Material, Integer> e : ingredients.entrySet())
      {
         Material m = e.getKey();
         String mString = Ollivanders2Common.firstLetterCapitalize(Ollivanders2Common.enumRecode(m.toString()));

         s = s + "\n" + e.getValue().toString() + " " + mString;
      }

      return s;
   }

   /**
    * Get the name of this potion
    *
    * @return the name of the potion
    */
   public String getName ()
   {
      return name;
   }

   /**
    * Get the ingredients list for this potion
    *
    * @return a Map of the ingredients for this potion
    */
   Map<Material, Integer> getIngredients ()
   {
      return ingredients;
   }

   /**
    * Get the description text for this spell.  This can be used to write books, for lessons, or other in-game messages.
    * Description text is required for adding a spell to an Ollivanders2 book.
    *
    * @return the description text for this spell
    */
   public String getText ()
   {
      return text;
   }

   /**
    * Get the flavor text for this spell.  This can be used to make books, lessons, and other descriptions of spells more interesting.
    * Flavor text is optional.
    *
    * @return the flavor text for this spell.
    */
   public String getFlavorText()
   {
      if (flavorText.size() < 1)
      {
         return null;
      }
      else
      {
         int index = Math.abs(Ollivanders2.random.nextInt() % flavorText.size());
         return flavorText.get(index);
      }
   }

   public O2MagicBranch getMagicBranch ()
   {
      return O2MagicBranch.POTIONS;
   }

   /**
    * Check the recipe for this potion against a set of ingredients.
    *
    * @param cauldronIngredients
    * @return true if the ingredient list matches this potion recipe exactly, false otherwise
    */
   public boolean checkRecipe (Map<Material, Integer> cauldronIngredients)
   {
      // are there the right number of ingredients?
      if (ingredients.size() != cauldronIngredients.size())
      {
         return false;
      }

      for (Map.Entry<Material, Integer> e : ingredients.entrySet())
      {
         Material material = e.getKey();
         Integer count = e.getValue();

         // is this ingredient in the recipe?
         if (!cauldronIngredients.containsKey(material))
         {
            return false;
         }

         // is the amount of the ingredient correct?
         if (cauldronIngredients.get(material).intValue() != count.intValue())
         {
            return false;
         }
      }

      return true;
   }

   /**
    * Brew this potion.
    *
    * @return an ItemStack with a single bottle of this potion
    */
   public ItemStack brew ()
   {
      ItemStack potion = new ItemStack(Material.POTION);
      PotionMeta meta = (PotionMeta)potion.getItemMeta();

      meta.setDisplayName(name);
      meta.setLore(Arrays.asList(name));
      meta.setColor(potionColor);
      if (effect != null)
         meta.addCustomEffect(effect, true);

      potion.setItemMeta(meta);

      return potion;
   }

   /**
    * Brew a random bad potion.
    *
    * @return a random bad potion
    */
   public static ItemStack brewBadPotion ()
   {
      ItemStack potion = new ItemStack(Material.POTION);
      PotionMeta meta = (PotionMeta)potion.getItemMeta();

      int rand = Math.abs(Ollivanders2.random.nextInt() % 10);
      String name = "Watery Potion";
      Color color = Color.BLUE;

      if (rand == 9)
      {
         name = "Slimy Potion";
         color = Color.GREEN;
      }
      else if (rand == 8)
      {
         name = "Lumpy Potion";
         color = Color.GRAY;
      }
      else if (rand == 7)
      {
         name = "Cloudy Potion";
         color = Color.WHITE;
      }
      else if (rand == 6)
      {
         name = "Smelly Potion";
         color = Color.ORANGE;
      }
      else if (rand == 5)
      {
         name = "Sticky Potion";
         color = Color.OLIVE;
      }
      // else rand < 5, use "Watery Potion" and Color.BLUE

      meta.setDisplayName(name);
      meta.setLore(Arrays.asList(name));
      meta.setColor(color);

      rand = Math.abs(Ollivanders2.random.nextInt() % 10);

      int duration = 1200;
      int amplifier = 1;

      if (rand >= 5)
      {
         PotionEffect effect;

         if (rand == 9)
         {
            effect = new PotionEffect(PotionEffectType.SLOW, duration, amplifier);
         }
         else if (rand == 8)
         {
            effect = new PotionEffect(PotionEffectType.BLINDNESS, duration, amplifier);
         }
         else if (rand == 7)
         {
            effect = new PotionEffect(PotionEffectType.HUNGER, duration, amplifier);
         }
         else if (rand == 6)
         {
            effect = new PotionEffect(PotionEffectType.CONFUSION, duration, amplifier);
         }
         else
         {
            effect = new PotionEffect(PotionEffectType.WEAKNESS, duration, amplifier);
         }
         meta.addCustomEffect(effect, true);
      }
      // rand < 5, no effect

      potion.setItemMeta(meta);

      return potion;
   }
}
