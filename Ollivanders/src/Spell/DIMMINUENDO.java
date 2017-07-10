package Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;

import net.pottercraft.Ollivanders2.SpellProjectile;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Makes certain entities babies and slimes smaller
 *
 * @author lownes
 */
public class DIMMINUENDO extends SpellProjectile implements Spell
{
   public DIMMINUENDO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
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
            age.setAge((int) (age.getAge() - (usesModifier * 240)));
         }
         if (live instanceof Zombie)
         {
            Zombie zombie = (Zombie) live;
            if (!zombie.isBaby())
            {
               zombie.setBaby(true);
            }
         }
         if (live instanceof Slime)
         {
            Slime slime = (Slime) live;
            slime.setSize((int) (slime.getSize() - usesModifier));
         }
         kill();
         return;
      }
   }
}