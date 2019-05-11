package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Confundus Charm super class which causes confusion in the target
 *
 * @author Azami7
 * @author lownes
 */
public abstract class ConfundusSuper extends Charms
{
   int strengthModifier = 0;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   ConfundusSuper ()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   ConfundusSuper (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   /**
    * Look for living entities in the projectile's location and add confusion effect to them
    */
   @Override
   protected void doCheckEffect ()
   {
      List<LivingEntity> entities = getLivingEntities(1.5);

      if (entities.size() > 0)
      {
         for (LivingEntity entity : entities)
         {
            if (entity.getUniqueId() == player.getUniqueId())
               continue;

            int strength = strengthModifier * (int) usesModifier;
            PotionEffect confusion = new PotionEffect(PotionEffectType.CONFUSION, strength * Ollivanders2Common.ticksPerSecond, strength);
            entity.addPotionEffect(confusion);
            break;
         }

         kill();
         return;
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }
}
