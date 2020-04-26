package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2API;
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
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LIBERACORPUS ()
   {
      super();

      spellType = O2SpellType.LIBERACORPUS;

      flavorText = new ArrayList<String>() {{
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
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      for (LivingEntity live : getLivingEntities(1.5))
      {
         if (live instanceof Player)
         {
            if (live.getUniqueId() == player.getUniqueId())
               continue;

            Player player = (Player) live;

            if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.SUSPENSION))
            {
               Ollivanders2API.getPlayers().playerEffects.ageEffect(player.getUniqueId(), O2EffectType.SUSPENSION, (int) (usesModifier * 2400));
            }

            kill();
            return;
         }
      }
   }
}