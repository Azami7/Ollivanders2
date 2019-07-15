package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Slows any living entity by an amount and time depending on the player's spell level.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class IMPEDIMENTA extends PotionEffectSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public IMPEDIMENTA ()
   {
      super();

      branch = O2MagicBranch.DARK_ARTS;
      spellType = O2SpellType.IMPEDIMENTA;

      flavorText = new ArrayList<String>() {{
         add("Swift use of this jinx can freeze an attacker for a few moments, or stop a magical beast in its tracks. The jinx is a vital part of any duellist’s arsenal.");
         add("\"I like the look of this one, this Impediment Jinx. Should slow down anything that’s trying to attack you, Harry. We’ll start with that one.\" -Hermione Granger");
      }};

      text = "Slows target movements.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public IMPEDIMENTA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.DARK_ARTS;
      spellType = O2SpellType.IMPEDIMENTA;
      setUsesModifier();

      effectTypes.add(PotionEffectType.SLOW);
      strengthModifier = 0;
      minDurationInSeconds = 15;
      maxDurationInSeconds = 60;

      durationInSeconds = (int) (usesModifier / 2);
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