package net.pottercraft.Ollivanders2.Potion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

/**
 * Manages all Ollivanders2 potions.
 *
 * @author Azami7
 */
public class O2Potions
{
   private Ollivanders2 p;

   private HashMap <String, O2PotionType> O2PotionMap = new HashMap<>();

   public O2Potions (Ollivanders2 plugin)
   {
      p = plugin;

      for (O2PotionType potionType : O2PotionType.values())
      {
         O2Potion potion = getPotionFromType(potionType);

         if (potion != null)
         {
            O2PotionMap.put(potion.getName(), potionType);
         }
      }
   }

   /**
    * Return the set of all the potions
    *
    * @return a Collection of 1 of each O2Potion
    */
   public Collection<O2Potion> getAllPotions ()
   {
      Collection<O2Potion> potions = new ArrayList<>();

      for (O2PotionType potionType : O2PotionType.values())
      {
         O2Potion potion = getPotionFromType(potionType);

         if (potion != null)
         {
            if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potion.getType()))
            {
               continue;
            }

            potions.add(potion);
         }
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
      Map<IngredientType, Integer> ingredientsInCauldron = getIngredientsInCauldron(cauldron);

      // make sure cauldron has ingredients in it
      if (ingredientsInCauldron.size() < 1)
         return null;

      // match the ingredients in this potion to a known potion
      O2Potion potion = matchPotion(ingredientsInCauldron);
      if (potion == null || (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potion.getType())))
      {
         // make them a bad potion
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
   O2Potion matchPotion (Map<IngredientType, Integer> ingredientsInCauldron)
   {
      // compare ingredients in the cauldron to the recipe for each potion
      for (O2PotionType potionType : O2PotionType.values())
      {
         O2Potion potion = getPotionFromType(potionType);

         if (potion != null && potion.checkRecipe(ingredientsInCauldron))
            return potion;
      }

      return null;
   }

   /**
    * Creates a map of all the ingredients in this cauldron.
    *
    * @param cauldron the brewing cauldron
    * @return a Map of the ingredients and count of each ingredient
    */
   Map<IngredientType, Integer> getIngredientsInCauldron (Block cauldron)
   {
      Map<IngredientType, Integer> ingredientsInCauldron = new HashMap<>();
      Location location = cauldron.getLocation();

      for (Entity e : cauldron.getWorld().getNearbyEntities(location, 1, 1, 1))
      {
         if (e instanceof Item)
         {
            Material material = ((Item) e).getItemStack().getType();
            String lore = ((Item)e).getItemStack().getItemMeta().getLore().get(0);

            IngredientType ingredientType = IngredientType.getIngredientType(lore);

            if (ingredientType == null || material != ingredientType.getMaterial())
               continue;

            Integer count = ((Item) e).getItemStack().getAmount();

            if (Ollivanders2.debug)
               p.getLogger().info("Found " + count + " of ingredient " + ingredientType.getName());

            ingredientsInCauldron.put(ingredientType, count);
         }
      }

      return ingredientsInCauldron;
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
            if (O2PotionMap.containsKey(lore))
            {
               return getPotionFromType(O2PotionMap.get(lore));
            }
         }
      }

      return null;
   }

   /**
    * Get an O2Potions object from its type.
    *
    * @param potionType the type of potion to get
    * @return the O2Potions object, if it could be created, or null otherwise
    */
   private O2Potion getPotionFromType (O2PotionType potionType)
   {
      O2Potion potion;

      Class potionClass = potionType.getClassName();

      try
      {
         potion = (O2Potion)potionClass.getConstructor(Ollivanders2.class, O2PotionType.class).newInstance(p, potionType);
      }
      catch (Exception exception)
      {
         p.getLogger().info("Exception trying to create new instance of " + potionType.toString());
         if (Ollivanders2.debug)
            exception.printStackTrace();

         return null;
      }

      return potion;
   }

   /**
    * Get a potion ingredient by name.
    *
    * @param name the name of the ingredient to get
    * @return the ingredient item or null if not found
    */
   public ItemStack getIngredientByName (String name)
   {
      for (IngredientType i : IngredientType.values())
      {
         String iName = i.getName();

         if (iName.toLowerCase().startsWith(name.toLowerCase()))
            return getIngredient(i);
         else
         {
            if (Ollivanders2.debug)
               p.getLogger().info("Did not find ingredient " + name);
         }
      }

      return null;
   }

   /**
    * Get an ingredient by type.
    *
    * @param ingredientType the type of the ingredient
    * @return the ingredient item
    */
   public ItemStack getIngredient (IngredientType ingredientType)
   {
      if (Ollivanders2.debug)
         p.getLogger().info("Getting ingredient " + ingredientType.getName());

      Material material = ingredientType.getMaterial();
      short variant = ingredientType.getVariant();
      String name = ingredientType.getName();

      ItemStack ingredient = new ItemStack(material, 1, variant);

      ItemMeta meta = ingredient.getItemMeta();
      meta.setLore(Arrays.asList(name));
      meta.setDisplayName(name);

      if (material == Material.POTION)
      {
         Ollivanders2Common common = new Ollivanders2Common(p);

         meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
         ((PotionMeta)meta).setColor(common.colorByNumber((int)variant));
      }

      ingredient.setItemMeta(meta);

      return ingredient;
   }
}
