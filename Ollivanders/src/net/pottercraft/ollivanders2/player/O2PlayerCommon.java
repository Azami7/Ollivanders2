package net.pottercraft.ollivanders2.player;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class O2PlayerCommon
{
   private Ollivanders2 p;

   public O2PlayerCommon (Ollivanders2 plugin)
   {
      p = plugin;
   }

   static String wandLoreConjunction = " and ";

   /**
    * Get all possible animagus shapes
    *
    * @return a list of all possible EntityTypes for animagus form
    */
   static ArrayList<EntityType> getAnimagusShapes ()
   {
      ArrayList<EntityType> animagusShapes = new ArrayList<>();

      animagusShapes.add(EntityType.OCELOT);
      animagusShapes.add(EntityType.WOLF);
      animagusShapes.add(EntityType.COW);
      animagusShapes.add(EntityType.PIG);
      animagusShapes.add(EntityType.HORSE);
      animagusShapes.add(EntityType.SHEEP);
      animagusShapes.add(EntityType.RABBIT);
      animagusShapes.add(EntityType.MULE);
      animagusShapes.add(EntityType.DONKEY);
      animagusShapes.add(EntityType.POLAR_BEAR);
      animagusShapes.add(EntityType.LLAMA);

      if (Ollivanders2.useHostileMobAnimagi)
      {
         animagusShapes.add(EntityType.SPIDER);
         animagusShapes.add(EntityType.SLIME);
         animagusShapes.add(EntityType.CAVE_SPIDER);
         animagusShapes.add(EntityType.CREEPER);
         animagusShapes.add(EntityType.EVOKER);
         animagusShapes.add(EntityType.HUSK);
         animagusShapes.add(EntityType.SILVERFISH);
         animagusShapes.add(EntityType.WITCH);
         animagusShapes.add(EntityType.VINDICATOR);
         animagusShapes.add(EntityType.SHULKER);
      }

      return animagusShapes;
   }


   /**
    * Take an integer and get the corresponding year
    * @param year The year; must be between 1 and 7
    * @return The corresponding year or null if invalid input
    */
   public static Year intToYear(int year) {
      switch (year) {
         case 1:
            return Year.YEAR_1;
         case 2:
            return Year.YEAR_2;
         case 3:
            return Year.YEAR_3;
         case 4:
            return Year.YEAR_4;
         case 5:
            return Year.YEAR_5;
         case 6:
            return Year.YEAR_6;
         case 7:
            return Year.YEAR_7;
         default:
            return null;
      }
   }

   /**
    * Does the player hold a wand item in their primary hand?
    *
    * @param player player to check.
    * @return True if the player holds a wand. False if not or if player is null.
    */
   public boolean holdsWand (Player player)
   {
      return holdsWand(player, EquipmentSlot.HAND);
   }

   /**
    * Does the player hold a wand item in their hand?
    *
    * @since 2.2.7
    * @param player player to check.
    * @param hand the equipment slot to check for this player
    * @return True if the player holds a wand. False if not or if player is null.
    */
   public boolean holdsWand (Player player, EquipmentSlot hand)
   {
      if (player == null || hand == null)
         return false;

      ItemStack held;
      if (hand == EquipmentSlot.HAND)
      {
         held = player.getInventory().getItemInMainHand();
      }
      else if (hand == EquipmentSlot.OFF_HAND)
      {
         held = player.getInventory().getItemInOffHand();
      }
      else
         return false;

      if (held == null)
         return false;

      return Ollivanders2API.common.isWand(held);
   }

   /**
    * Is this ItemStack the player's destined wand?
    *
    * @param player player to check the stack against.
    * @param stack ItemStack to be checked
    * @return true if yes, false if no
    */
   public boolean destinedWand (Player player, ItemStack stack)
   {
      if (Ollivanders2API.common.isWand(stack))
      {
         O2Player o2Player = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());

         if (o2Player == null)
         {
            return false;
         }

         return o2Player.isDestinedWand(stack);
      }
      else
      {
         return false;
      }
   }

   /**
    * Checks what kind of wand a player holds in their primary hand. Returns a value based on the
    * wand and it's relation to the player.
    *
    * @assumes player not null, player holding a wand
    * @param player player being checked. The player must be holding a wand.
    * @return 2 - the wand is not player's type AND/OR is not allied to player.<p>
    * 1 - the wand is player's type and is allied to player OR the wand is the elder wand and is not allied to player.<p>
    * 0.5 - the wand is the elder wand and it is allied to player.
    */
   public double wandCheck (Player player)
   {
      return wandCheck(player, EquipmentSlot.HAND);
   }

   /**
    * Checks what kind of wand a player holds. Returns a value based on the wand and it's relation to the player.
    *
    * @since 2.2.7
    * @assumes player not null, player is holding a wand in the equipment slot passed in
    * @param player player being checked. The player must be holding a wand.
    * @param hand the hand that is holding the wand to check.
    * @return 2 - The wand is not player's type AND/OR is not allied to player.<p>
    * 1 - The wand is player's type and is allied to player OR the wand is the elder wand and is not allied to player.<p>
    * 0.5 - The wand is the elder wand and it is allied to player.
    */
   public double wandCheck (Player player, EquipmentSlot hand)
   {
      ItemStack item;

      if (hand == EquipmentSlot.HAND)
      {
         item = player.getInventory().getItemInMainHand();
      }
      else
      {
         item = player.getInventory().getItemInOffHand();
      }

      return doWandCheck(player, item);
   }

   /**
    * Checks what kind of wand a player holds. Returns a value based on the wand and it's relation to the player.
    *
    * @since 2.2.7
    * @param player - the player to check
    * @param item - the
    * @return 2 - The wand is not player's type AND/OR is not allied to player.<p>
    * 1 - The wand is player's type and is allied to player OR the wand is the elder wand and is not allied to player.<p>
    * 0.5 - The wand is the elder wand and it is allied to player.
    */
   private double doWandCheck (Player player, ItemStack item)
   {
      List<String> lore = item.getItemMeta().getLore();
      if (lore.get(0).equals("Blaze and Ender Pearl")) // elder wand
      {
         if (lore.size() == 2 && lore.get(1).equals(player.getUniqueId().toString()))
         {
            // wand is Elder Wand and allied to player
            return 0.5;
         }
      }
      else // not the elder wand
      {
         if (!destinedWand(player, player.getInventory().getItemInMainHand()))
         {
            // not the player's destined wand
            return 2;
         }
      }

      return 1;
   }
}