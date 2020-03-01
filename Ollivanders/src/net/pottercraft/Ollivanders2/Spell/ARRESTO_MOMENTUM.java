package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Slows down any item or living entity according to your level in the spell.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class ARRESTO_MOMENTUM extends O2Spell
{
   double multiplier = 0.5;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ARRESTO_MOMENTUM()
   {
      super();

      spellType = O2SpellType.ARRESTO_MOMENTUM;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>()
      {{
         add("An incantation for slowing velocity.");
         add("\"Dumbledore ...ran onto the field as you fell, waved his wand, and you sort of slowed down before you hit the ground.\" - Hermione Granger");
         add("The witch Daisy Pennifold had the idea of bewitching the Quaffle so that if dropped, it would fall slowly earthwards as though sinking through water, meaning that Chasers could grab it in mid-air.");
      }};

      text = "Arresto Momentum will immediately slow down any entity or item.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ARRESTO_MOMENTUM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.ARRESTO_MOMENTUM;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   @Override
   void doInitSpell ()
   {
      if (usesModifier > 100)
      {
         multiplier = 0;
      }
      else if (multiplier > 75)
      {
         multiplier = 0.2;
      }
      else if (usesModifier > 50)
      {
         multiplier = 0.3;
      }
      else if (usesModifier > 25)
      {
         multiplier = 0.4;
      }
      else
      {
         multiplier = 0.5;
      }
   }

   /**
    * Checks for entities or items in a radius around the projectile and slows their velocity, if found
    */
   @Override
   protected void doCheckEffect ()
   {
      double modifier = usesModifier;

      // check for entities first
      List<Entity> entities = getCloseEntities(1.5);

      if (entities.size() > 0)
      {
         for (Entity entity : entities)
         {
            if (entity.getUniqueId() == player.getUniqueId())
               continue;

            if (Ollivanders2.debug)
            {
               p.getLogger().info("current speed = " + entity.getVelocity().length());
            }

            entity.setVelocity(entity.getVelocity().multiply(multiplier));

            if (Ollivanders2.debug)
            {
               p.getLogger().info("new speed = " + entity.getVelocity().length());
            }

            kill();
            return;
         }

         return;
      }

      // if the spell has hit a solid block, the projectile is dead and wont go further so kill the spell
      if (hasHitTarget())
         kill();
   }
}