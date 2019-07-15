package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

/**
 * Makes an anti-disapparition spell. Players can't apparate out of it.
 *
 * @author lownes
 */
public final class NULLUM_EVANESCUNT extends StationarySpellSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public NULLUM_EVANESCUNT ()
   {
      super();

      spellType = O2SpellType.NULLUM_EVANESCUNT;
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
      setUsesModifier();

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
      return new net.pottercraft.Ollivanders2.StationarySpell.NULLUM_EVANESCUNT(p, player.getUniqueId(), location, O2StationarySpellType.NULLUM_EVANESCUNT, radius, duration);
   }
}