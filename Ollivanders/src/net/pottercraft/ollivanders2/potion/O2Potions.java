package net.pottercraft.ollivanders2.potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2Common;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages all Ollivanders2 potions.
 *
 * @author Azami7
 */
public class O2Potions
{
   final private Ollivanders2 p;

   final private Map<String, O2PotionType> O2PotionMap = new HashMap<>();

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

   /**
    * Constructor
    *
    * @param plugin a reference to the plugin
    */
   public O2Potions(@NotNull Ollivanders2 plugin)
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
   @NotNull
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
   @NotNull
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
    * @param brewer   the player brewing this potion
    * @return the brewed potion if the recipe matches a known potion, null otherwise
    */
   @Nullable
   public ItemStack brewPotion(@NotNull Block cauldron, @NotNull Player brewer)
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
   @Nullable
   private O2Potion matchPotion(@NotNull Map<O2ItemType, Integer> ingredientsInCauldron)
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
   @NotNull
   private Map<O2ItemType, Integer> getIngredientsInCauldron(@NotNull Block cauldron)
   {
      Map<O2ItemType, Integer> ingredientsInCauldron = new HashMap<>();
      Location location = cauldron.getLocation();

      for (Entity e : cauldron.getWorld().getNearbyEntities(location, 1, 1, 1))
      {
         if (e instanceof Item)
         {
            Material material = ((Item) e).getItemStack().getType();
            ItemMeta meta = ((Item) e).getItemStack().getItemMeta();
            if (meta == null)
               continue;

            List<String> itemLore = meta.getLore();
            if (itemLore == null)
               continue;

            String lore = itemLore.get(0);
            if (lore == null)
               continue;

            // For ingredients, lore and name are the same. We use lore instead of item name because this cannot be set by using an anvil - so players cannot "make"
            // ingredients, they can only get real ones from the plugin.
            O2ItemType ingredientType = Ollivanders2API.getItems(p).getTypeByDisplayName(lore);

            if (ingredientType == null || material != Ollivanders2API.getItems(p).getItemMaterialByType(ingredientType))
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
   @Nullable
   public O2Potion findPotionByItemMeta(@NotNull ItemMeta meta)
   {
      if (meta.hasLore())
      {
         List<String> lore = meta.getLore();

         // we search on lore rather than item name so that players cannot use anvils to create potions
         if (lore == null)
            return null;

         for (String l : lore)
         {
            if (O2PotionMap.containsKey(l.toLowerCase()))
            {
               return getPotionFromType(O2PotionMap.get(l.toLowerCase()));
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
   @Nullable
   public O2Potion getPotionFromType(@NotNull O2PotionType potionType)
   {
      O2Potion potion;

      Class<?> potionClass = potionType.getClassName();

      try
      {
         potion = (O2Potion) potionClass.getConstructor(Ollivanders2.class).newInstance(p);
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
   @Nullable
   public O2PotionType getPotionTypeByName(@NotNull String name)
   {
      return O2PotionMap.getOrDefault(name.toLowerCase(), null);
   }

   /**
    * Get a list of the names of every potion ingredient.
    *
    * @param p a reference to the plugin
    * @return a list of all potions ingredients
    */
   @NotNull
   public static List<String> getAllIngredientNames (@NotNull Ollivanders2 p)
   {
      ArrayList<String> ingredientList = new ArrayList<>();

      for (O2ItemType i : ingredients)
      {
         ingredientList.add(Ollivanders2API.getItems(p).getItemDisplayNameByType(i));
      }

      return ingredientList;
   }

   /**
    * Verify this potion type is loaded. A potion may not be loaded if it depends on something such as LibsDisguises and that
    * dependency plugin does not exist.
    *
    * @param potionType the potion type to check
    * @return true if this potion type is loaded, false otherwise
    */
   public boolean isLoaded(@NotNull O2PotionType potionType)
   {
      return O2PotionMap.containsValue(potionType);
   }
}
