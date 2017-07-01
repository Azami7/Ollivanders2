package Effect;

import org.bukkit.entity.Horse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/28/17.
 *
 * @author Azami7
 */
public class INCARNATIO_EQUUS extends OEffect implements Effect
{
   int horseID = -1;

   public INCARNATIO_EQUUS(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect(Ollivanders2 p, Player owner)
   {
      age(1);
      if (duration > 0)
      {
         if(horseID == -1)
         {
            Horse horse = (Horse) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.HORSE);
            horse.setCustomName("Horse");
            horse.setCustomNameVisible(true);
            horseID = horse.getEntityId();
         }
         else{
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == horseID && entity.getType() == EntityType.HORSE)
               {
                  Horse horse = (Horse)entity;
                  if (horse.getCustomName().equals("Horse"))
                  {
                     owner.teleport(entity);
                     if(horse.getTarget() == owner)
                     {
                        horse.setTarget(null);
                     }
                     if (duration > 0)
                     {
                        for (Player other : owner.getWorld().getPlayers())
                        {
                           other.hidePlayer(owner);
                        }
                     }
                     return;
                  }
               }
            }
            duration = 0;
         }
      }
      else
      {
         if (horseID != -1)
         {
            for (Player other : owner.getWorld().getPlayers())
            {
               other.showPlayer(owner);
            }
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == horseID && entity.getType() == EntityType.HORSE)
               {
                  if (((Horse)entity).getCustomName().equals("Horse"))
                  {
                     entity.remove();
                     horseID = -1;
                     return;
                  }
               }
            }
         }
         horseID = -1;
      }
   }
}
