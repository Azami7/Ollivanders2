package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.StationarySpellObj;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Creates a PROTEGO_TOTALUM Stationary Spell Object
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class PROTEGO_TOTALUM extends StationarySpell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROTEGO_TOTALUM()
   {
      super();

      spellType = O2SpellType.PROTEGO_TOTALUM;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("Raising her wand, she began to walk in a wide circle around Harry and Ron, murmuring incantations as she went. Harry saw little disturbances in the surrounding air: it was as if Hermione had cast a heat haze across their clearing.");
      }};

      text = "Protego totalum is a stationary spell which will prevent any entities from crossing it's boundary.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PROTEGO_TOTALUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PROTEGO_TOTALUM;
      branch = O2MagicBranch.CHARMS;
      initSpell();

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
      return new net.pottercraft.ollivanders2.stationaryspell.PROTEGO_TOTALUM(p, player.getUniqueId(), location, O2StationarySpellType.PROTEGO_TOTALUM, radius, duration);
   }
}