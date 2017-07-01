package Effect;

import org.bukkit.entity.Llama;
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
public class INCARNATIO_LAMA extends OEffect implements Effect
{
   int llamaID = -1;

   public INCARNATIO_LAMA(Player sender, Effects effect, int duration)
   {
      super(sender, effect, duration);
   }

   @Override
   public void checkEffect(Ollivanders2 p, Player owner)
   {
      age(1);
      if (duration > 0)
      {
         if(llamaID == -1)
         {
            Llama llama = (Llama) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.LLAMA);
            llama.setCustomName("Llama");
            llama.setCustomNameVisible(true);
            llamaID = llama.getEntityId();
         }
         else{
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == llamaID && entity.getType() == EntityType.LLAMA)
               {
                  Llama llama = (Llama)entity;
                  if (llama.getCustomName().equals("Llama"))
                  {
                     owner.teleport(entity);
                     if(llama.getTarget() == owner)
                     {
                        llama.setTarget(null);
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
         if (llamaID != -1)
         {
            for (Player other : owner.getWorld().getPlayers())
            {
               other.showPlayer(owner);
            }
            for (Entity entity : owner.getWorld().getEntities())
            {
               if (entity.getEntityId() == llamaID && entity.getType() == EntityType.LLAMA)
               {
                  if (((Llama)entity).getCustomName().equals("LLama"))
                  {
                     entity.remove();
                     llamaID = -1;
                     return;
                  }
               }
            }
         }
         llamaID = -1;
      }
   }
}