package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Makes certain entities grow into adults, slimes grow larger, and at
 * usesModifier 10, zombies grow into giants
 *
 * @author lownes
 */
public class ENGORGIO extends SpellProjectile implements Spell
{

   public ENGORGIO (Ollivanders2 plugin, Player player, Spells name,
                    Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1))
      {
         if (live instanceof Ageable)
         {
            Ageable age = (Ageable) live;
            age.setAge((int) (age.getAge() + (usesModifier * 240)));
         }
         if (live instanceof Zombie)
         {
            Zombie zombie = (Zombie) live;
            if (zombie.isBaby())
            {
               zombie.setBaby(false);
            }
            else if (usesModifier >= 10)
            {
               zombie.getWorld().spawnEntity(zombie.getLocation(), EntityType.GIANT);
               zombie.remove();
            }
         }
         if (live instanceof Slime)
         {
            Slime slime = (Slime) live;
            slime.setSize((int) (slime.getSize() + usesModifier));
         }
         kill();
         return;
      }
   }

}