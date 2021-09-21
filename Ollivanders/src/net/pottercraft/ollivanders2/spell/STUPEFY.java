package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Blinds and slows the target entity for a duration depending on the spell's level.
 *
 * @author lownes
 * @author Azami7
 */
public final class STUPEFY extends AddPotionEffect
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public STUPEFY(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.STUPEFY;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Stunning Spell");
         add("\"Stunning is one of the most useful spells in your arsenal. It's sort of a wizard's bread and butter, really.\" -Harry Potter");
      }};

      text = "Stupefy will stun an opponent for a duration.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public STUPEFY(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.STUPEFY;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      effectTypes.add(PotionEffectType.BLINDNESS);
      effectTypes.add(PotionEffectType.SLOW);
      strengthModifier = (int) usesModifier / 10;
      minDurationInSeconds = 1;
      maxDurationInSeconds = 180;

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