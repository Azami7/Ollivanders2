package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;

import java.util.ArrayList;
import java.util.List;

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

      spellType = O2SpellType.ENGORGIO;

      flavorText = new ArrayList<String>() {{
         add("The Engorgement Charm");
         add("These straightforward but surprisingly dangerous charms cause certain things to swell or shrink.");
      }};

      text = "Grows a baby animal, slime, or zombie.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ENGORGIO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.ENGORGIO;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      // world guard flags
      worldGuardFlags.add(DefaultFlag.MOB_SPAWNING);
      worldGuardFlags.add(DefaultFlag.DAMAGE_ANIMALS);
   }

   /**
    * Look for entities within the projectile range and grow them, if possible
    */
   @Override
   protected void doCheckEffect ()
   {
      List<LivingEntity> livingEntities = getLivingEntities(1.5);

      if (livingEntities.size() > 0)
      {
         for (LivingEntity live : livingEntities)
         {
            if (live.getUniqueId() == player.getUniqueId())
               continue;

            if (live instanceof Ageable)
            {
               Ageable age = (Ageable) live;
               age.setAge((int) (age.getAge() + (usesModifier * 240)));

               break;
            }
            else if (live instanceof Zombie)
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

               break;
            }
            else if (live instanceof Slime)
            {
               Slime slime = (Slime) live;
               slime.setSize((int) (slime.getSize() + usesModifier));

               break;
            }
         }

         kill();
         return;
      }

      // projectile is stopped, kill spell
      if (hasHitTarget())
         kill();
   }
}