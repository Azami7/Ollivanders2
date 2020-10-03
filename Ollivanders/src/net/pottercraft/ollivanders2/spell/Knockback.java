package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Spells that have a knockback effect.
 *
 * @author Azami7
 * @since 2.3
 */
public abstract class Knockback extends O2Spell
{
   boolean pull = false;

   /**
    * This reduces strong the knockback is. 1 is max strength, any number higher than one will reduce the strength.
    */
   protected int strengthReducer = 20;

   protected double minVelocity = 1;
   protected double maxVelocity = 5;

   /**
    * Whitelist of entity types this spell works on.
    */
   protected List<EntityType> entityWhitelist = new ArrayList<>();

   /**
    * Blacklist of entity types this spell will not work on.
    */
   protected List<EntityType> entityBlacklist = new ArrayList<>();

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public Knockback()
   {
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public Knockback(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      initSpell();
   }

   /**
    * Looks for an entity near the radius of the spell projectile
    */
   @Override
   protected void doCheckEffect()
   {
      List<Entity> entities = getCloseEntities(1.5);

      if (entities.size() > 0)
      {
         // look for entities within radius of the projectile and knockback one of them
         for (Entity entity : entities)
         {
            // first check entity whitelist
            if (entityWhitelist.size() > 0 && !entityWhitelist.contains(entity.getType()))
               continue;

            // do not knockback the caster or any blacklisted entity
            if (entity.getUniqueId() == player.getUniqueId() || entityBlacklist.contains(entity.getType()))
            {
               continue;
            }

            double velocity = usesModifier / strengthReducer;
            if (velocity < minVelocity)
            {
               velocity = minVelocity;
            }
            else if (velocity > maxVelocity)
            {
               velocity = maxVelocity;
            }

            if (pull)
            {
               velocity = velocity * -1;
            }

            entity.setVelocity(player.getLocation().getDirection().normalize().multiply(velocity));

            kill();
            return;
         }
      }

      // projectile is stopped, kill spell
      if (hasHitTarget())
         kill();
   }
}
