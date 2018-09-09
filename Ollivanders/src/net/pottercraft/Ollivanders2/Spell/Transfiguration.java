package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Teachable;

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
public abstract class Transfiguration extends SpellProjectile implements Spell, Teachable
{
   private final UUID nullUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
   private EntityType fromEType;
   private ItemStack fromStack;
   private UUID toID = nullUUID;
   private boolean hasTransfigured;
   private int timeMultiplier = 1200;

   protected O2MagicBranch branch = O2MagicBranch.TRANSFIGURATION;;
   protected ArrayList<String> flavorText = new ArrayList<>();
   protected String text = "";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public Transfiguration (O2SpellType type)
   {
      super(type);
   }

   /**
    * Constructor for casting a transfiguration spell.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public Transfiguration (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);
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
         for (SpellProjectile spell : p.getProjectiles())
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
      this.lifeTicks = (int) (usesModifier * -1 * timeMultiplier);
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
                  return (Entity) e.getWorld().dropItemNaturally(e.getLocation(), fromStack);
               }
               if (fromEType.equals(EntityType.FALLING_BLOCK))
               {
                  //return (Entity) e.getWorld().spawnFallingBlock(e.getLocation(), fromStack.getType(), (byte) 0);
                  return (Entity) e.getWorld().spawnFallingBlock(e.getLocation(), fromStack.getData().clone());
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
            return (Entity) location.getWorld().dropItemNaturally(location, fromStack);
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
    * Moves the spell forward until it meets an entity and then performs a simple transfiguration on a non-player entity. This is the basic transfiguration code for most simple transfiguration spells.
    *
    * @param type  - The EntityType to transfigure the non-player entity into
    * @param stack - The itemstack to transfigure into, if the entitytype is Item
    */
   public void simpleTransfigure (EntityType type, ItemStack stack)
   {
      if (!hasTransfigured())
      {
         move();
         for (Entity e : getCloseEntities(1))
         {
            if (e.getType() != EntityType.PLAYER)
            {
               transfigureEntity(e, type, stack);
               return;
            }
         }
      }
      else
      {
         if (lifeTicks > 160)
         {
            endTransfigure();
         }
         else
         {
            lifeTicks++;
         }
      }
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
    * Sets the time multiplier for the Transfiguration.
    *
    * @param mult - Multiplier so that the duration is 8 plus (mult/20) seconds.
    */
   public void setTimeMultiplier (int mult)
   {
      timeMultiplier = mult;
   }

   @Override
   public String getText ()
   {
      return text;
   }

   @Override
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

   @Override
   public O2MagicBranch getMagicBranch ()
   {
      return branch;
   }
}