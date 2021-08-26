package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Temporarily disables a stationary spell's effects if it is your spell.
 *
 * @author lownes
 * @author Azami7
 */
public final class PARTIS_TEMPORUS extends O2Spell
{
   private int duration;
   private final static int minDurationInSeconds = 15;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PARTIS_TEMPORUS()
   {
      super();

      spellType = O2SpellType.PARTIS_TEMPORUS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("The Parting Charm");
      }};

      text = "Partis temporus, if cast at a stationary spell that you have cast, will cause that stationary spell's effects to stop for a short time.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PARTIS_TEMPORUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PARTIS_TEMPORUS;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      int durationInSeconds = (int) usesModifier;
      if (durationInSeconds < minDurationInSeconds)
      {
         durationInSeconds = minDurationInSeconds;
      }

      duration = durationInSeconds * Ollivanders2Common.ticksPerSecond;
   }

   @Override
   protected void doCheckEffect ()
   {
      for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells(p).getStationarySpellsAtLocation(location))
      {
         if (stationarySpell.getCasterID() == player.getUniqueId())
         {
            stopProjectile();

            stationarySpell.active = false;
            stationarySpell.flair(10);

            if (duration > stationarySpell.duration)
            {
               duration = stationarySpell.duration;
            }
         }
      }

      if (hasHitTarget())
      {
         duration--;

         if (duration <= 0)
         {
            kill();
         }
      }
   }

   @Override
   protected void revert ()
   {
      for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells(p).getStationarySpellsAtLocation(location))
      {
         if (stationarySpell.getCasterID() == player.getUniqueId())
         {
            stationarySpell.active = true;
            stationarySpell.flair(10);
         }
      }
   }
}