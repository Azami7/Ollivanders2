package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Eliminates all fall damage.
 *
 * @author lownes
 * @author Azami7
 */
public final class MOLLIARE extends StationarySpell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MOLLIARE()
   {
      super();

      spellType = O2SpellType.MOLLIARE;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("The Cushioning Charm.");
         add("Harry felt himself glide back toward the ground as though weightless, landing painlessly on the rocky passage floor.");
      }};

      text = "Molliare softens the ground in a radius around the site.  All fall damage will be negated in this radius.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MOLLIARE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.MOLLIARE;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      baseDurationInSeconds = 30;
      durationModifierInSeconds = 1;
      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
   }

   @Override
   protected O2StationarySpell createStationarySpell ()
   {
      return new net.pottercraft.ollivanders2.stationaryspell.MOLLIARE(p, player.getUniqueId(), location, O2StationarySpellType.MOLLIARE, radius, duration);
   }
}