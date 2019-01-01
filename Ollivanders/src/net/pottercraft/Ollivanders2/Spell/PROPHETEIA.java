package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Reveal an unfulfilled prophecy about a player.
 *
 * @author Azami7
 * @since 2.2.9
 */
public class PROPHETEIA extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PROPHETEIA ()
   {
      super();

      branch = O2MagicBranch.DIVINATION;
      spellType = O2SpellType.PROPHETEIA;

      flavorText = new ArrayList<String>()
      {{
         add("\"But when Sybill Trelawney spoke, it was not in her usual ethereal, mystic voice, but in the hard, hoarse tones Harry had heard her use once before.\"");
      }};

      text = "Propheteia allows one to reveal amn unfulfilled prophecy that has been made about a target player. Chances of success depend on experience.";
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
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();

      for (LivingEntity live : getLivingEntities(1.5))
      {
         if (live instanceof Player && live.getUniqueId() != player.getUniqueId())
         {
            int rand = (Math.abs(Ollivanders2.random.nextInt()) % 10);

            if (usesModifier > rand)
            {
               String prophecy = p.prophecies.getProphecy(live.getUniqueId());

               if (prophecy != null)
               {
                  player.sendMessage(Ollivanders2.chatColor + prophecy);
                  kill();
                  return;
               }
            }
         }

         player.sendMessage(Ollivanders2.chatColor + "You do not discover anything.");

         kill();
         return;
      }
   }
}
