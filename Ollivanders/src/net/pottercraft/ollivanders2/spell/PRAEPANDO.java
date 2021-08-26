package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.jetbrains.annotations.NotNull;

/**
 * Create a pocket of extra-dimensional space.
 *
 * @author Azami7
 * @author cakenggt
 */
public final class PRAEPANDO extends StationarySpell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PRAEPANDO()
   {
      super();

      spellType = O2SpellType.PRAEPANDO;
      branch = O2MagicBranch.CHARMS;

      text = "Praepando is a space-extension spell which allows you to create a pocket of extra-dimensional space at a location. "
            + "Spells can travel from the extra-dimensional pocket through to the real-world, but cannot go the other way around.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PRAEPANDO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PRAEPANDO;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
      centerOnCaster = true;

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.BUILD);
   }

   @Override
   protected O2StationarySpell createStationarySpell ()
   {
      return new net.pottercraft.ollivanders2.stationaryspell.PRAEPANDO(p, player.getUniqueId(), location, O2StationarySpellType.PRAEPANDO, 1, duration, radius);
   }
}