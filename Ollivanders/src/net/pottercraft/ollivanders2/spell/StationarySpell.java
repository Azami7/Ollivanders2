package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Parent class for all spells that create a stationary spell.
 *
 * @author Azami7
 * @since 2.3
 */
public abstract class StationarySpell extends O2Spell
{
   /**
    * The time left for this stationary spell.
    */
   int duration;

   /**
    * Base duration
    */
   int baseDurationInSeconds = 30;

   /**
    * Duration modifier
    */
   int durationModifierInSeconds = 15;

   /**
    * Radius of the spell
    */
   int radius;

   /**
    * Base radius
    */
   int baseRadius = 5;

   /**
    * Radius modifier
    */
   int radiusModifier = 1;

   /**
    * Flair size
    */
   int flairSize = 10;

   /**
    * Is the location for this stationary spell centered at the caster or a projectile target
    */
   boolean centerOnCaster = false;

   /**
    * The maximum duration a stationary spell can last, if it is not permanent
    */
   static int maxDuration = Ollivanders2Common.ticksPerSecond * 1800; // 30 minutes, only applies to temporary stationary spells.

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public StationarySpell(Ollivanders2 plugin)
   {
      super(plugin);

      branch = O2MagicBranch.CHARMS;
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public StationarySpell(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      branch = O2MagicBranch.CHARMS;

      // world guard flags
      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.BUILD);

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);
   }

   @Override
   protected void doCheckEffect()
   {
      if (!centerOnCaster && !hasHitTarget())
      {
         return;
      }

      // set duration to be base time plus a modifier seconds per experience level for this spell
      duration = ((int) usesModifier * Ollivanders2Common.ticksPerSecond * durationModifierInSeconds) + (Ollivanders2Common.ticksPerSecond * baseDurationInSeconds);
      if (duration > maxDuration)
         duration = maxDuration;

      radius = baseRadius * radiusModifier;

      O2StationarySpell stationarySpell = createStationarySpell();

      if (stationarySpell != null)
      {
         stationarySpell.flair(flairSize);
         Ollivanders2API.getStationarySpells(p).addStationarySpell(stationarySpell);
      }

      kill();
   }

   /**
    * Create the stationary spell. Has to be overridden by child spells to have effect.
    *
    * @return the stationary spell or null if one is not created.
    */
   protected O2StationarySpell createStationarySpell()
   {
      return null;
   }
}
