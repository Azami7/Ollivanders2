package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.StationarySpell.StationarySpellObj;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.StationarySpell.O2StationarySpellType;

import java.util.ArrayList;

/**
 * Eliminates all fall damage.
 *
 * @author lownes
 * @author Azami7
 */
public final class MOLLIARE extends StationarySpellSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MOLLIARE ()
   {
      super();

      spellType = O2SpellType.MOLLIARE;

      flavorText = new ArrayList<String>() {{
         add("The Cushioning Charm.");
         add("Harry felt himself glide back toward the ground as though weightless, landing painlessly on the rocky passage floor.");
      }};

      text = "Molliare softens the ground in a radius around the site.  All fall damage will be negated in this radius.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MOLLIARE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.MOLLIARE;
      setUsesModifier();

      baseDurationInSeconds = 30;
      durationModifierInSeconds = 1;
      baseRadius = 5;
      radiusModifier = 1;
      flairSize = 10;
   }

   @Override
   protected StationarySpellObj createStationarySpell ()
   {
      return new net.pottercraft.Ollivanders2.StationarySpell.MOLLIARE(p, player.getUniqueId(), location, O2StationarySpellType.MOLLIARE, radius, duration);
   }
}