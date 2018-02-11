package net.pottercraft.Ollivanders2;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Cauldron;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Parses the ingredients in a cauldron when it is right clicked with a wand and determines what potion
 * should be made and how many, if any.
 *
 * @author lownes
 * @author Azami7
 */
public class PotionParser
{
   /**
    * Doubles spell experience gained when casting spells
    */
   public final static List<ItemStack> MEMORY_POTION = Arrays.asList(
         new ItemStack(Material.SUGAR_CANE, 3),
         new ItemStack(Material.GLOWSTONE_DUST, 2)
   );

   /**
    * All spells cast are twice as powerful.
    */
   public final static List<ItemStack> BARUFFIOS_BRAIN_ELIXIR = Arrays.asList(
         new ItemStack(Material.REDSTONE, 5),
         new ItemStack(Material.GOLD_NUGGET, 1)
   );

   /**
    * Relieve the symptoms of Lycanthropy
    */
   public final static List<ItemStack> WOLFSBANE_POTION = Arrays.asList(
         new ItemStack(Material.SPIDER_EYE, 2),
         new ItemStack(Material.ROTTEN_FLESH, 3)
   );

   /**
    * Heals a player.
    */
   public final static List<ItemStack> REGENERATION_POTION = Arrays.asList(
         new ItemStack(Material.BONE, 1),
         new ItemStack(Material.SPIDER_EYE, 1),
         new ItemStack(Material.SULPHUR, 1),
         new ItemStack(Material.ROTTEN_FLESH, 1),
         new ItemStack(Material.NETHER_STAR, 1)
   );

   /**
    *
    */
   public final static List<ItemStack> WIT_SHARPENING_POTION = Arrays.asList(
         new ItemStack(Material.FERMENTED_SPIDER_EYE, 1),
         new ItemStack(Material.BEETROOT, 2),
         new ItemStack(Material.GHAST_TEAR, 2)
   );

   /**
    * Consumed after successfully casting the Animagus incantation, this will turn a player in to an Animagus.
    */
   public final static List<ItemStack> ANIMAGUS_POTION = Arrays.asList(
         new ItemStack(Material.NETHER_WARTS, 2),
         new ItemStack(Material.CHORUS_FRUIT, 2),
         new ItemStack(Material.EYE_OF_ENDER, 1),
         new ItemStack(Material.SUGAR, 3)
   );

   Ollivanders2 p;
   public Map<String, List<ItemStack>> allPotions = new HashMap<>();

   public PotionParser (Ollivanders2 plugin)
   {
      p = plugin;

      allPotions.put("Memory Potion", MEMORY_POTION);
      allPotions.put("Baruffio's Brain Elixir", BARUFFIOS_BRAIN_ELIXIR);
      allPotions.put("Wolfsbane Potion", WOLFSBANE_POTION);
      allPotions.put("Regeneration Potion", REGENERATION_POTION);
      allPotions.put("Wit-Sharpening Potion", WIT_SHARPENING_POTION);
      allPotions.put("Animagus Potion", ANIMAGUS_POTION);

      if (Ollivanders2.debug)
         p.getLogger().info("Loaded "  + allPotions.size() + " potions.");
   }

   /**
    * Parses the ingredients in a cauldron. The parameter must be a cauldron block.
    *
    * @param cauldron - The block clicked. Must be a cauldron. Must have already been checked to see if it has a hot block under it.
    */
   public void parse (Block cauldron)
   {
      if (Ollivanders2.debug)
         p.getLogger().info("PotionParser:parse: enter");

      // make sure cauldron has water in it
      Cauldron cauldronData = (Cauldron)cauldron.getState().getData();
      if (cauldronData.isEmpty())
      {
         if (Ollivanders2.debug)
            p.getLogger().info("PotionParser:parse: cauldron is empty");

         return;
      }
      else
      {
         if (Ollivanders2.debug)
            p.getLogger().info("PotionParser:parse: cauldron is full");
      }

      Map<Material, Integer> ingredients = new HashMap<>();
      List<Item> ingredientItems = new ArrayList<>();
      for (Item item : cauldron.getWorld().getEntitiesByClass(Item.class))
      {
         if (item.getLocation().getBlock().equals(cauldron)
               || item.getLocation().getBlock().equals(cauldron.getRelative(BlockFace.UP)))
         {
            Material material = item.getItemStack().getType();
            int amount = item.getItemStack().getAmount();
            ingredientItems.add(item);
            if (Ollivanders2.debug)
               p.getLogger().info("PotionParser:parse: added " + material);

            if (ingredients.containsKey(material))
            {
               ingredients.put(material, ingredients.get(material) + amount);
            }
            else
            {
               ingredients.put(material, amount);
            }
         }
      }
      if (!ingredients.containsKey(Material.GLASS_BOTTLE))
      {
         if (Ollivanders2.debug)
            p.getLogger().info("PotionParser:parse: missing glass bottle");

         return;
      }

      /**
		 * check to find out which recipe it is
		 * make the potions according to how many ingredients there are and delete those item entities
		 */
      String recipe = recipeChecker(ingredients);
      if (recipe != null)
      {
         if (Ollivanders2.debug)
            p.getLogger().info("PotionParser:parse: found a matching recipe");
         //recipeCooker(recipe, water, ingredientItems, cauldron);
         recipeCooker(recipe, ingredientItems, cauldron);
      }
      else
      {
         if (Ollivanders2.debug)
            p.getLogger().info("PotionParser:parse: did not find a matching recipe");
      }
   }

   /**
    * Checks to find out which recipe is satisfied.
    *
    * @param ingredients - The list of materials on the ground
    * @return - The name of potion that matches the ingredients
    */
   private String recipeChecker (Map<Material, Integer> ingredients)
   {
      if (Ollivanders2.debug)
         p.getLogger().info("PotionParser:recipeChecker: enter");

      search:
      for (String name : allPotions.keySet())
      {
         List<ItemStack> recipe = allPotions.get(name);
         for (ItemStack ingredient : recipe)
         {
            if (ingredients.containsKey(ingredient.getType()))
            {
               if (ingredients.get(ingredient.getType()) < ingredient.getAmount())
               {
                  continue search;
               }
            }
            else
            {
               continue search;
            }
         }
         return name;
      }
      return null;
   }

   /**
    * Creates potion and consumes the ingredients.
    *
    * @param name        - name of the recipe that is to be created
    * @param ingredients - ingredients in the cauldron
    */
   //private static void recipeCooker (String name, int water, List<Item> ingredients, Block cauldron)
   private void recipeCooker (String name, List<Item> ingredients, Block cauldron)
   {
      List<ItemStack> recipe = allPotions.get(name);
      List<Integer> amounts = new ArrayList<>();

      //finding the least amount of ingredients
      for (Item item : ingredients)
      {
         if (item.getItemStack().getType() == Material.GLASS_BOTTLE)
         {
            amounts.add(item.getItemStack().getAmount());
            continue;
         }
         for (ItemStack stack : recipe)
         {
            if (item.getItemStack().getType() == stack.getType())
            {
               amounts.add(item.getItemStack().getAmount() / stack.getAmount());
               continue;
            }
         }
      }

      //dropping the potions
      int potions = Collections.min(amounts);
      Location dropLoc = cauldron.getLocation().add(0.5, 0.9, 0.5);
      ItemStack potion = new ItemStack(Material.POTION);
      ItemMeta meta = potion.getItemMeta();
      meta.setDisplayName(name);
      meta.setLore(Arrays.asList(name));
      potion.setItemMeta(meta);
      for (int x = 0; x < potions; x++)
      {
         cauldron.getWorld().dropItem(dropLoc, potion);
      }

      //subtracting the ingredients
      for (Item item : ingredients)
      {
         ItemStack itemStack = item.getItemStack();
         if (item.getItemStack().getType() == Material.GLASS_BOTTLE)
         {
            itemStack.setAmount(itemStack.getAmount() - potions);
            if (itemStack.getAmount() <= 0)
            {
               item.remove();
            }
            else
            {
               item.setItemStack(itemStack);
            }
            continue;
         }
         for (ItemStack stack : recipe)
         {
            if (item.getItemStack().getType() == stack.getType())
            {
               itemStack.setAmount(itemStack.getAmount() - (potions * stack.getAmount()));
               if (itemStack.getAmount() <= 0)
               {
                  item.remove();
               }
               else
               {
                  item.setItemStack(itemStack);
               }
               continue;
            }
         }
      }
   }
}