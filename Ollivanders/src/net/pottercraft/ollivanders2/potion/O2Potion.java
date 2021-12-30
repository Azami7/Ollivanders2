package net.pottercraft.ollivanders2.potion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;

import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.O2MagicBranch;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ollivander2 magical potion.
 *
 * O2Potions can have either or both of the following types of effects:
 *
 * PotionEffect - this is a standard Minecraft potion effect such as Night Vision and is set in the item metadata in the
 * brew().
 * Ollivanders Effect - effects related to the mechanics of the Ollivanders plugin. These are applied on the potion
 * drink action in OllivandersListener in the onPlayerDrink().
 *
 * @author Azami7
 * @since 2.2.7
 */
public abstract class O2Potion
{
   Ollivanders2 p;
   Ollivanders2Common common;

   /**
    * The type this potion is.
    */
   protected O2PotionType potionType;

   /**
    * The ingredients list for this potion.
    */
   protected Map<O2ItemType, Integer> ingredients = new HashMap<>();

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

   /**
    * The PotionEffect for this potion
    */
   PotionEffect minecraftPotionEffect = null;

   /**
    * The type of potion this is
    */
   Material potionMaterialType = Material.POTION;

   /**
    * The duration for this potion
    */
   protected int duration = 3600;

   /**
    * Constructor
    *
    * @param plugin a callback to the plugin
    */
   public O2Potion(@NotNull Ollivanders2 plugin)
   {
      p = plugin;
      potionType = O2PotionType.BABBLING_BEVERAGE;
      common = new Ollivanders2Common(p);
   }

   /**
    * Get the ingredients text for this potion
    *
    * @return the recipe text for this ingredient
    */
   @NotNull
   protected String getIngredientsText()
   {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("\n\nIngredients:");

      for (Entry<O2ItemType, Integer> e : ingredients.entrySet())
      {
         O2ItemType ingredientType = e.getKey();
         String name = Ollivanders2API.getItems().getItemDisplayNameByType(ingredientType);

         stringBuilder.append("\n").append(e.getValue().toString()).append(" ").append(name);
      }

      return stringBuilder.toString();
   }

   /**
    * Get the type of this potion.
    *
    * @return the type of potion
    */
   @NotNull
   public O2PotionType getPotionType()
   {
      return potionType;
   }

   /**
    * Get the name of this potion
    *
    * @return the name of the potion
    */
   @NotNull
   public String getName()
   {
      return potionType.getPotionName();
   }

   /**
    * Get the ingredients list for this potion
    *
    * @return a Map of the ingredients for this potion
    */
   @NotNull
   Map<O2ItemType, Integer> getIngredients()
   {
      return ingredients;
   }

   /**
    * Get the description text for this potion.  This can be used to write books, for lessons, or other in-game messages.
    * Description text is required for adding a spell to an Ollivanders2 book.
    *
    * @return the description text for this potion
    */
   @NotNull
   public String getText ()
   {
      return text + getIngredientsText();
   }

   /**
    * Get the flavor text for this potion.  This can be used to make books, lessons, and other descriptions of spells more interesting.
    * Flavor text is optional.
    *
    * @return the flavor text for this potion.
    */
   @Nullable
   public String getFlavorText()
   {
      if (flavorText.size() < 1)
      {
         return null;
      }
      else
      {
         int index = Math.abs(Ollivanders2Common.random.nextInt() % flavorText.size());
         return flavorText.get(index);
      }
   }

   /**
    * Get the branch of magic for this potion
    *
    * @return the branch of magic for this potion
    */
   @NotNull
   public O2MagicBranch getMagicBranch()
   {
      return O2MagicBranch.POTIONS;
   }

   /**
    * Get get the level of magic for this potion
    *
    * @return the level of magic for this potion
    */
   @NotNull
   public Ollivanders2Common.MagicLevel getLevel()
   {
      return potionType.getLevel();
   }

   /**
    * Check the recipe for this potion against a set of ingredients.
    *
    * @param cauldronIngredients the ingredients found in the cauldron
    * @return true if the ingredient list matches this potion recipe exactly, false otherwise
    */
   public boolean checkRecipe(@NotNull Map<O2ItemType, Integer> cauldronIngredients)
   {
      common.printDebugMessage("Checking " + potionType.getPotionName() + " recipe", null, null, false);

      // are there the right number of ingredients?
      if (ingredients.size() != cauldronIngredients.size())
      {
         common.printDebugMessage("   expected " + ingredients.size() + " ingredients, got " + cauldronIngredients.size(), null, null, false);
         return false;
      }

      for (Map.Entry<O2ItemType, Integer> e : ingredients.entrySet())
      {
         O2ItemType ingredientType = e.getKey();
         Integer count = e.getValue();

         // is this ingredient in the recipe?
         if (!cauldronIngredients.containsKey(ingredientType))
         {
            common.printDebugMessage("   recipe does not contain " + ingredientType.getName(), null, null, false);
            return false;
         }

         // is the amount of the ingredient correct?
         if (cauldronIngredients.get(ingredientType).intValue() != count.intValue())
         {
            common.printDebugMessage("   recipe needs " + count.intValue() + " " + ingredientType.getName() + ", got " + cauldronIngredients.get(ingredientType).intValue(), null, null, false);
            return false;
         }
      }

      common.printDebugMessage("   matches", null, null, false);
      return true;
   }

   /**
    * Brew this potion.
    *
    * @param brewer the player brewing the potion
    * @param checkCanBrew should we enforce checking if the brewer can brew or not
    * @return an ItemStack with a single bottle of this potion
    */
   @NotNull
   public ItemStack brew(@NotNull Player brewer, boolean checkCanBrew)
   {
      if (checkCanBrew && !canBrew(brewer))
      {
         brewer.sendMessage(Ollivanders2.chatColor + "You feel uncertain about how to make this potion.");
         return brewBadPotion();
      }

      ItemStack potion = new ItemStack(potionMaterialType);
      PotionMeta meta = (PotionMeta) potion.getItemMeta();
      if (meta == null)
      {
         common.printDebugMessage("O2Potion.brew: item meta is null", null, null, true);
         return brewBadPotion();
      }

      meta.setDisplayName(potionType.getPotionName());
      PersistentDataContainer container = meta.getPersistentDataContainer();
      container.set(O2Potions.potionTypeKey, PersistentDataType.STRING, potionType.toString());

      meta.setColor(potionColor);
      if (minecraftPotionEffect != null)
         meta.addCustomEffect(minecraftPotionEffect, true);

      meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

      potion.setItemMeta(meta);

      return potion;
   }

   /**
    * Determine if a player can successfully brew this potion. This takes in to consideration whether book learning
    * is enabled, the player's experience with the potion, and the difficulty of the potion.
    *
    * @param brewer the player brewing the potion
    * @return true if the player will be successful, false otherwise.
    */
   private boolean canBrew(@NotNull Player brewer)
   {
      // When maxSpellLevel is on, potions are always successful
      if (Ollivanders2.maxSpellLevel)
         return true;

      boolean canBrew;

      O2Player o2p = Ollivanders2API.getPlayers().getPlayer(brewer.getUniqueId());
      if (o2p == null)
         return false;

      int potionCount = o2p.getPotionCount(potionType);

      // do not allow them to brew if book learning is on and they do not know this potion
      if (Ollivanders2.bookLearning && (potionCount < 1))
         canBrew = false;
      else
      {
         int successRate = 0;

         // success rate = ((potion count * 10) / (potion success modifier) - potions success modifier
         //
         // Examples:
         // BEGINNER potion has a success modifier of 1, player has potion count of 10, player is a 7th year
         // = 99% success rate
         //
         // EXPERT potion has a success modifier of 4, player has a potion count of 10, player is a 5th year
         // = 21% success rate
         //
         // OWL potion has a success modifier of 2, player has a potion count of 2, player is a 1st year
         // = 8% success rate
         //
         successRate = ((potionCount * 10) / potionType.getLevel().getSuccessModifier()) - potionType.getLevel().getSuccessModifier();

         int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 100) + 1;

         canBrew = (successRate > rand);
      }

      return canBrew;
   }

   /**
    * Brew a random bad potion.
    *
    * @return a random bad potion
    */
   @NotNull
   public static ItemStack brewBadPotion()
   {
      ItemStack potion = new ItemStack(Material.POTION);
      PotionMeta meta = (PotionMeta) potion.getItemMeta();

      if (meta == null)
         return potion;

      int rand = Math.abs(Ollivanders2Common.random.nextInt() % 10);
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

      rand = Math.abs(Ollivanders2Common.random.nextInt() % 10);

      int duration = 1200;
      int amplifier = 1;

      if (rand >= 5)
      {
         PotionEffect e;

         if (rand == 9)
         {
            e = new PotionEffect(PotionEffectType.SLOW, duration, amplifier);
         }
         else if (rand == 8)
         {
            e = new PotionEffect(PotionEffectType.BLINDNESS, duration, amplifier);
         }
         else if (rand == 7)
         {
            e = new PotionEffect(PotionEffectType.HUNGER, duration, amplifier);
         }
         else if (rand == 6)
         {
            e = new PotionEffect(PotionEffectType.CONFUSION, duration, amplifier);
         }
         else
         {
            e = new PotionEffect(PotionEffectType.WEAKNESS, duration, amplifier);
         }
         meta.addCustomEffect(e, true);
      }
      // rand < 5, no effect

      potion.setItemMeta(meta);
      meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

      return potion;
   }

   /**
    * Drink this potion and do effects
    *
    * @param player the player who drank the potion
    */
   public abstract void drink(@NotNull Player player);
}