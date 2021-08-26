package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Shield spell
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class PROTEGO extends StationarySpell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO()
   {
      super();

      spellType = O2SpellType.PROTEGO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("\"I don't remember telling you to use a Shield Charm... but there is no doubt that it was effective...\" -Severus Snape");
         add("The Shield Charm");
      }};

      text = "Protego is a shield spell which, while you are crouching, will cause any spells cast at it to bounce off.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PROTEGO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PROTEGO;
      branch = O2MagicBranch.CHARMS;
      initSpell();

      baseDurationInSeconds = 1;
      durationModifierInSeconds = 1;
      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
      centerOnCaster = true;
   }

   @Override
   protected O2StationarySpell createStationarySpell ()
   {
      // protego has a limited duration, ensure duration is not set too high
      if (duration > (3 * Ollivanders2Common.ticksPerSecond))
      {
         duration = 3 * Ollivanders2Common.ticksPerSecond;
      }

      return new net.pottercraft.ollivanders2.stationaryspell.PROTEGO(p, player.getUniqueId(), location, O2StationarySpellType.PROTEGO, radius, duration);
   }
}