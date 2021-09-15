package net.pottercraft.ollivanders2.effect;

import java.util.ArrayList;
import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.jetbrains.annotations.NotNull;

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

   final ArrayList<O2EffectType> additionalEffects = new ArrayList<>();

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the ID of the player this effect acts on
    */
   public SUSPENSION(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
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

   /**
    * Suspend the player in the air.
    */
   private void suspend ()
   {
      Player target = p.getServer().getPlayer(targetID);
      if (target == null)
      {
         kill();
         return;
      }

      addAdditionalEffects();

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

   /**
    * Add additional effects for suspension such as immobilizing them.
    */
   private void addAdditionalEffects ()
   {
      // add an immbolize effect with a duration slightly longer than this one so they cannot
      // move while suspended
      IMMOBILIZE immbobilize = new IMMOBILIZE(p, duration + 10, targetID);
      Ollivanders2API.getPlayers(p).playerEffects.addEffect(immbobilize);
      additionalEffects.add(O2EffectType.IMMOBILIZE);
   }

   /**
    * Release player from the suspension and return to original location
    */
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
         if (!Ollivanders2API.getPlayers(p).playerEffects.hasEffect(targetID, O2EffectType.FLYING))
         {
            target.setAllowFlight(false);
         }
      }
   }

   /**
    * Remove additional effects of Suspension
    */
   private void removeAdditionalEffect ()
   {
      for (O2EffectType effectType : additionalEffects)
      {
         Ollivanders2API.getPlayers(p).playerEffects.removeEffect(targetID, effectType);
      }
   }

   /**
    * Do any cleanup related to removing this effect from the player
    */
   @Override
   public void doRemove ()
   {
      release();
      removeAdditionalEffect();
   }

   /**
    * Do any effects when player velocity changes
    *
    * @param event the player velocity event
    */
   public void doOnPlayerVelocityEvent (@NotNull PlayerVelocityEvent event)
   {
      event.setCancelled(true);
      common.printDebugMessage("SUSPENSION: cancelling PlayerVelocityEvent", null, null, false);
   }
}