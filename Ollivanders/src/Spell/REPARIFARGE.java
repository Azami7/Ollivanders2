package Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 *
 */
public class REPARIFARGE extends SpellProjectile implements Spell
{
   public REPARIFARGE (Ollivanders2 plugin, Player player, Spells name,
                       Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<Entity> entities = this.getCloseEntities(1);
      for (Entity entity : entities)
      {
         for (SpellProjectile proj : p.getProjectiles())
         {
            if (proj instanceof Transfiguration)
            {
               if (entity instanceof EnderDragonPart)
               {
                  entity = ((EnderDragonPart) entity).getParent();
               }
               if (entity.getUniqueId() == ((Transfiguration) proj).getToID())
               {
                  proj.lifeTicks = proj.lifeTicks + (int) (usesModifier * 1200) + 160;
               }
            }
         }
      }
   }
}