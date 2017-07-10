package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Shrinks a giant to a normal zombie. It's radius depends on experience
 *
 * @author lownes
 */
public class REDUCIO extends SpellProjectile implements Spell
{
   public REDUCIO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(usesModifier))
      {
         if (live instanceof Giant)
         {
            live.getWorld().spawnEntity(live.getLocation(), EntityType.ZOMBIE);
            live.remove();
            kill();
         }
      }
   }
}