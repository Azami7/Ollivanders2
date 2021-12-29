package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Casts a more powerful confusion potion effect that is twice as strong and lasts twice as long as Confundus.
 *
 * @since 2.2.5
 * @link https://github.com/Azami7/Ollivanders2/issues/95
 * @see ConfundusSuper
 * @author Azami7
 */
public final class CONFUNDUS_DUO extends ConfundusSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public CONFUNDUS_DUO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.CONFUNDUS_DUO;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>() {{
         add("The Confundus Charm");
         add("The guard was confused. He stared down at the thin, golden Probe and then at his companion, who said in a slightly dazed voice, 'Yeah, you've just checked them, Marius.'");
      }};

      text = "Confundus Duo is a stronger variation of the Confundus Charm. Effects are twice as strong and last twice as long as Confundo.";

   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public CONFUNDUS_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.CONFUNDUS_DUO;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      // Amplifier
      amplifier = 1;

      // Duration
      minDurationInSeconds = 30;
      maxDurationInSeconds = 240;

      durationInSeconds = (int)(usesModifier / 2);
      if (durationInSeconds < minDurationInSeconds)
         durationInSeconds = minDurationInSeconds;
      else if (durationInSeconds > maxDurationInSeconds)
         durationInSeconds = maxDurationInSeconds;
   }
}
