package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;

/**
 * Makes certain entities grow into adults, slimes grow larger, and at usesModifier 10, zombies grow into giants
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class ENGORGIO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ENGORGIO ()
   {
      super();

      flavorText.add("The Engorgement Charm");
      flavorText.add("These straightforward but surprisingly dangerous charms cause certain things to swell or shrink.");
      text = "Grows a baby animal, slime, or zombie.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public ENGORGIO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
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