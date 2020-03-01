package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

/**
 * Gives a target blindness
 *
 * @author lownes
 * @author Azami7
 */
public final class OBSCURO extends AddPotionEffect
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public OBSCURO()
   {
      super();

      spellType = O2SpellType.OBSCURO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("A black blindfold appeared over Phineas Nigellus' clever, dark eyes, causing him to bump into the frame and shriek with pain.");
      }};

      text = "Obscuro will blind the target.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public OBSCURO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.OBSCURO;
      branch = O2MagicBranch.CHARMS;
      initSpell();

      effectTypes.add(PotionEffectType.BLINDNESS);
      strengthModifier = 0;
      minDurationInSeconds = 30;

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
