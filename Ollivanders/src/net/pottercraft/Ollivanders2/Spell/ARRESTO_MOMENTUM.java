package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Slows down any item or living entity according to your level in the spell.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class ARRESTO_MOMENTUM extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ARRESTO_MOMENTUM ()
   {
      super();

      spellType = O2SpellType.ARRESTO_MOMENTUM;

      flavorText = new ArrayList<String>() {{
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
      setUsesModifier();
   }

   /**
    * Checks for entities or items in a radius around the projectile and slows their velocity, if found
    */
   @Override
   protected void doCheckEffect ()
   {
      double modifier = usesModifier;

      // check for entities first
      List<LivingEntity> entities = getLivingEntities(1.5);
      for (LivingEntity entity : entities)
      {
         if (entity.getUniqueId() == player.getUniqueId())
            continue;

         double speed = entity.getVelocity().length() / (Math.pow(modifier, 2));
         entity.setVelocity(entity.getVelocity().normalize().multiply(speed));
         entity.setFallDistance((float) (entity.getFallDistance() / modifier));
         kill();
         return;
      }

      // check for items next
      List<Item> items = getItems(1.5);
      if (items.size() > 0)
      {
         Item item = items.get(0);

         double speed = item.getVelocity().length() / (Math.pow(modifier, 2));
         item.setVelocity(item.getVelocity().normalize().multiply(speed));
         kill();
         return;
      }

      // if the spell has hit a solid block, the projectile is dead and wont go further so kill the spell
      if (hasHitTarget())
      {
         kill();
      }
   }
}