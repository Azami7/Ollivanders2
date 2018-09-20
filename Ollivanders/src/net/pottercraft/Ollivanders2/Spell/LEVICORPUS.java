package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.*;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
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
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(2))
      {
         if (live instanceof Player)
         {
            net.pottercraft.Ollivanders2.Effect.LEVICORPUS levi = new net.pottercraft.Ollivanders2.Effect.LEVICORPUS(p, (int)(usesModifier * 1200.0), live.getUniqueId());

            p.players.playerEffects.addEffect(levi);

            kill();
            return;
         }
      }
   }
}