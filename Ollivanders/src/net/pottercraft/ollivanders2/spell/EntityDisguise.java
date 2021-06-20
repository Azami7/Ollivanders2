package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Super class for all disguise-based transfigurations
 *
 * @author Azami7
 * @since 2.2.6
 */
public abstract class EntityDisguise extends EntityTransfiguration
{
   protected DisguiseType disguiseType;
   protected TargetedDisguise disguise;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EntityDisguise()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public EntityDisguise(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   /**
    * Transfigure the entity.
    *
    * @param entity the entity to transfigure
    */
   @Override
   protected void transfigureEntity(@NotNull Entity entity)
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
    * @param e the entity to check
    * @return true if it can be changed
    */
   @Override
   protected boolean canTransfigure(@NotNull Entity e)
   {
      common.printDebugMessage("canTransfigure: " + e.getType().toString(), null, null, false);

      if (!Ollivanders2.libsDisguisesEnabled)
      {
         common.printDebugMessage("LibsDisguises not enabled.", null, null, false);
         return false;
      }
      else if (!targetTypeCheck(e))
      {
         common.printDebugMessage("Target entity type cannot be transfigured.", null, null, false);
         return false;
      }

      return true;
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
   }
}
