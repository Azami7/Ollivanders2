package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2WorldGuard;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Super class for all disguise-based transfigurations
 *
 * @since 2.2.6
 * @author Azami7
 */
public abstract class EntityDisguiseSuper extends EntityTransfigurationSuper
{
   protected DisguiseType disguiseType;
   protected TargetedDisguise disguise;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EntityDisguiseSuper () {}

   /**
    * Constructor.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public EntityDisguiseSuper (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super (plugin, player, name, rightWand);

      worldGuard = new Ollivanders2WorldGuard(plugin);
      permanent = false;
   }

   /**
    * Transfigure the entity.
    *
    * @param entity
    */
   @Override
   protected void transfigureEntity (Entity entity)
   {
      DisguiseAPI.disguiseToAll(entity, disguise);

      isTransfigured = true;
   }

   /**
    * Determine if this entity be transfigured by this spell.
    *
    * Entity can transfigure if:
    * 1. LibsDisguises is enabled
    * 2. It is not in the blacklist
    * 3. It is in the whitelist, if the whitelist exists
    * 4. The entity is not already the target type
    * 5. There are no WorldGuard permissions preventing the caster from altering this entity type
    *
    * @param e
    * @return true if it can be changed
    */
   @Override
   protected boolean canTransfigure (Entity e)
   {
      EntityType eType = e.getType();

      if (Ollivanders2.debug)
         p.getLogger().info("canTransfigure: " + e.getType().toString());

      if (!Ollivanders2.libsDisguisesEnabled)
      {
         if (Ollivanders2.debug)
            p.getLogger().info("LibsDisguises not enabled.");

         return false;
      }
      else if (!targetTypeCheck(e))
      {
         if (Ollivanders2.debug)
            p.getLogger().info("Target entity type cannot be transfigured.");

         return false;
      }
      else if (!wgPermissionsCheck(e))
      {
         if (Ollivanders2.debug)
            p.getLogger().info("WG permissions not allowed.");

         return false;
      }

      return true;
   }

   /**
    * Check for WorldGuard permissions related to this entity type. This needs to be overridden by each
    * child class or it will fail all transfigurations.
    *
    * @param e the target entity
    * @return true if there are no restrictions, false otherwise
    */
   protected boolean wgPermissionsCheck(Entity e)
   {
      return false;
   }

   /**
    * Revert the entity back to their original form.
    */
   @Override
   public void revert ()
   {
      Entity entity = disguise.getEntity();
      try
      {
         DisguiseAPI.undisguiseToAll(entity);
      }
      catch (Exception e)
      {
         // in case entity no longer exists
      }

      kill();
   }
}
