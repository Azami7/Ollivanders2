package net.pottercraft.ollivanders2.spell;

import java.util.UUID;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2Common;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * The super class for transfiguration projectile spells.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public abstract class Transfiguration extends O2Spell
{
   private final UUID nullUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
   private EntityType fromEType;
   private ItemStack fromStack;
   private UUID toID = nullUUID;
   private boolean hasTransfigured;

   /**
    * If this is not permanent, how long it should last. Default is 15 seconds.
    */
   int spellDuration = Ollivanders2Common.ticksPerSecond * 15;

   /**
    * Allows spell variants to change the duration of this spell.
    */
   double durationModifier = 1.0;

   /**
    * Max duration of this spell. Default is 10 minutes.
    */
   int maxDuration = Ollivanders2Common.ticksPerSecond * 600;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public Transfiguration ()
   {
      super();

      branch = O2MagicBranch.TRANSFIGURATION;
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public Transfiguration (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.TRANSFIGURATION;

      hasTransfigured = false;
   }

   /**
    * Transfigures entity into new EntityType. Do not use on players. Time length is usesModier() number of minutes plus 8 seconds.
    *
    * @param entity  - Entity to transfigure
    * @param toType  - Type to change entity into. If transfiguring into an item, have this be EntityType.DROPPED_ITEM
    * @param toStack - If transfiguring into an item, the itemstack to transfigure into
    * @return - The resulting entity.
    */
   public Entity transfigureEntity (Entity entity, EntityType toType, ItemStack toStack)
   {
      Location loc = location;
      if (entity != null)
      {
         fromEType = entity.getType();
         if (fromEType == EntityType.DROPPED_ITEM)
         {
            fromStack = ((Item) entity).getItemStack();
         }
         if (fromEType == EntityType.FALLING_BLOCK)
         {
            fromStack = new ItemStack(((FallingBlock) entity).getMaterial());
         }
         loc = entity.getLocation();
         for (O2Spell spell : p.getProjectiles())
         {
            if (spell instanceof Transfiguration)
            {
               Transfiguration trans = (Transfiguration) spell;
               if (trans.getToID() == entity.getUniqueId())
               {
                  trans.kill();
                  return transfigureEntity(trans.endTransfigure(), toType, toStack);
               }
            }
         }
         entity.remove();
      }
      hasTransfigured = true;
      Entity newEntity = null;
      if (toType == EntityType.DROPPED_ITEM)
      {
         newEntity = player.getWorld().dropItemNaturally(loc, toStack);
      }
      else if (toType == null)
      {
      }
      else
      {
         newEntity = player.getWorld().spawnEntity(loc, toType);
      }

      if (newEntity == null)
      {
         toID = nullUUID;
      }
      else
      {
         toID = newEntity.getUniqueId();
      }

      spellDuration = (int) (spellDuration * usesModifier * durationModifier);

      if (spellDuration > maxDuration)
      {
         spellDuration = maxDuration;
      }

      return newEntity;
   }

   /**
    * Ends the transfiguration. Drops items if there are any in it's inventory.
    *
    * @return The newly spawned Entity. Null if no entity was spawned from the ending of the transfiguration.
    */
   public Entity endTransfigure ()
   {
      kill();
      for (World w : p.getServer().getWorlds())
      {
         for (Entity e : w.getEntities())
         {
            if (e.getUniqueId() == toID)
            {
               e.remove();
               if (e instanceof InventoryHolder)
               {
                  for (ItemStack stack : ((InventoryHolder) e).getInventory().getContents())
                  {
                     if (stack != null)
                     {
                        e.getWorld().dropItemNaturally(e.getLocation(), stack);
                     }
                  }
               }
               if (fromEType == null)
               {
                  return null;
               }
               if (fromEType.equals(EntityType.DROPPED_ITEM))
               {
                  return e.getWorld().dropItemNaturally(e.getLocation(), fromStack);
               }
               if (fromEType.equals(EntityType.FALLING_BLOCK))
               {
                  return e.getWorld().spawnFallingBlock(e.getLocation(), fromStack.getData().clone());
               }
               else
               {
                  return e.getWorld().spawnEntity(e.getLocation(), fromEType);
               }
            }
         }
      }
      if (toID == nullUUID)
      {
         if (fromEType.equals(EntityType.DROPPED_ITEM))
         {
            return location.getWorld().dropItemNaturally(location, fromStack);
         }
         else
         {
            return location.getWorld().spawnEntity(location, fromEType);
         }
      }
      else
      {
         return null;
      }
   }

   /**
    * Has the transfiguration effect happened yet?
    *
    * @return If the transfiguration has taken place
    */
   public boolean hasTransfigured ()
   {
      return hasTransfigured;
   }

   /**
    * Gets the id of the transfigured entity
    *
    * @return the toID
    */
   public UUID getToID ()
   {
      return toID;
   }

   /**
    * Decrease the duration of this transfiguration, if it is not permanent, by the percent.
    *
    * @param percent the percent to reduce this transfiguration duration
    */
   public void reparifarge (int percent)
   {
      if (permanent)
      {
         return;
      }

      if (percent > 50)
      {
         percent = 50;
      }
      else if (percent < 1)
      {
         percent = 1;
      }

      double reduction = spellDuration * (percent / 100);

      spellDuration = spellDuration - (int) reduction;
   }
}