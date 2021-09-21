package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.listeners.OllivandersListener;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Super class for transfiguring friendly mobs.
 *
 * @author Azami7
 * @since 2.2.6
 */
public abstract class FriendlyMobDisguise extends EntityDisguise
{
   int minDurationInSeconds = 15;
   int maxDurationInSeconds = 600; // 10 minutes

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public FriendlyMobDisguise(Ollivanders2 plugin)
   {
      super(plugin);
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FriendlyMobDisguise(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
      }
   }

   @Override
   void doInitSpell()
   {
      // whitelist of entities that can be targeted by this spell
      entityWhitelist.addAll(Ollivanders2Common.smallFriendlyAnimals);

      int uses = (int) (usesModifier * 4);

      if (uses > 100)
      {
         entityWhitelist.addAll(Ollivanders2Common.mediumFriendlyAnimals);
      }

      if (uses > 200)
      {
         entityWhitelist.addAll(Ollivanders2Common.largeFriendlyAnimals);
      }

      // spell duration
      int durationInSeconds = (int) usesModifier;
      if (durationInSeconds < minDurationInSeconds)
      {
         durationInSeconds = minDurationInSeconds;
      }
      else if (durationInSeconds > maxDurationInSeconds)
      {
         durationInSeconds = maxDurationInSeconds;
      }

      spellDuration = durationInSeconds * Ollivanders2Common.ticksPerSecond;
   }
}
