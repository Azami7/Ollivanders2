package net.pottercraft.Ollivanders2.Effect;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

/**
 * Turns player into a werewolf during the full moon. Doesn't go away until death.
 *
 * @author lownes
 */
public class LYCANTHROPY extends O2Effect
{
   int wereId = -1;

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    */
   public LYCANTHROPY (Ollivanders2 plugin, O2EffectType effect, int duration)
   {
      super(plugin, effect, duration);
   }

   /**
    * Check the time of day for the player and transform them in to or back from
    * a wolf Entity.
    *
    * @param target the player affected by the effect
    */
   @Override
   public void checkEffect (Player target)
   {
      long time = target.getWorld().getFullTime();
      long dayOrNight = (time / 12000) % 2;
      long days = time / 24000;
      long phase = days % 8;
      boolean wolfsbane = false;
      for (O2Effect effect : p.getO2Player(target).getEffects())
      {
         if (effect instanceof WOLFSBANE_POTION)
         {
            wolfsbane = true;
            break;
         }
      }
      if (phase == 0 && dayOrNight == 1 && !wolfsbane)
      {
         //Full moon at night
         if (wereId == -1)
         {
            //spawn werewolf and set wereId and set wolf name to werewolf and set
            //it to be angry
            Wolf werewolf = (Wolf) target.getWorld().spawnEntity(target.getLocation(), EntityType.WOLF);
            werewolf.setAngry(true);
            werewolf.setCustomName("Werewolf");
            werewolf.setCustomNameVisible(true);
            wereId = werewolf.getEntityId();
         }
         else
         {
            //see if wereId points to a wolf with name werewolf.
            //If so, teleport to it. if not, kill player
            for (Entity entity : target.getWorld().getEntities())
            {
               if (entity.getEntityId() == wereId && entity.getType() == EntityType.WOLF)
               {
                  Wolf wolf = (Wolf) entity;
                  if (wolf.getCustomName().equals("Werewolf"))
                  {
                     target.teleport(entity);
                     if (!wolf.isAngry())
                     {
                        wolf.setAngry(true);
                     }
                     if (wolf.getTarget() == target)
                     {
                        wolf.setTarget(null);
                     }
                     if (time % 20 == 0)
                     {
                        for (Player other : target.getWorld().getPlayers())
                        {
                           other.hidePlayer(p, target);
                        }
                     }
                     return;
                  }
               }
            }
            target.damage(1000.0);
         }
      }
      else
      {
         //if wereId points to a wolf with the name werewolf, kill it and
         //set wereId to -1
         if (wereId != -1)
         {
            for (Player other : target.getWorld().getPlayers())
            {
               other.showPlayer(p, target);
            }
            for (Entity entity : target.getWorld().getEntities())
            {
               if (entity.getEntityId() == wereId && entity.getType() == EntityType.WOLF)
               {
                  if (entity.getCustomName().equals("Werewolf"))
                  {
                     entity.remove();
                     wereId = -1;
                     return;
                  }
               }
            }
            wereId = -1;
         }
      }
   }
}
