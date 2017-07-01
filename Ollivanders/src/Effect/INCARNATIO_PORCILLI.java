package Effect;

import org.bukkit.entity.Pig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/27/17. Imported from iarepandemonium/Ollivanders.
 *
 * @author lownes
 */
public class INCARNATIO_PORCILLI extends OEffect implements Effect
{
   int pigID = -1;

   public INCARNATIO_PORCILLI(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect(Ollivanders2 p, Player owner)
   {
      age(1);
      if (duration > 0)
      {
         if(pigID == -1)
         {
            Pig pig = (Pig) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.PIG);
            pig.setCustomName("Pig");
            pig.setCustomNameVisible(true);
            pigID = pig.getEntityId();
         }
         else
         {
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == pigID && entity.getType() == EntityType.PIG)
               {
                  Pig pig = (Pig)entity;
                  if (pig.getCustomName().equals("Pig"))
                  {
                     owner.teleport(entity);
                     if(pig.getTarget() == owner)
                     {
                        pig.setTarget(null);
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
            for (Player other : owner.getWorld().getPlayers())
            {
               other.showPlayer(owner);
            }
            owner.damage(1000.0);
            duration = 0;
         }
      }
      else
      {
         if (pigID != -1)
         {
            for (Player other : owner.getWorld().getPlayers())
            {
               other.showPlayer(owner);
            }
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == pigID && entity.getType() == EntityType.PIG)
               {
                  if (((Pig)entity).getCustomName().equals("Pig"))
                  {
                     entity.remove();
                     pigID = -1;
                     return;
                  }
               }
            }
         }
         pigID = -1;
      }
   }
}
