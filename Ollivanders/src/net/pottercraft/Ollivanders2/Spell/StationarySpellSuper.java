package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Parent class for all spells that create a stationary spell.
 *
 * @author Azami7
 * @since 2.3
 */
public abstract class StationarySpellSuper extends Charms
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
   static int maxDuration = Ollivanders2Common.ticksPerSecond * 1800; // 30 minutes, only applies to temporaty stationary spells.

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public StationarySpellSuper ()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public StationarySpellSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      // world guard flags
      worldGuardFlags.add(DefaultFlag.BUILD);

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);
   }

   @Override
   protected void doCheckEffect ()
   {
      if (!centerOnCaster && !hasHitTarget())
      {
         return;
      }

      // set duration to be base time plus a modifier seconds per experience level for this spell
      duration = ((int) usesModifier * Ollivanders2Common.ticksPerSecond * durationModifierInSeconds) + (Ollivanders2Common.ticksPerSecond * baseDurationInSeconds);
      radius = baseRadius * radiusModifier;

      StationarySpellObj stationarySpell = createStationarySpell();

      if (stationarySpell != null)
      {
         stationarySpell.flair(flairSize);
         Ollivanders2API.getStationarySpells().addStationarySpell(stationarySpell);
      }

      kill();
   }

   protected StationarySpellObj createStationarySpell ()
   {
      return null;
   }
}
