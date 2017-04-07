package Spell;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * All animals that you have created through transfiguration will target the targeted LivingEntity.
 *
 * @author lownes
 */
public class OPPUGNO extends SpellProjectile implements Spell
{

   public OPPUGNO (Ollivanders2 plugin, Player player, Spells name,
                   Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity e : getLivingEntities(1))
      {
         for (SpellProjectile spell : p.getProjectiles())
         {
            if (spell instanceof Transfiguration)
            {
               if (spell.player.equals(player))
               {
                  for (Entity entity : player.getWorld().getEntities())
                  {
                     if (entity.getUniqueId() == ((Transfiguration) spell).getToID())
                     {
                        if (entity instanceof LivingEntity)
                        {
                           //getLogger().info(entity.getType() + " is now targeting " + e.getType());
                           //((Creature)entity).setTarget(e);
                           ((Creature) entity).damage(0.0, e);
                           kill();
                        }
                     }
                  }
               }
            }
         }
      }
   }

}