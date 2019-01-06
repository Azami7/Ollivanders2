package net.pottercraft.Ollivanders2.Effect;

import java.util.Collection;
import java.util.UUID;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;

import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
    * @param plugin a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid the ID of the player this effect acts on
    */
   public AGGRESSION (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.AGGRESSION;
      legilimensText = "feels aggressive";

      divinationText.add("will suffer from an insatiable rage");
      divinationText.add("will succomb to a primal fear");
      divinationText.add("shall be afflicted in the mind");
      divinationText.add("shall lose their mind to insanity");
      divinationText.add("will be possessed by a demon spirit");

      permanent = true;
      target = p.getServer().getPlayer(targetID);
   }

   /**
    * Attack random entity every 15 seconds and provoke nearby Creatures to attack.
    */
   @Override
   public void checkEffect ()
   {
      if (!permanent)
      {
         age(1);
      }

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
   private void damageRandomEntity (Collection<LivingEntity> nearby)
   {
      Player target = p.getServer().getPlayer(targetID);

      if (nearby != null && !nearby.isEmpty())
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
         if (Ollivanders2.worldGuardEnabled && !Ollivanders2.worldGuardO2.checkWGFriendlyMobDamage(target, toDamage.getLocation()))
         {
            return;
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
   private void provoke (Collection<LivingEntity> nearby)
   {
      Player target = p.getServer().getPlayer(targetID);

      if (nearby != null && !nearby.isEmpty())
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
      if (level < 1)
         level = 1;
      else if (level > 10)
         level = 10;

      aggressionLevel = level;
   }
}
