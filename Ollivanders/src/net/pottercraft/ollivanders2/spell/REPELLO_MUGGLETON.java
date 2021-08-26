package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Adds a repello muggleton stationary spell object.
 *
 * @author lownes
 * @author Azami7
 */
public final class REPELLO_MUGGLETON extends StationarySpell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REPELLO_MUGGLETON()
   {
      super();

      spellType = O2SpellType.REPELLO_MUGGLETON;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("Muggle-Repelling Charms on every inch of it. Every time Muggles have got anywhere near here all year, they've suddenly remembered urgent appointments and had to dash away again.");
      }};

      text = "Repello Muggleton will hide any blocks and players in it's radius from those outside of it.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.REPELLO_MUGGLETON;
      branch = O2MagicBranch.CHARMS;
      initSpell();

      baseDurationInSeconds = 30;
      durationModifierInSeconds = 15;
      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
      centerOnCaster = true;
   }

   @Override
   protected O2StationarySpell createStationarySpell ()
   {
      return new net.pottercraft.ollivanders2.stationaryspell.REPELLO_MUGGLETON(p, player.getUniqueId(), location, O2StationarySpellType.REPELLO_MUGGLETON, radius, duration);
   }
}