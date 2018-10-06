package net.pottercraft.Ollivanders2.Effect;

import java.util.UUID;

import net.pottercraft.Ollivanders2.Ollivanders2API;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Keeps a player hoisted 1.5 blocks into the air. This replaced the original LEVICORPUS effect.
 *
 * @author Azami7
 * @since 2.2.8
 */
public class SUSPENSION extends O2Effect
{
   Location originalLocation;
   boolean suspended = false;
   boolean canFly = false;

   /**
    * Constructor
    *
    * @param plugin a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid the ID of the player this effect acts on
    */
   public SUSPENSION (Ollivanders2 plugin, Integer duration, UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.SUSPENSION;
   }

   /**
    * Age this effect by 1, move the player up 1.5 blocks off the ground if they are not already suspended.
    */
   @Override
   public void checkEffect ()
   {
      age(1);

      if (!suspended)
      {
         suspend();
      }
   }

   @Override
   public void kill ()
   {
      release();
      kill = true;
   }

   private void suspend ()
   {
      Player target = p.getServer().getPlayer(targetID);
      if (target == null)
      {
         kill();
         return;
      }

      // make them fly so they do not fall from suspension
      canFly = target.getAllowFlight();
      if (!canFly)
      {
         target.setAllowFlight(true);
      }

      // suspend them in the air
      originalLocation = target.getLocation();
      Location newLoc = target.getEyeLocation();
      Location suspendLoc = new Location (newLoc.getWorld(), newLoc.getX(), newLoc.getY(), newLoc.getZ(), originalLocation.getYaw(), 45);
      target.teleport(suspendLoc);
      target.setFlying(true);

      suspended = true;
   }

   private void release ()
   {
      Player target = p.getServer().getPlayer(targetID);
      if (target == null)
         return;

      // teleport them back to their original location
      target.teleport(originalLocation);

      // if they were not able to fly, remove flying
      target.setFlying(false);

      if (!canFly)
      {
         if (!Ollivanders2API.getPlayers().playerEffects.hasEffect(targetID, O2EffectType.FLYING))
         {
            target.setAllowFlight(false);
         }
      }
   }
}