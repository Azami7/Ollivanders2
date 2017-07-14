package Spell;

import net.pottercraft.Ollivanders2.SpellProjectile;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Bat;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Spells;

/**
 * Conjures a flock of birds from the tip of the wand.
 *
 * @version Ollivanders2
 * @author Azami7
 */
public class AVIS extends SpellProjectile implements Spell
{
   private int birdCount = 0;
   private int maxBirds = 2;

   public AVIS (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      if (usesModifier > 100)
         maxBirds += 10;
      else
         maxBirds += (int)usesModifier / 10;
   }

   public void checkEffect()
   {
      if (birdCount < maxBirds)
      {
         move();

         if (Ollivanders2.mcVersionCheck())
         {
            Parrot bird = (Parrot) location.getWorld().spawnEntity(location, EntityType.PARROT);

            int rand = Math.abs(Ollivanders2.random.nextInt() % 5);
            Parrot.Variant variant;
            if (rand == 0)
               variant = Parrot.Variant.CYAN;
            else if (rand == 1)
               variant = Parrot.Variant.GRAY;
            else if (rand == 2)
               variant = Parrot.Variant.BLUE;
            else if (rand == 3)
               variant = Parrot.Variant.GREEN;
            else
               variant = Parrot.Variant.RED;
            bird.setVariant(variant);
         }
         else
         {
            Bat bird = (Bat) location.getWorld().spawnEntity(location, EntityType.BAT);
         }

         birdCount++;
      }
      else
      {
         kill();
      }
   }
}