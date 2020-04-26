package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Prior Incantato is a spell that forced a wand to show an "echo" of the most recent spell it had performed.
 * http://harrypotter.wikia.com/wiki/Reverse_Spell
 *
 * @author Azami7
 * @since 2.2.9
 */
public class PRIOR_INCANTATO extends Charms
{
   public PRIOR_INCANTATO ()
   {
      super();

      spellType = O2SpellType.PRIOR_INCANTATO;

      flavorText = new ArrayList<String>()
      {{
         add("The Reverse Spell");
         add("\"Placing his wand tip to tip against Harry's wand and saying the spell, Amos causes a shadow of the Dark Mark to erupt from where the two wands meet, showing that this was the last spell cast with Harry's wand.\"");
      }};

      text = "Force a player's wand to reveal the last spell cast. Your success depends on your experience with this spell.";
   }

   public PRIOR_INCANTATO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.INFORMOUS;
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
            {
               continue;
            }

            int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 10);

            if (usesModifier > rand)
            {
               doPriorIncanto((Player) live);
            }
            else
            {
               player.sendMessage(Ollivanders2.chatColor + ((Player) live).getName() + "'s wand resists your spell.");
            }

            kill();
            return;
         }
      }
   }

   /**
    * Show the prior incantation for the target's wand.
    *
    * @param target the target player
    */
   private void doPriorIncanto (Player target)
   {
      O2Player o2p = p.getO2Player(target);

      O2SpellType prior = o2p.getPriorIncantatem();

      if (prior == null)
      {
         player.sendMessage(Ollivanders2.chatColor + target.getName() + "'s wand has not cast a spell.");

         return;
      }

      List<Entity> nearbyPlayers = Ollivanders2API.common.getTypedCloseEntities(target.getLocation(), 20, EntityType.PLAYER);

      for (Entity entity : nearbyPlayers)
      {
         if (!(entity instanceof Player) || entity.getUniqueId() == target.getUniqueId())
         {
            continue;
         }

         entity.sendMessage(Ollivanders2.chatColor + "The shadowy echo of the spell " + prior.getSpellName() + " emits from " + target.getName() + "'s wand.");
      }

      target.sendMessage(Ollivanders2.chatColor + "The shadowy echo of the spell " + prior.getSpellName() + " emits from your wand.");
   }
}
