package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.*;
import net.pottercraft.Ollivanders2.Effect.O2EffectType;
import net.pottercraft.Ollivanders2.Effect.O2Effect;
import net.pottercraft.Ollivanders2.Player.O2Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Reduces the time duration of any levicorpus effects on the target
 *
 * @author lownes
 * @author Azami7
 */
public final class LIBERACORPUS extends Charms
{
   public O2SpellType spellType = O2SpellType.LIBERACORPUS;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Levicorpus Counter-Spell");
      add("...he jerked his wand upwards; Snape fell into a crumpled heap on the ground.");
   }};

   protected String text = "Liberacorpus will reduce the time left on any levicorpus effects on the target by an amount determined by your experience.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LIBERACORPUS () { }

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
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(2))
      {
         if (live instanceof Player)
         {
            Player player = (Player) live;

            if (p.players.playerEffects.hasEffect(player.getUniqueId(), O2EffectType.LEVICORPUS))
            {
               p.players.playerEffects.ageEffect(player.getUniqueId(), O2EffectType.LEVICORPUS, (int)(usesModifier * 2400));
            }

            kill();
            return;
         }
      }
   }
}