package net.pottercraft.Ollivanders2.Potion;

import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Manages all Ollivanders2 potions.
 *
 * @author Azami7
 */
public class Potions
{
   private final HashMap<String, String> O2PotionsMap;

   Ollivanders2 p;

   public Potions (Ollivanders2 plugin)
   {
      p = plugin;

      ArrayList<O2Potion> potions = new ArrayList<>();
      potions.add(new ANIMAGUS_POTION(p));
      potions.add(new ANTIDOTE_POTION(p));
      potions.add(new BARUFFIOS_BRAIN_ELIXIR(p));
      potions.add(new FORGETFULLNESS_POTION(p));
      potions.add(new HERBICIDE_POTION(p));
      potions.add(new MEMORY_POTION(p));
      potions.add(new REGENERATION_POTION(p));
      potions.add(new WIT_SHARPENING_POTION(p));
      potions.add(new WOLFSBANE_POTION(p));

      O2PotionsMap = new HashMap<>();
      loadPotionsClasses(potions);

      if (Ollivanders2.debug)
         p.getLogger().info("Loaded "  + O2PotionsMap.size() + " potions.");
   }

   /**
    * Load all the potions in to the potions map.
    *
    * @param potions
    */
   private void loadPotionsClasses (ArrayList<O2Potion> potions)
   {
      for (O2Potion potion : potions)
      {
         O2PotionsMap.put(potion.getName(), potion.getClass().getName());
      }
   }

   /**
    * Return the set of all the potions
    *
    * @return
    */
   public ArrayList<O2Potion> getPotions ()
   {
      ArrayList<O2Potion> potions = new ArrayList<>();

      for (Entry <String, String> e : O2PotionsMap.entrySet())
      {
         O2Potion potion = getPotionFromClassName(e.getValue());

         if (potion != null)
            potions.add(potion);
      }

      return potions;
   }

   /**
    * Brew a potion in a cauldron.
    *
    * @param cauldron the cauldron with the potion ingredients
    * @return the brewed potion if the recipe matches a known potion, null otherwise
    */
   public ItemStack brewPotion (Block cauldron)
   {
      // make sure the block passed to us is a cauldron
      if (cauldron.getType() != Material.CAULDRON)
         return null;

      // make sure cauldron has water in it
      if (cauldron.isEmpty())
         return null;

      // get ingredients from the cauldron
      Map<Material, Integer> ingredientsInCauldron = getIngredients(cauldron);

      // make sure cauldron has ingredients in it
      if (ingredientsInCauldron.size() < 1)
         return null;

      // match the ingredients in this potion to a known potion
      O2Potion potion = matchPotion(ingredientsInCauldron);
      if (potion == null)
      {
         // the ingredients did not match a known potion, make them a bad potion
         return O2Potion.brewBadPotion();
      }

      return potion.brew();
   }

   /**
    * Match the ingredients in this cauldron to a known potion.
    *
    * @param ingredientsInCauldron the ingredients in this cauldron
    * @return the matching potion if found, null otherwise
    */
   O2Potion matchPotion (Map<Material, Integer> ingredientsInCauldron)
   {
      // compare ingredients in the cauldron to the recipe for each potion
      for (Entry<String, String> e : O2PotionsMap.entrySet())
      {
         O2Potion potion = getPotionFromClassName(e.getValue());

         if (potion != null && potion.checkRecipe(ingredientsInCauldron))
            return potion;
      }

      return null;
   }

   /**
    * Creates a map of all the ingredients in this cauldron.
    *
    * @param cauldron
    * @return a Map of the ingredients and count of each ingredient
    */
   Map<Material, Integer> getIngredients (Block cauldron)
   {
      Map<Material, Integer> ingredientsInCauldron = new HashMap<>();
      Location location = cauldron.getLocation();

      for (Entity e : cauldron.getWorld().getNearbyEntities(location, 1, 1, 1))
      {
         if (e instanceof Item)
         {
            Material material = ((Item) e).getItemStack().getType();
            Integer count = ((Item) e).getItemStack().getAmount();

            ingredientsInCauldron.put(material, count);
         }
      }

      return ingredientsInCauldron;
   }

   /**
    * Find a potion by name.
    *
    * @param name the name to search for
    * @return the potion, if found, null otherwise
    */
   public O2Potion findPotionByName (String name)
   {
      if (O2PotionsMap.containsKey(name))
      {
         return getPotionFromClassName(O2PotionsMap.get(name));
      }
      else
      {
         return null;
      }
   }

   /**
    * Get an O2Potion from ItemMeta
    *
    * @param meta
    * @return the O2Potion, if one was found, null otherwise
    */
   public O2Potion findPotionByItemMeta (ItemMeta meta)
   {
      if (meta.hasLore())
      {
         for (String lore : meta.getLore())
         {
            if (O2PotionsMap.containsKey(lore))
            {
               return getPotionFromClassName(O2PotionsMap.get(lore));
            }
         }
      }

      return null;
   }

   /**
    * Get an O2Potions object from a class name.
    *
    * @param name
    * @return the O2Potions object, if it could be created, or null otherwise
    */
   private O2Potion getPotionFromClassName (String name)
   {
      O2Potion potion = null;

      try
      {
         potion = (O2Potion)Class.forName(name).getConstructor(Ollivanders2.class).newInstance(p);
      }
      catch (Exception exception)
      {
         p.getLogger().info("Exception trying to create new instance of " + name);
         if (Ollivanders2.debug)
            exception.printStackTrace();

         return null;
      }

      return potion;
   }
}
