package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Protego horribilis is the incantation to a protective spell.
 *
 * @version Ollivanders2
 * @author Azami7
 */
public final class PROTEGO_HORRIBILIS extends StationarySpellSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO_HORRIBILIS ()
   {
      super();

      spellType = O2SpellType.PROTEGO_HORRIBILIS;

      flavorText = new ArrayList<String>() {{
         add(" [...] although he could barely see out of it, he pointed his wand through the smashed window and started muttering incantations of great complexity. Harry heard a weird rushing noise, as though Flitwick had unleashed the power of the wind into the grounds.");
      }};

      text = "Protego horribilis is a stationary spell which will destroy any spells crossing it's barrier.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PROTEGO_HORRIBILIS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PROTEGO_HORRIBILIS;
      setUsesModifier();

      baseDurationInSeconds = 30;
      durationModifierInSeconds = 10;
      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
      centerOnCaster = true;
   }

   @Override
   protected StationarySpellObj createStationarySpell ()
   {
      return new net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_HORRIBILIS(p, player.getUniqueId(), location, O2StationarySpellType.PROTEGO_HORRIBILIS, radius, duration);
   }
}