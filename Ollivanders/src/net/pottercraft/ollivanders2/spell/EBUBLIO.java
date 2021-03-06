package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Bubble head charm gives the player water breathing for a length of time depending on the player's spell level.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class EBUBLIO extends AddO2Effect
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EBUBLIO()
   {
      super();

      spellType = O2SpellType.EBUBLIO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("The Bubble-Head Charm");
         add("Fleur Delacour, though she demonstrated excellent use of the Bubble-Head Charm, was attacked by grindylows as she approached her goal, and failed to retrieve her hostage.");
         add("Cedric Diggory, who also used the Bubble-Head Charm, was first to return with his hostage, though he returned one minute outside the time limit of an hour.");
      }};

      text = "Gives target player the ability to breathe underwater.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public EBUBLIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.EBUBLIO;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      effectsToAdd.add(O2EffectType.WATER_BREATHING);
      strengthModifier = 1;
      minDurationInSeconds = 30;
      targetSelf = true;

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