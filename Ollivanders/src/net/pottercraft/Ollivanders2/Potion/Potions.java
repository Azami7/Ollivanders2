package net.pottercraft.Ollivanders2.Potion;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Manages all Ollivanders2 potions.
 *
 * @author Azami7
 */
public class Potions
{
   private final ArrayList<Potion> potions = new ArrayList();

   Ollivanders2 p;

   public Potions (Ollivanders2 plugin)
   {
      p = plugin;

      potions.add(new ANIMAGUS_POTION(p));
      potions.add(new ANTIDOTE_POTION(p));
      potions.add(new BARUFFIOS_BRAIN_ELIXIR(p));
      potions.add(new FORGETFULLNESS_POTION(p));
      potions.add(new MEMORY_POTION(p));
      potions.add(new REGENERATION_POTION(p));
      potions.add(new WIT_SHARPENING_POTION(p));
      potions.add(new WOLFSBANE_POTION(p));

      if (Ollivanders2.debug)
         p.getLogger().info("Loaded "  + potions.size() + " potions.");
   }

   public ArrayList<Potion> getPotions ()
   {
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
      Potion potion = matchPotion(ingredientsInCauldron);
      if (potion == null)
      {
         // the ingredients did not match a known potion, make them a bad potion
         return Potion.brewBadPotion();
      }

      return potion.brew();
   }

   /**
    * Match the ingredients in this cauldron to a known potion.
    *
    * @param ingredientsInCauldron the ingredients in this cauldron
    * @return the matching potion if found, null otherwise
    */
   Potion matchPotion (Map<Material, Integer> ingredientsInCauldron)
   {
      // compare ingredients in the cauldron to the recipe for each potion
      for (Potion potion : potions)
      {
         if (potion.checkRecipe(ingredientsInCauldron))
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
}
