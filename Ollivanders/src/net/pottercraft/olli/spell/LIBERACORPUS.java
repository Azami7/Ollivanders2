package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Reduces the time duration of any levicorpus effects on the target.
 *
 * @author lownes
 * @author Azami7
 */
public final class LIBERACORPUS extends ReduceO2Effect
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LIBERACORPUS()
   {
      super();

      spellType = O2SpellType.LIBERACORPUS;
      branch = O2MagicBranch.COUNTER_SPELL;

      flavorText = new ArrayList<String>()
      {{
         add("The Levicorpus Counter-Spell");
         add("...he jerked his wand upwards; Snape fell into a crumpled heap on the ground.");
      }};

      text = "Liberacorpus will reduce the time left on any levicorpus effects on the target by an amount determined by your experience.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LIBERACORPUS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LIBERACORPUS;
      branch = O2MagicBranch.COUNTER_SPELL;

      initSpell();

      effectsToReduce.add(O2EffectType.SUSPENSION);
   }
}