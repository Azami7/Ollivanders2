package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.*;

/**
 * Shrinks a giant to a normal zombie, makes certain entities babies and slimes smaller.
 *
 * @author lownes
 * @author Azami7
 */
public final class REDUCIO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REDUCIO ()
   {
      super();

      flavorText.add("The Shrinking Charm");
      flavorText.add("These straightforward but surprisingly dangerous charms cause certain things to swell or shrink. You will be learning both charms together, so that you can always undo an over-enthusiastic cast. There is thus no excuse for having accidentally shrunk your homework down to microscopic size or for allowing a giant toad to rampage through your school’s flower gardens.");
      text = "Shrinks a giant to a normal zombie, makes certain entities babies and slimes smaller.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public REDUCIO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
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
      }
   }
}