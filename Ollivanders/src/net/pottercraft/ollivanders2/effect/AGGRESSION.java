package net.pottercraft.ollivanders2.effect;

import java.util.Collection;
import java.util.UUID;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This effect causes the player to impulsively harm those nearby and causes them to provoke attacks by
 * nearby mobs. This effect does not age but must be removed explicitly.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class AGGRESSION extends O2Effect
{
   // Value from 1-10 for how aggressive this player will be with 1 being lowest level
   int aggressionLevel = 5;

   Player target;

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the ID of the player this effect acts on
    */
   public AGGRESSION(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.AGGRESSION;
      legilimensText = "feels aggressive";

      divinationText.add("will suffer from an insatiable rage");
      divinationText.add("will succomb to a primal fear");
      divinationText.add("shall be afflicted in the mind");
      divinationText.add("shall lose their mind to insanity");
      divinationText.add("will be possessed by a demon spirit");
      divinationText.add("shall be cursed");

      permanent = true;
      target = p.getServer().getPlayer(targetID);

      aggressionLevel = duration;
   }

   /**
    * Attack random entity every 15 seconds and provoke nearby Creatures to attack.
    */
   @Override
   public void checkEffect ()
   {
      // only take action once per 10 seconds, which is every 120 ticks
      if ((duration % 120) == 0)
      {
         int rand = Math.abs(Ollivanders2Common.random.nextInt()) % 10;

         Ollivanders2Common common = new Ollivanders2Common(p);
         if (rand < aggressionLevel)
         {
            // damage nearby entity
            Collection<LivingEntity> nearby = common.getLivingEntitiesInRadius(target.getLocation(), 3);
            damageRandomEntity(nearby);

            // provoke nearby Creatures to attack
            nearby = common.getLivingEntitiesInRadius(target.getLocation(), 6);
            provoke(nearby);
         }
      }
   }

   /**
    * Damage a random nearby entity.
    *
    * @param nearby a collection of nearby entities
    */
   private void damageRandomEntity(@NotNull Collection<LivingEntity> nearby)
   {
      Player target = p.getServer().getPlayer(targetID);

      if (!nearby.isEmpty())
      {
         int rand = Math.abs(Ollivanders2Common.random.nextInt());
         LivingEntity[] nArray = nearby.toArray(new LivingEntity[nearby.size()]);

         LivingEntity toDamage = nArray[rand % nearby.size()];

         // don't let the player hit themselves
         if (toDamage.getUniqueId() != targetID)
         {
            return;
         }

         // don't do damage if worldguard is protecting where the entity is
         if (Ollivanders2.worldGuardEnabled)
         {
            if (toDamage instanceof Player && !Ollivanders2.worldGuardO2.checkWGFlag(target, toDamage.getEyeLocation(), Flags.PVP))
            {
               return;
            }
            else if (!(toDamage instanceof Monster) && !Ollivanders2.worldGuardO2.checkWGFlag(target, toDamage.getEyeLocation(), Flags.DAMAGE_ANIMALS))
            {
               return;
            }
         }

         double curHealth = toDamage.getHealth();
         // damage is entities current health divided by 2, 3, or 4
         rand = Math.abs(Ollivanders2Common.random.nextInt());
         double damage = curHealth / ((rand % 3) + 2);
         toDamage.damage(damage, target);
      }
   }

   /**
    * Provoke nearby Creatures to target this player.
    *
    * @param nearby collection of nearby living entities
    */
   private void provoke(@NotNull Collection<LivingEntity> nearby)
   {
      Player target = p.getServer().getPlayer(targetID);

      if (!nearby.isEmpty())
      {
         for (LivingEntity entity : nearby)
         {
            if (entity instanceof Creature)
            {
               ((Creature) entity).setTarget(target);
            }
         }
      }
   }

   /**
    * Set the aggression level for this player
    *
    * @param level 1-10 where 1 is the lowest
    */
   void setAggressionLevel (int level)
   {
      aggressionLevel = level;

      if (aggressionLevel < 1)
         aggressionLevel = 1;
      else if (aggressionLevel > 10)
         aggressionLevel = 10;

      duration = aggressionLevel;
   }

   /**
    * Override set permanent so that it cannot be called and alter duration, which we override to also set
    * the aggression level since this is always permanent.
    *
    * @param perm true if this is permanent, false otherwise
    */
   @Override
   public void setPermanent (boolean perm) { }
}
