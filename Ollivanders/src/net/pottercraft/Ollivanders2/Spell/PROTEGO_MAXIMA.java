package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Makes a spell projectile that creates a shield that hurts any entities within 0.5 meters of the spell wall.
 *
 * @version Ollivanders2
 * @author Azami7
 */
public final class PROTEGO_MAXIMA extends StationarySpellSuper
{
   double damage;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO_MAXIMA ()
   {
      super();

      spellType = O2SpellType.PROTEGO_MAXIMA;

      flavorText = new ArrayList<String>() {{
         add("\"Protego Maxima. Fianto Duri. Repello Inimicum.\" -Filius Flitwick");
         add("A Stronger Shield Charm");
      }};

      text = "Protego maxima is a stationary spell which will hurt any entities close to it's boundary.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PROTEGO_MAXIMA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PROTEGO_MAXIMA;
      setUsesModifier();

      baseDurationInSeconds = 300;
      durationModifierInSeconds = 10;
      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
      centerOnCaster = true;

      damage = (usesModifier / 10) + 1;
   }

   @Override
   protected StationarySpellObj createStationarySpell ()
   {
      return new net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_MAXIMA(p, player.getUniqueId(), location, O2StationarySpellType.PROTEGO_MAXIMA, radius, duration, damage);
   }
}