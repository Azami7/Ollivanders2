package net.pottercraft.Ollivanders2.Potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import net.pottercraft.Ollivanders2.Item.O2ItemType;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Manages all Ollivanders2 potions.
 *
 * @author Azami7
 */
public class O2Potions
{
   private Ollivanders2 p;

   private HashMap <String, O2PotionType> O2PotionMap = new HashMap<>();

   public static final List<O2ItemType> ingredients = new ArrayList<O2ItemType>()
   {{
      add(O2ItemType.ACONITE);
      add(O2ItemType.ARMADILLO_BILE);
      add(O2ItemType.BEZOAR);
      add(O2ItemType.BILLYWIG_STING_SLIME);
      add(O2ItemType.BLOOD);
      add(O2ItemType.BONE);
      add(O2ItemType.BOOM_BERRY_JUICE);
      add(O2ItemType.BOOMSLANG_SKIN);
      add(O2ItemType.CHIZPURFLE_FANGS);
      add(O2ItemType.CRUSHED_FIRE_SEEDS);
      add(O2ItemType.DEATHS_HEAD_MOTH_CHRYSALIS);
      add(O2ItemType.DEW_DROP);
      add(O2ItemType.DITTANY);
      add(O2ItemType.DRAGON_BLOOD);
      add(O2ItemType.DRAGONFLY_THORAXES);
      add(O2ItemType.DRIED_NETTLES);
      add(O2ItemType.FAIRY_WING);
      add(O2ItemType.FLOBBERWORM_MUCUS);
      add(O2ItemType.FLUXWEED);
      add(O2ItemType.FULGURITE);
      add(O2ItemType.GALANTHUS_NIVALIS);
      add(O2ItemType.GINGER_ROOT);
      add(O2ItemType.GROUND_DRAGON_HORN);
      add(O2ItemType.GROUND_PORCUPINE_QUILLS);
      add(O2ItemType.GROUND_SCARAB_BEETLE);
      add(O2ItemType.GROUND_SNAKE_FANGS);
      add(O2ItemType.HONEYWATER);
      add(O2ItemType.HORKLUMP_JUICE);
      add(O2ItemType.HORNED_SLUG_MUCUS);
      add(O2ItemType.HORN_OF_BICORN);
      add(O2ItemType.INFUSION_OF_WORMWOOD);
      add(O2ItemType.JOBBERKNOLL_FEATHER);
      add(O2ItemType.KNOTGRASS);
      add(O2ItemType.LACEWING_FLIES);
      add(O2ItemType.LAVENDER_SPRIG);
      add(O2ItemType.LEECHES);
      add(O2ItemType.LETHE_RIVER_WATER);
      add(O2ItemType.LIONFISH_SPINES);
      add(O2ItemType.MANDRAKE_LEAF);
      add(O2ItemType.MERCURY);
      add(O2ItemType.MINT_SPRIG);
      add(O2ItemType.MISTLETOE_BERRIES);
      add(O2ItemType.MOONDEW_DROP);
      add(O2ItemType.POISONOUS_POTATO);
      add(O2ItemType.POWDERED_ASHPODEL_ROOT);
      add(O2ItemType.POWDERED_SAGE);
      add(O2ItemType.ROTTEN_FLESH);
      add(O2ItemType.RUNESPOOR_EGG);
      add(O2ItemType.SALAMANDER_BLOOD);
      add(O2ItemType.SALAMANDER_FIRE);
      add(O2ItemType.SLOTH_BRAIN);
      add(O2ItemType.SLOTH_BRAIN_MUCUS);
      add(O2ItemType.SOPOPHORUS_BEAN_JUICE);
      add(O2ItemType.SPIDER_EYE);
      add(O2ItemType.STANDARD_POTION_INGREDIENT);
      add(O2ItemType.UNICORN_HAIR);
      add(O2ItemType.UNICORN_HORN);
      add(O2ItemType.VALERIAN_SPRIGS);
      add(O2ItemType.VALERIAN_ROOT);
      add(O2ItemType.WOLFSBANE);
   }};

   public O2Potions (Ollivanders2 plugin)
   {
      p = plugin;

      for (O2PotionType potionType : O2PotionType.values())
      {
         if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potionType))
            continue;

         O2PotionMap.put(potionType.getPotionName().toLowerCase(), potionType);
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
            if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potion.getPotionType()))
            {
               continue;
            }

            potions.add(potion);
         }
      }

      return potions;
   }

   /**
    * Get the names of all potions loaded in the game.
    *
    * @return the list of active potion names
    */
   public List<String> getAllPotionNames ()
   {
      ArrayList<String> potionNames = new ArrayList<>();

      for (O2PotionType potionType : O2PotionType.values())
      {
         potionNames.add(potionType.getPotionName());
      }

      return potionNames;
   }

   /**
    * Brew a potion in a cauldron.
    *
    * @param cauldron the cauldron with the potion ingredients
    * @param brewer the player brewing this potion
    * @return the brewed potion if the recipe matches a known potion, null otherwise
    */
   public ItemStack brewPotion (Block cauldron, Player brewer)
   {
      // make sure the block passed to us is a cauldron
      if (cauldron.getType() != Material.CAULDRON)
         return null;

      // make sure cauldron has water in it
      if (cauldron.isEmpty())
         return null;

      // get ingredients from the cauldron
      Map<O2ItemType, Integer> ingredientsInCauldron = getIngredientsInCauldron(cauldron);

      // make sure cauldron has ingredients in it
      if (ingredientsInCauldron.size() < 1)
         return null;

      // match the ingredients in this potion to a known potion
      O2Potion potion = matchPotion(ingredientsInCauldron);
      if (potion == null || (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.libDisguisesPotions.contains(potion.getPotionType())))
      {
         // make them a bad potion
         return O2Potion.brewBadPotion();
      }

      return potion.brew(brewer, true);
   }

   /**
    * Match the ingredients in this cauldron to a known potion.
    *
    * @param ingredientsInCauldron the ingredients in this cauldron
    * @return the matching potion if found, null otherwise
    */
   private O2Potion matchPotion (Map<O2ItemType, Integer> ingredientsInCauldron)
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
   private Map<O2ItemType, Integer> getIngredientsInCauldron (Block cauldron)
   {
      Map<O2ItemType, Integer> ingredientsInCauldron = new HashMap<>();
      Location location = cauldron.getLocation();

      for (Entity e : cauldron.getWorld().getNearbyEntities(location, 1, 1, 1))
      {
         if (e instanceof Item)
         {
            Material material = ((Item) e).getItemStack().getType();
            String lore = ((Item)e).getItemStack().getItemMeta().getLore().get(0);

            O2ItemType ingredientType = Ollivanders2API.getItems().getTypeByDisplayName(lore);

            if (ingredientType == null || material != Ollivanders2API.getItems().getItemMaterialByType(ingredientType))
               continue;

            Integer count = ((Item) e).getItemStack().getAmount();

            if (Ollivanders2.debug)
            {
               p.getLogger().info("Found " + count + " of ingredient " + ingredientType.toString());
            }

            ingredientsInCauldron.put(ingredientType, count);
         }
      }

      return ingredientsInCauldron;
   }

   /**
    * Get an O2Potion from ItemMeta
    *
    * @param meta the metadata for this item
    * @return the O2Potion, if one was found, null otherwise
    */
   public O2Potion findPotionByItemMeta (ItemMeta meta)
   {
      if (meta.hasLore())
      {
         for (String lore : meta.getLore())
         {
            if (O2PotionMap.containsKey(lore.toLowerCase()))
            {
               return getPotionFromType(O2PotionMap.get(lore.toLowerCase()));
            }
         }
      }

      return null;
   }

   /**
    * Get an O2Potions object from its type.
    *
    * @param potionType the type of potion to get
    * @return the O2Potion object, if it could be created, or null otherwise
    */
   public O2Potion getPotionFromType (O2PotionType potionType)
   {
      O2Potion potion;

      Class<?> potionClass = potionType.getClassName();

      try
      {
         potion = (O2Potion)potionClass.getConstructor(Ollivanders2.class).newInstance(p);
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
    * Get a potion type by name.
    *
    * @param name the name of the potion
    * @return the type if found, null otherwise
    */
   public O2PotionType getPotionTypeByName (String name)
   {
      if (O2PotionMap.containsKey(name.toLowerCase()))
         return O2PotionMap.get(name.toLowerCase());
      else
         return null;
   }

   /**
    * Get a list of the names of every potion ingredient.
    *
    * @return a list of all potions ingredients
    */
   public static List<String> getAllIngredientNames ()
   {
      ArrayList<String> ingredientList = new ArrayList<>();

      for (O2ItemType i : ingredients)
      {
         ingredientList.add(Ollivanders2API.getItems().getItemDisplayNameByType(i));
      }

      return ingredientList;
   }
}
