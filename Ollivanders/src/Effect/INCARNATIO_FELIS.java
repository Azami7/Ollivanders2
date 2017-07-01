package Effect;

import java.util.Random;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.OEffect;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/27/17. Imported from iarepandemonium/Ollivanders.
 *
 * @author lownes
 * @author Azami7
 */
public class INCARNATIO_FELIS extends OEffect implements Effect
{
   int ocelotID = -1;

   public INCARNATIO_FELIS(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect(Ollivanders2 p, Player owner)
   {
      age(1);
      if (duration > 0)
      {
         if(ocelotID == -1)
         {
            Ocelot ocelot = (Ocelot)owner.getWorld().spawnEntity(owner.getLocation(), EntityType.OCELOT);

            int rand = (new Random().nextInt()) % 3;
            if (rand == 0)
               ocelot.setCatType(Ocelot.Type.BLACK_CAT);
            else if (rand == 1)
               ocelot.setCatType(Ocelot.Type.RED_CAT);
            else
               ocelot.setCatType(Ocelot.Type.SIAMESE_CAT);

            ocelot.setCustomName("Cat");
            ocelot.setCustomNameVisible(true);
            ocelotID = ocelot.getEntityId();
         }
         else
         {
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == ocelotID && entity.getType() == EntityType.OCELOT)
               {
                  Ocelot ocelot = (Ocelot)entity;
                  if (ocelot.getCustomName().equals("Cat"))
                  {
                     owner.teleport(entity);
                     if(ocelot.getTarget() == owner)
                     {
                        ocelot.setTarget(null);
                     }
                     if (duration > 0) {
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
         if (ocelotID != -1)
         {
            for (Player other : owner.getWorld().getPlayers())
            {
               other.showPlayer(owner);
            }
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == ocelotID && entity.getType() == EntityType.OCELOT)
               {
                  if (((Ocelot)entity).getCustomName().equals("Cat"))
                  {
                     entity.remove();
                     ocelotID = -1;
                     return;
                  }
               }
            }
         }
         ocelotID = -1;
      }
   }
}
