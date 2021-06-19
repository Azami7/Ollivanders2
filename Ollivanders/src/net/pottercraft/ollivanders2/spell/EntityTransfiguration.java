package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The super class for transfiguration of all entities.
 *
 * @author Azami7
 * @since 2.2.6
 */
public abstract class EntityTransfiguration extends O2Spell
{
   /**
    * If the transfiguration has taken place or not.
    */
   boolean isTransfigured = false;

   /**
    * The type of entity this will transfigure.
    */
   EntityType targetType = EntityType.SHEEP;

   /**
    * If this is not permanent, how long it should last.
    */
   int spellDuration = 120;

   /**
    * Allows spell variants to change the duration of this spell.
    */
   private double durationModifier = 1.0;

   /**
    * The percent chance this spell will succeed each casting.
    */
   protected int successRate = 100;

   /**
    * A blacklist of Entity types that will not be affected by this spell.  Only used if the whitelist is empty.
    */
   List<EntityType> entityBlacklist = new ArrayList<>();

   /**
    * A whitelist of Entity types that will be affected by this spell.
    */
   List<EntityType> entityWhitelist = new ArrayList<>();

   /**
    * The duration of the transfiguration for non-permanent instances
    */
   private int transfigurationTime = 0;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EntityTransfiguration()
   {
      super();

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
    * Look for entities in the radius of the projectile and transfigure one if found
    */
   @Override
   protected void doCheckEffect()
   {
      // if an entity has not transfigured, look for one to transfigure
      if (!isTransfigured)
      {
         for (Entity e : getCloseEntities(1.5))
         {
            if (e.getUniqueId() == player.getUniqueId())
               continue;

            transfigure(e);
            stopProjectile();

            // if entity did not change, the spell failed
            if (!isTransfigured)
            {
               kill();
            }

            return;
         }

         // if projectile has stopped moving and we did not transfigure an entity, kill the spell
         if (!isTransfigured && hasHitTarget())
            kill();
      }
      else
      {
         // check duration on the transfiguration
         if (transfigurationTime >= spellDuration)
         {
            kill();
            return;
         }

         transfigurationTime++;
      }
   }

   /**
    * Transfigure the target living entity. Will not change the entity if it is on the entityBlacklist list or
    * if the target entity is already the transfigure type.
    *
    * @param entity the entity to transfigure
    */
   protected void transfigure(@NotNull Entity entity)
   {
      if (isTransfigured || !canTransfigure(entity))
      {
         return;
      }

      // check success
      int rand = Math.abs(Ollivanders2Common.random.nextInt() % 100);
      if (rand < successRate)
      {
         transfigureEntity(entity);

         spellDuration = (int) (spellDuration * durationModifier);

         // do not let the spell last more than 24 hours
         if (spellDuration > 86400)
            spellDuration = 86400;
      }
      else
      {
         common.printDebugMessage("Failed success check.", null, null, false);
      }
   }

   /**
    * Determine if this entity can be transfigured by this spell. This must be overridden by child classes or
    * it will fail all transfigurations.
    *
    * @param e the entity to check
    * @return true if the entity can be transfigured, false otherwise.
    */
   protected boolean canTransfigure(@NotNull Entity e)
   {
      return false;
   }

   /**
    * Transfigures entity into new EntityType.
    *
    * @param entity the entity to transfigure
    */
   protected void transfigureEntity(@NotNull Entity entity)
   {
   }

   /**
    * Determines if this entity can be changed by this Transfiguration spell.
    *
    * @param e the entity to check
    * @return true if the entity can be changed, false otherwise.
    */
   protected boolean targetTypeCheck(@NotNull Entity e)
   {
      // get entity type
      EntityType eType = e.getType();

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
}
