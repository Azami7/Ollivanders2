package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Immobilizes a player for an amount of time depending on the player's spell level.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class IMMOBULUS extends PotionEffectSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public IMMOBULUS ()
   {
      super();

      spellType = O2SpellType.IMMOBULUS;

      flavorText = new ArrayList<String>() {{
         add("The Freezing Charm");
         add("\"[…] immobilising two pixies at once with a clever Freezing Charm and stuffing them back into their cage.\"");
         add("The Freezing Charm is a spell which immobilises living targets.");
      }};

      text = "Slows entity movement for a time period.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public IMMOBULUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.IMMOBULUS;
      setUsesModifier();

      effectTypes.add(PotionEffectType.SLOW);
      effectTypes.add(PotionEffectType.SLOW_FALLING);
      strengthModifier = 10;
      minDurationInSeconds = 10;

      durationInSeconds = (int) usesModifier;
      if (durationInSeconds < minDurationInSeconds)
      {
         durationInSeconds = minDurationInSeconds;
      }
      else if (durationInSeconds > maxDurationInSeconds)
      {
         durationInSeconds = maxDurationInSeconds;
      }
   }
}