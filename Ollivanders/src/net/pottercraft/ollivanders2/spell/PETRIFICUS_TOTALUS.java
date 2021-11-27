package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.IMMOBILIZE;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Full Body-Bind Curse - Used to temporarily bind the victim's body in a position much like that of a soldier at attention.
 * http://harrypotter.wikia.com/wiki/Full_Body-Bind_Curse
 *
 * @author Azami7
 * @since 2.2.9
 */
public class PETRIFICUS_TOTALUS extends O2Spell
{
   private final static int maxDurationInSeconds = 300;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public PETRIFICUS_TOTALUS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.PETRIFICUS_TOTALUS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Full Body-Bind Curse");
         add("\"Neville's arms snapped to his sides. His legs sprang together. His whole body rigid, he swayed where he stood and then fell flat on his face, stiff as a board. Neville's jaws were jammed together so he couldn't speak. Only his eyes were moving, looking at them in horror.\"");
         add("\"Harry's body became instantly rigid and immobile, and he felt himself fall back against the tower wall, propped like an unsteady statue.\"");
      }};

      text = "Temporarily paralyzes a person.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PETRIFICUS_TOTALUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PETRIFICUS_TOTALUS;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   @Override
   protected void doCheckEffect()
   {
      for (LivingEntity live : getLivingEntities(1.5))
      {
         if (live.getUniqueId() == player.getUniqueId())
         {
            continue;
         }

         if (live instanceof Player)
         {
            int durationInSeconds = ((int) usesModifier + 30);
            if (durationInSeconds > maxDurationInSeconds)
            {
               durationInSeconds = maxDurationInSeconds;
            }

            IMMOBILIZE immobilize = new IMMOBILIZE(p, durationInSeconds * Ollivanders2Common.ticksPerSecond, live.getUniqueId());

            Ollivanders2API.getPlayers().playerEffects.addEffect(immobilize);

            kill();
            return;
         }
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
      {
         kill();
      }
   }
}
