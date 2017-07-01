package Effect;

import org.bukkit.entity.Cow;
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
public class INCARNATIO_VACCULA extends OEffect implements Effect
{
   int cowID = -1;

   public INCARNATIO_VACCULA(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect(Ollivanders2 p, Player owner)
   {
      age(1);
      if (duration > 0)
      {
         if(cowID == -1)
         {
            Cow cow = (Cow) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.COW);
            cow.setCustomName("Cow");
            cow.setCustomNameVisible(true);
            cowID = cow.getEntityId();
         }
         else{
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == cowID && entity.getType() == EntityType.COW)
               {
                  Cow cow = (Cow)entity;
                  if (cow.getCustomName().equals("Cow"))
                  {
                     owner.teleport(entity);
                     if(cow.getTarget() == owner)
                     {
                        cow.setTarget(null);
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
         if (cowID != -1)
         {
            for (Player other : owner.getWorld().getPlayers())
            {
               other.showPlayer(owner);
            }
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == cowID && entity.getType() == EntityType.COW)
               {
                  if (((Cow)entity).getCustomName().equals("Cow"))
                  {
                     entity.remove();
                     cowID = -1;
                     return;
                  }
               }
            }
         }
         cowID = -1;
      }
   }
}
