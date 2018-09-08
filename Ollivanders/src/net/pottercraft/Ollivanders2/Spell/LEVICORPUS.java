package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.*;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Player.O2Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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

      flavorText.add("\"Oh, that one had a great vogue during my time at Hogwarts. There were a few months in my fifth year when you couldn't move for being hoisted into the air by your ankle.\" -Remus Lupin");
      flavorText.add("Pointing his wand at nothing in particular, he gave it an upward flick and said Levicorpus! inside his head... There was a flash of light... Ron was dangling upside down in midair as though an invisible hook had hoisted him up by the ankle.");
      flavorText.add("The Suspension Jinx");

      text = "Hoist a player up into the air for a duration.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public LEVICORPUS (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(2))
      {
         if (live instanceof Player)
         {
            O2Player o2p = p.getO2Player((Player) live);
            net.pottercraft.Ollivanders2.Effect.LEVICORPUS levi = new net.pottercraft.Ollivanders2.Effect.LEVICORPUS(p, O2EffectType.LEVICORPUS,
                  (int) (usesModifier * 1200.0), (Player)live);
            o2p.addEffect(levi);
            kill();
            return;
         }
      }
   }
}
