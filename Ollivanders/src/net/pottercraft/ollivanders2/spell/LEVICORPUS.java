package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Puts a levicorpus effect on the player
 *
 * @author lownes
 * @author Azami7
 * @see <a href = "https://harrypotter.fandom.com/wiki/Levitation_Charm">https://harrypotter.fandom.com/wiki/Levitation_Charm</a>
 */
public final class LEVICORPUS extends AddO2Effect
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public LEVICORPUS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.LEVICORPUS;
      branch = O2MagicBranch.DARK_ARTS;

      flavorText = new ArrayList<>()
      {{
         add("\"Oh, that one had a great vogue during my time at Hogwarts. There were a few months in my fifth year when you couldn't move for being hoisted into the air by your ankle.\" -Remus Lupin");
         add("Pointing his wand at nothing in particular, he gave it an upward flick and said Levicorpus! inside his head... There was a flash of light... Ron was dangling upside down in midair as though an invisible hook had hoisted him up by the ankle.");
         add("The Suspension Jinx");
      }};

      text = "Hoist a player up into the air for a duration.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LEVICORPUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LEVICORPUS;
      branch = O2MagicBranch.DARK_ARTS;

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.PVP);

      effectsToAdd.add(O2EffectType.SUSPENSION);

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      maxDurationInSeconds = 180; // 3 minutes
      durationInSeconds = ((int) usesModifier + 30);
   }
}