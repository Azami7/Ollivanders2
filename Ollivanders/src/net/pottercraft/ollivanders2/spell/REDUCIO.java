package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Shrinks a giant to a normal zombie, makes certain entities babies and slimes smaller.
 *
 * @author lownes
 * @author Azami7
 */
public final class REDUCIO extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public REDUCIO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.REDUCIO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Shrinking Charm");
         add("These straightforward but surprisingly dangerous charms cause certain things to swell or shrink. You will be learning both charms together, so that you can always undo an over-enthusiastic cast. There is thus no excuse for having accidentally shrunk your homework down to microscopic size or for allowing a giant toad to rampage through your school’s flower gardens.");
      }};

      text = "Shrinks a giant to a normal zombie, makes certain entities babies and slimes smaller.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public REDUCIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.REDUCIO;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   @Override
   protected void doCheckEffect()
   {
      for (LivingEntity live : getLivingEntities(usesModifier))
      {
         if (live.getUniqueId() == player.getUniqueId())
            continue;

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
            if (zombie.isAdult())
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

      if (hasHitTarget())
         kill();
   }
}