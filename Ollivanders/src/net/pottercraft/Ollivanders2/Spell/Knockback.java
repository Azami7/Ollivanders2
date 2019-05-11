package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Spells that have a knockback effect.
 *
 * @author Azami7
 * @since 2.3
 */
public abstract class Knockback extends Charms
{
   /**
    * This reduces strong the knockback is. 1 is max strength, any number higher than one will reduce the strength.
    */
   protected int strengthReducer = 1;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public Knockback () {}

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public Knockback (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      setUsesModifier();

      // world guard flags
      worldGuardFlags.add(DefaultFlag.PVP);
      worldGuardFlags.add(DefaultFlag.DAMAGE_ANIMALS);
   }

   /**
    * Looks for an entity within the radius of the
    */
   @Override
   protected void doCheckEffect ()
   {
      List<Entity> entities = getCloseEntities(1.5);

      if (entities.size() > 0)
      {
         // look for entities within radius of the projectile and knockback one of them
         for (Entity entity : entities)
         {
            // do not knockback the caster
            if (entity.getUniqueId() == player.getUniqueId())
               continue;

            entity.setVelocity(player.getLocation().getDirection().normalize().multiply(usesModifier / strengthReducer));
            break;
         }

         kill();
         return;
      }

      // projectile is stopped, kill spell
      if (hasHitTarget())
         kill();
   }
}
