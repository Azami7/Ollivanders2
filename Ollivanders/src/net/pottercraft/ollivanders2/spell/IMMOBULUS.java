package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Immobilizes a player for an amount of time depending on the player's spell level.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class IMMOBULUS extends AddPotionEffect
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public IMMOBULUS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.IMMOBULUS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Freezing Charm");
         add("\"[…] immobilising two pixies at once with a clever Freezing Charm and stuffing them back into their cage.\"");
         add("The Freezing Charm is a spell which immobilises living targets.");
      }};

      text = "Slows entity movement for a time period.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public IMMOBULUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.IMMOBULUS;
      branch = O2MagicBranch.CHARMS;

      initSpell();

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