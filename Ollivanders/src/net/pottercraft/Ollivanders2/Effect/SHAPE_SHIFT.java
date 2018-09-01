package net.pottercraft.Ollivanders2.Effect;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.*;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Change the the form of a player in to another entity.
 *
 * Requires libDisguises
 *
 * @author Azami7
 * @since 2.2.8
 */
public abstract class SHAPE_SHIFT extends O2Effect
{
   boolean permanent = false;
   boolean transformed = false;

   TargetedDisguise disguise;
   EntityType form;
   LivingWatcher watcher;

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param effect the effect cast
    * @param duration the duration of the effect
    * @param player the player this effect acts on
    */
   public SHAPE_SHIFT (Ollivanders2 plugin, O2EffectType effect, int duration, Player player)
   {
      super(plugin, effect, duration, player);
   }

   /**
    * Handle upkeep of this effect.
    */
   @Override
   public void checkEffect ()
   {
      if (!Ollivanders2.libsDisguisesEnabled)
      {
         transformed = false;

         kill();
         return;
      }

      if (!permanent)
      {
         age(1);
      }

      if (!transformed && !kill)
      {
         transform();
      }
   }

   /**
    * Transfigure the player to the new form.
    */
   protected void transform ()
   {
      if (form != null)
      {
         // disguisePlayer the player
         DisguiseType disguiseType = DisguiseType.getType(form);
         disguise = new MobDisguise(disguiseType);
         watcher = (LivingWatcher) disguise.getWatcher();

         customizeWatcher();

         DisguiseAPI.disguiseToAll(target, disguise);
         transformed = true;
      }
      else
      {
         kill();
      }
   }

   /**
    * Transfigure the player back to human form and kill this effect.
    */
   @Override
   public void kill ()
   {
      if (transformed)
      {
         if (disguise != null)
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

         transformed = false;
      }

      kill = true;
   }

   /**
    * Override this to set the specific form this player will transfigure in to.
    */
   protected void customizeWatcher ()
   {

   }
}
