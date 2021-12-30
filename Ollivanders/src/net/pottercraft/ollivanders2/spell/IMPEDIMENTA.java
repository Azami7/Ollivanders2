package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Slows any living entity by an amount and time depending on the player's spell level.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class IMPEDIMENTA extends AddPotionEffect
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public IMPEDIMENTA(Ollivanders2 plugin)
   {
      super(plugin);

      branch = O2MagicBranch.DARK_ARTS;
      spellType = O2SpellType.IMPEDIMENTA;

      flavorText = new ArrayList<>()
      {{
         add("Swift use of this jinx can freeze an attacker for a few moments, or stop a magical beast in its tracks. The jinx is a vital part of any duellist’s arsenal.");
         add("\"I like the look of this one, this Impediment Jinx. Should slow down anything that’s trying to attack you, Harry. We’ll start with that one.\" -Hermione Granger");
      }};

      text = "Slows a living entity's movements.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public IMPEDIMENTA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.DARK_ARTS;
      spellType = O2SpellType.IMPEDIMENTA;

      effectTypes.add(PotionEffectType.SLOW);

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      // Amplifier
      maxAmplifier = 4;

      amplifier = (int)(usesModifier / 50);
      if (amplifier > maxAmplifier)
         amplifier = maxAmplifier;

      // Duration
      minDurationInSeconds = 5;
      maxDurationInSeconds = 60;

      durationInSeconds = (int)(usesModifier / 2);
      if (durationInSeconds < minDurationInSeconds)
         durationInSeconds = minDurationInSeconds;
      else if (durationInSeconds > maxDurationInSeconds)
         durationInSeconds = maxDurationInSeconds;
   }
}