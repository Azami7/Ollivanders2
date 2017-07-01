package Effect;

import org.bukkit.entity.Wolf;
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
public class INCARNATIO_LUPI extends OEffect implements Effect
{
   int wolfID = -1;

   public INCARNATIO_LUPI(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect(Ollivanders2 p, Player owner)
   {
      age(1);
      if (duration > 0)
      {
         if(wolfID == -1)
         {
            Wolf wolf = (Wolf) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.WOLF);
            wolf.setCustomName("Wolf");
            wolf.setCustomNameVisible(true);
            wolfID = wolf.getEntityId();
         }
         else
         {
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == wolfID && entity.getType() == EntityType.WOLF)
               {
                  Wolf wolf = (Wolf)entity;
                  if (wolf.getCustomName().equals("Wolf"))
                  {
                     owner.teleport(entity);
                     if(wolf.getTarget() == owner)
                     {
                        wolf.setTarget(null);
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
         if (wolfID != -1)
         {
            for (Player other : owner.getWorld().getPlayers())
            {
               other.showPlayer(owner);
            }
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == wolfID && entity.getType() == EntityType.WOLF)
               {
                  if (((Wolf)entity).getCustomName().equals("Wolf"))
                  {
                     entity.remove();
                     wolfID = -1;
                     return;
                  }
               }
            }
         }
         wolfID = -1;
      }
   }
}
