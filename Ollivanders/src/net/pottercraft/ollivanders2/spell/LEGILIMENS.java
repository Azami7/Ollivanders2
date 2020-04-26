package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.Ollivanders2Common;
import net.pottercraft.ollivanders2.player.O2Player;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Open the target LivingEntity's inventory
 *
 * @author lownes
 * @author Azami7
 */
public final class LEGILIMENS extends DarkArts
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LEGILIMENS ()
   {
      super();

      spellType = O2SpellType.LEGILIMENS;

      flavorText = new ArrayList<String>() {{
         add("\"The mind is not a book, to be opened at will and examined at leisure. Thoughts are not etched on the inside of skulls, to be perused by any invader. The mind is a complex and many-layered thing, Potter. Or at least most minds are... It is true, however, that those who have mastered Legilimency are able, under certain conditions, to delve into the minds of their victims and to interpret their findings correctly.\" -Severus Snape");
         add("The Legilimency Spell");
      }};

      text = "Legilimens, when cast at a player, will reveal certain information about the player. Your success depends both on your level of experience with Legilimens and the target player's experience.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LEGILIMENS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.LEGILIMENS;
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

            Player target = (Player) live;

            int targetExperience = p.getO2Player(target).getSpellCount(O2SpellType.LEGILIMENS) / 10;

            int randLess = (Math.abs(Ollivanders2Common.random.nextInt()) % 10);
            int randEqual = (Math.abs(Ollivanders2Common.random.nextInt()) % 3);
            int randGreater = (Math.abs(Ollivanders2Common.random.nextInt()) % 5);

            // Legilimens will be successful:
            // 80% of the time if the caster's legilimens level is greater than the target's
            // 33% of the time if the caster and target have the same legilimens level
            // 10% of the time if the caster's legilimens level is less than the target
            if (((usesModifier > targetExperience) && (randGreater < 4)) // success on 3, 2, 1, 0
                  || ((usesModifier == targetExperience) && (randEqual > 0)) // success on 1, 2
                  || ((usesModifier < targetExperience) && (randLess < 1))) // success on 0
            {
               if (Ollivanders2API.getPlayers().playerEffects.hasEffect(target.getUniqueId(), O2EffectType.ANIMAGUS_EFFECT))
               {
                  p.getLogger().info("Legilimens: target is in animagus form");
                  p.getLogger().info("Uses modifier = " + usesModifier);

                  // when in animagus form, only someone who has mastered legilimens can mind read a person
                  if (usesModifier >= 10)
                  {
                     int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 100);

                     p.getLogger().info("rand = " + rand);
                     // 10% chance to detect animagus
                     if (rand < 90)
                     {
                        kill();
                        return;
                     }
                  }
                  else
                  {
                     kill();
                     return;
                  }
               }

               readMind(target);
            }
            else
            {
               player.sendMessage(Ollivanders2.chatColor + target.getName() + " resists your mind.");
            }

            kill();
            return;
         }
      }
   }

   /**
    * Read a target player's mind
    *
    * @param target the player to mind read
    */
   private void readMind (Player target)
   {
      O2Player o2p = p.getO2Player(target);

      player.sendMessage(Ollivanders2.chatColor + "You search in to " + o2p.getPlayerName() + "'s mind ...");

      // detect if they are a muggle
      if (o2p.isMuggle())
      {
         player.sendMessage(Ollivanders2.chatColor + " is a muggle.");
      }
      else
      {
         player.sendMessage(Ollivanders2.chatColor + " is a witch/wizard.");
      }

      // detect house and year
      if (Ollivanders2.useHouses)
      {
         StringBuilder message = new StringBuilder();
         message.append(Ollivanders2.chatColor);

         if (Ollivanders2API.getHouses().isSorted(target))
         {
            message.append(" is a ");

            if (Ollivanders2.useYears)
            {
               message.append(o2p.getYear().getDisplayText()).append(" year ");
            }

            message.append(Ollivanders2API.getHouses().getHouse(target).getName()).append(".");
         }
         else
         {
            message.append(" has not started school yet.");
         }

         player.sendMessage(message.toString());
      }

      int rand = (Math.abs(Ollivanders2Common.random.nextInt()) % 100);

      // 50% chance detect destined wand
      if (rand >= 50)
      {
         if (o2p.foundWand())
         {
            player.sendMessage(Ollivanders2.chatColor + " uses a " + o2p.getWandWood() + " and " + o2p.getWandCore() + " wand.");
         }
         else
         {
            player.sendMessage(Ollivanders2.chatColor + " has not gotten a wand.");
         }
      }

      // information beyond this depends on legilimens level
      if (usesModifier > 2)
      {
         // 50% chance detect recent spell cast
         if (rand >= 50)
         {
            O2SpellType lastSpell = o2p.getLastSpell();
            if (lastSpell != null)
            {
               player.sendMessage(Ollivanders2.chatColor + " last cast " + lastSpell.getSpellName() + ".");
            }

         }

         if (usesModifier > 3)
         {
            // 33% chance detect mastered spell
            if (rand >= 66)
            {
               if (Ollivanders2.enableNonVerbalSpellCasting)
               {
                  O2SpellType masteredSpell = o2p.getMasterSpell();
                  if (masteredSpell != null)
                  {
                     player.sendMessage(Ollivanders2.chatColor + " can non-verbally cast the spell " + masteredSpell.getSpellName() + ".");
                  }
               }
            }

            if (usesModifier > 5)
            {
               // 40% chance detect effects
               if (rand >= 40)
               {
                  String legilText = Ollivanders2API.getPlayers().playerEffects.detectEffectWithLegilimens(o2p.getID());
                  if (legilText != null)
                  {
                     player.sendMessage(Ollivanders2.chatColor + " " + legilText + ".");
                  }
               }

               if (usesModifier >= 10)
               {
                  // 10% chance detect is animagus
                  if (rand >= 90)
                  {
                     if (o2p.isAnimagus())
                     {
                        player.sendMessage(Ollivanders2.chatColor + " is an animagus.");

                        EntityType animagusForm = o2p.getAnimagusForm();
                        if (animagusForm != null)
                        {
                           player.sendMessage(Ollivanders2.chatColor + " has the animagus form of a " + Ollivanders2API.common.enumRecode(animagusForm.toString()) + ".");
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
