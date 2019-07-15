package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Effect.SUSPENSION;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Puts a levicorpus effect on the player
 *
 * @author lownes
 * @author Azami7
 */
public final class LEVICORPUS extends DarkArts
{
   private static int maxDurationInSeconds = 300;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LEVICORPUS ()
   {
      super();

      spellType = O2SpellType.LEVICORPUS;

      flavorText = new ArrayList<String>() {{
         add("\"Oh, that one had a great vogue during my time at Hogwarts. There were a few months in my fifth year when you couldn't move for being hoisted into the air by your ankle.\" -Remus Lupin");
         add("Pointing his wand at nothing in particular, he gave it an upward flick and said Levicorpus! inside his head... There was a flash of light... Ron was dangling upside down in midair as though an invisible hook had hoisted him up by the ankle.");
         add("The Suspension Jinx");
      }};

      text = "Hoist a player up into the air for a duration.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LEVICORPUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LEVICORPUS;
      setUsesModifier();

      // world guard flags
      worldGuardFlags.add(DefaultFlag.PVP);
   }

   @Override
   protected void doCheckEffect ()
   {
      for (LivingEntity live : getLivingEntities(1.5))
      {
         if (live.getUniqueId() == player.getUniqueId())
            continue;

         if (live instanceof Player)
         {
            int durationInSeconds = ((int) usesModifier + 30);
            if (durationInSeconds > maxDurationInSeconds)
            {
               durationInSeconds = maxDurationInSeconds;
            }

            SUSPENSION levi = new SUSPENSION(p, durationInSeconds * Ollivanders2Common.ticksPerSecond, live.getUniqueId());

            Ollivanders2API.getPlayers().playerEffects.addEffect(levi);

            kill();
            return;
         }
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }
}