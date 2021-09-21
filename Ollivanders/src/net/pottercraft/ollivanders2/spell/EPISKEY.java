package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Gives an entity a healing effect for usesModifier seconds
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class EPISKEY extends AddPotionEffect
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public EPISKEY(Ollivanders2 plugin)
   {
      super(plugin);

      branch = O2MagicBranch.HEALING;
      spellType = O2SpellType.EPISKEY;

      flavorText = new ArrayList<>()
      {{
         add("\"Episkey,\" said Tonks. Harry's nose felt very hot, then very cold. He raised a hand and felt it gingerly. It seemed to be mended.");
         add("A minor healing spell.");
      }};

      text = "Episkey will heal minor injuries.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public EPISKEY(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.HEALING;
      spellType = O2SpellType.EPISKEY;

      initSpell();

      effectTypes.add(PotionEffectType.REGENERATION);
      strengthModifier = 0;
      minDurationInSeconds = 15;
      maxDurationInSeconds = 120;

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