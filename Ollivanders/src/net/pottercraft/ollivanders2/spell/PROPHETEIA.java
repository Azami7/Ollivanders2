package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Reveal an unfulfilled prophecy about a player.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class PROPHETEIA extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROPHETEIA()
   {
      super();

      branch = O2MagicBranch.DIVINATION;
      spellType = O2SpellType.PROPHETEIA;

      flavorText = new ArrayList<String>()
      {{
         add("\"But when Sybill Trelawney spoke, it was not in her usual ethereal, mystic voice, but in the hard, hoarse tones Harry had heard her use once before.\"");
      }};

      text = "Propheteia allows one to reveal an unfulfilled prophecy that has been made about a target player. Chances of success depend on experience.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PROPHETEIA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.DIVINATION;
      spellType = O2SpellType.PROPHETEIA;
      initSpell();
   }

   @Override
   protected void doCheckEffect ()
   {
      for (LivingEntity livingEntity : getLivingEntities(1.5))
      {
         if (livingEntity.getUniqueId() == player.getUniqueId())
         {
            continue;
         }

         if (!(livingEntity instanceof Player))
         {
            continue;
         }

         int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 10);

         if (usesModifier > rand)
         {
            String prophecy = Ollivanders2API.getProphecies(p).getProphecy(livingEntity.getUniqueId());

            if (prophecy != null)
            {
               player.sendMessage(Ollivanders2.chatColor + prophecy);
               kill();
               return;
            }
         }

         player.sendMessage(Ollivanders2.chatColor + "You do not discover anything.");

         kill();
         return;
      }

      if (hasHitTarget())
      {
         kill();
      }
   }
}
