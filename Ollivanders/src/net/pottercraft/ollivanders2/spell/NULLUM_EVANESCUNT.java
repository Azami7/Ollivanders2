package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;

/**
 * Makes an anti-disapparition spell. Players can't apparate out of it.
 *
 * @author lownes
 */
public final class NULLUM_EVANESCUNT extends StationarySpell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public NULLUM_EVANESCUNT()
   {
      super();

      spellType = O2SpellType.NULLUM_EVANESCUNT;
      branch = O2MagicBranch.CHARMS;

      text = "Nullum evanescunt creates a stationary spell which will not allow disapparition out of it.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public NULLUM_EVANESCUNT (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.NULLUM_EVANESCUNT;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      baseDurationInSeconds = 300;
      durationModifierInSeconds = 15;
      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
      centerOnCaster = true;
   }

   @Override
   protected StationarySpellObj createStationarySpell ()
   {
      return new net.pottercraft.ollivanders2.stationaryspell.NULLUM_EVANESCUNT(p, player.getUniqueId(), location, O2StationarySpellType.NULLUM_EVANESCUNT, radius, duration);
   }
}