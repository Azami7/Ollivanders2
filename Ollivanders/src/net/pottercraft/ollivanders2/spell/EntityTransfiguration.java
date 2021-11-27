package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The super class for transfiguration of all entities.
 *
 * @author Azami7
 * @since 2.2.6
 */
public abstract class EntityTransfiguration extends TransfigurationBase
{
   /**
    * The type of entity this will transfigure.
    */
   EntityType targetType = EntityType.SHEEP;

   /**
    * A blacklist of Entity types that will not be affected by this spell.  Only used if the whitelist is empty.
    */
   List<EntityType> entityBlacklist = new ArrayList<>();

   Entity originalEntity = null;
   Entity transfiguredEntity = null;

   /**
    * A whitelist of Entity types that will be affected by this spell.
    */
   List<EntityType> entityWhitelist = new ArrayList<>();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public EntityTransfiguration(Ollivanders2 plugin)
   {
      super(plugin);

      branch = O2MagicBranch.TRANSFIGURATION;
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public EntityTransfiguration(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.TRANSFIGURATION;

      entityBlacklist.add(EntityType.AREA_EFFECT_CLOUD);
      entityBlacklist.add(EntityType.EXPERIENCE_ORB);
      entityBlacklist.add(EntityType.FALLING_BLOCK);
      entityBlacklist.add(EntityType.EXPERIENCE_ORB);
      entityBlacklist.add(EntityType.THROWN_EXP_BOTTLE);
      entityBlacklist.add(EntityType.UNKNOWN);

      permanent = false;
   }

   /**
    * Look for an entity to transfigure at the current projectile location. Will not change the entity if it is on the entityBlacklist list or
    * if the target entity is already the transfigure type.
    */
   @Override
   void transfigure()
   {
      for (Entity entity : getCloseEntities(1.5))
      {
         if (entity.getUniqueId() == player.getUniqueId())
            continue;

         if (isTransfigured || !canTransfigure(entity))
            return;

         // check success
         int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
         if (rand < successRate)
         {
            originalEntity = entity;
            transfiguredEntity = transfigureEntity(entity);

            if (transfiguredEntity == null)
            {
               kill();
               common.printDebugMessage("Transfiguration failed in " + spellType.toString(), null, null, true);
            }
            else
            {
               isTransfigured = true;
               return;
            }
         }
         else
         {
            sendFailureMessage();
            common.printDebugMessage("Failed success check.", null, null, false);
            kill();
         }
      }
   }

   /**
    * Determine if this entity can be transfigured by this spell.
    *
    * @param entity the entity to check
    * @return true if the entity can be transfigured, false otherwise.
    */
   protected boolean canTransfigure(@NotNull Entity entity)
   {
      // is this the right entity type?
      if (!targetTypeCheck(entity))
         return false;

      // is this entity already transfigured?
      for (O2Spell spell : p.getProjectiles())
      {
         if (spell instanceof TransfigurationBase)
         {
            if (((TransfigurationBase)spell).isEntityTransfigured(entity))
               return false;
         }
      }

      return true;
   }

   /**
    * Transfigures entity into new EntityType.
    *
    * @param entity the entity to transfigure
    * @return the transfigured entity
    */
   @Nullable
   abstract protected Entity transfigureEntity(@NotNull Entity entity);

   /**
    * Determines if this entity can be changed by this Transfiguration spell.
    *
    * @param entity the entity to check
    * @return true if the entity can be changed, false otherwise.
    */
   boolean targetTypeCheck(@NotNull Entity entity)
   {
      // get entity type
      EntityType eType = entity.getType();

      boolean check = true;

      if (eType == targetType) // do not change if this entity is already the target type
      {
         common.printDebugMessage("Target entity is same type as spell type.", null, null, false);
         check = false;
      }
      else if (entityBlacklist.contains(eType)) // do not change if this entity is in the blacklist
      {
         common.printDebugMessage("EntityType is on the blacklist.", null, null, false);
         check = false;
      }
      else if (!entityWhitelist.isEmpty()) // do not change if the whitelist exists and this entity is not in it
      {
         if (!entityWhitelist.contains(eType))
         {
            common.printDebugMessage("EntityType is not on the whitelist.", null, null, false);
            check = false;
         }
      }

      return check;
   }

   /**
    * Revert the transfigured entity back to its original type
    */
   @Override
   protected void revert()
   {
      if (permanent)
         return;

      // remove the new entity
      if (transfiguredEntity != null)
      {
         if (transfiguredEntity instanceof InventoryHolder)
         {
            for (ItemStack stack : ((InventoryHolder) transfiguredEntity).getInventory().getContents())
            {
               transfiguredEntity.getWorld().dropItemNaturally(transfiguredEntity.getLocation(), stack);
            }
         }

         transfiguredEntity.remove();
      }
      isTransfigured = false;

      if (originalEntity == null)
         return;

      if (!consumeOriginal)
      {
         // get location to respawn
         Location loc;
         if (transfiguredEntity == null)
            loc = originalEntity.getLocation();
         else
            loc = transfiguredEntity.getLocation();

         if (loc.getWorld() == null)
         {
            common.printDebugMessage("location has a null world in " + spellType.toString(), null, null, true);
            return;
         }

         if (originalEntity.getType() == EntityType.DROPPED_ITEM)
         {
            Item item = (Item) originalEntity;
            loc.getWorld().dropItemNaturally(loc, item.getItemStack());
         }
         else if (originalEntity.getType() == EntityType.FALLING_BLOCK)
            loc.getWorld().spawnFallingBlock(loc, ((FallingBlock) originalEntity).getBlockData().clone());
         else
            respawnEntity();
      }

      doRevert();
   }

   /**
    * Spawn an entity like the original entity
    */
   void respawnEntity()
   {
      Entity respawn = transfiguredEntity.getWorld().spawnEntity(transfiguredEntity.getLocation(), originalEntity.getType());

      // set values as close to the original as possible
      respawn.setCustomName(originalEntity.getCustomName());
      respawn.setVelocity(transfiguredEntity.getVelocity());
      respawn.setGravity(originalEntity.hasGravity());
      respawn.setGlowing(originalEntity.isGlowing());
      respawn.setInvulnerable(originalEntity.isInvulnerable());
      // set age to be the combined of both the original and the transfigured
      respawn.setTicksLived(originalEntity.getTicksLived() + transfiguredEntity.getTicksLived());
   }

   /**
    * Do spell-specific revert functions beyond spawning the replacement entity
    */
   void doRevert() {}

   /**
    * Is this block transfigured by this spell
    *
    * @param block the block to check
    * @return true if transfigured, false otherwise
    */
   @Override
   public boolean isBlockTransfigured(@NotNull Block block)
   {
      return false;
   }

   /**
    * Is this entity transfigured by this spell
    *
    * @param entity the entity to check
    * @return true if transfigured, false otherwise
    */
   @Override
   public boolean isEntityTransfigured(@NotNull Entity entity)
   {
      if (permanent)
         return false;

      if (transfiguredEntity == null)
         return false;

      return transfiguredEntity.getUniqueId() == entity.getUniqueId();
   }
}
