package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

/**
 * Create a pocket of extra-dimensional space.
 *
 * @author Azami7
 * @author cakenggt
 */
public final class PRAEPANDO extends StationarySpellSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PRAEPANDO ()
   {
      super();

      spellType = O2SpellType.PRAEPANDO;

      text = "Praepando is a space-extension spell which allows you to create a pocket of extra-dimensional space at a location. "
            + "Spells can travel from the extra-dimensional pocket through to the real-world, but cannot go the other way around.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PRAEPANDO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PRAEPANDO;
      setUsesModifier();

      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
      centerOnCaster = true;

      // world guard flags
      worldGuardFlags.add(DefaultFlag.BUILD);
   }

   @Override
   protected StationarySpellObj createStationarySpell ()
   {
      return new net.pottercraft.Ollivanders2.StationarySpell.PRAEPANDO(p, player.getUniqueId(), location, O2StationarySpellType.PRAEPANDO, 1, duration, radius);
   }
}
