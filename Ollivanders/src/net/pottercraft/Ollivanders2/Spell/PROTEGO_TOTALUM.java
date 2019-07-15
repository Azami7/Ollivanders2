package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Creates a PROTEGO_TOTALUM Stationary Spell Object
 *
 * @version Ollivanders2
 * @author Azami7
 */
public final class PROTEGO_TOTALUM extends StationarySpellSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO_TOTALUM ()
   {
      super();

      spellType = O2SpellType.PROTEGO_TOTALUM;

      flavorText = new ArrayList<String>() {{
         add("Raising her wand, she began to walk in a wide circle around Harry and Ron, murmuring incantations as she went. Harry saw little disturbances in the surrounding air: it was as if Hermione had cast a heat haze across their clearing.");
      }};

      text = "Protego totalum is a stationary spell which will prevent any entities from crossing it's boundary.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PROTEGO_TOTALUM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PROTEGO_TOTALUM;
      setUsesModifier();

      baseDurationInSeconds = 300;
      durationModifierInSeconds = 10;
      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
      centerOnCaster = true;
   }

   @Override
   protected StationarySpellObj createStationarySpell ()
   {
      return new net.pottercraft.Ollivanders2.StationarySpell.PROTEGO_TOTALUM(p, player.getUniqueId(), location, O2StationarySpellType.PROTEGO_TOTALUM, radius, duration);
   }
}