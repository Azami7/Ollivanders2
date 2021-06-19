package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Zombie;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Makes certain entities grow into adults, slimes grow larger, and at usesModifier 10, zombies grow into giants
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class ENGORGIO extends O2Spell
{
   private final int maxSlimeSize = 10;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ENGORGIO()
   {
      super();

      spellType = O2SpellType.ENGORGIO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("The Engorgement Charm");
         add("These straightforward but surprisingly dangerous charms cause certain things to swell or shrink.");
      }};

      text = "Grows a baby animal, slime, or zombie.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ENGORGIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.ENGORGIO;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.MOB_SPAWNING);
         worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
      }
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
               double ageIncreaseInMinutes = usesModifier / 5;

               // baby animals become adults after 20 minutes, or ~24000 ticks
               if (ageIncreaseInMinutes > 20)
               {
                  ageIncreaseInMinutes = 20;
               }

               age.setAge((int) (age.getAge() + (ageIncreaseInMinutes * Ollivanders2Common.ticksPerSecond)));

               kill();
               return;
            }
            else if (live instanceof Zombie)
            {
               Zombie zombie = (Zombie) live;
               if (zombie.isBaby())
               {
                  zombie.setBaby(false);
               }
               else if (usesModifier >= 100)
               {
                  zombie.getWorld().spawnEntity(zombie.getLocation(), EntityType.GIANT);
                  zombie.remove();
               }

               kill();
               return;
            }
            else if (live instanceof Slime)
            {
               Slime slime = (Slime) live;
               int slimeSize = (int) usesModifier / 20;

               if (slimeSize < slime.getSize())
               {
                  slimeSize = slime.getSize();
               }
               else if (slimeSize > maxSlimeSize)
               {
                  slimeSize = maxSlimeSize;
               }

               slime.setSize(slimeSize);

               kill();
               return;
            }
         }
      }

      // projectile is stopped, kill spell
      if (hasHitTarget())
         kill();
   }
}