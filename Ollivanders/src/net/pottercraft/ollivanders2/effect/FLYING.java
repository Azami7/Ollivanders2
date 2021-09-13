package net.pottercraft.ollivanders2.effect;

import java.util.UUID;

import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Grants player flight
 *
 * @author azami7
 */
public class FLYING extends O2Effect
{
   boolean doSmokeEffect = true;

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the ID of the player this effect acts on
    */
   public FLYING (@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.FLYING;
   }

   /**
    * Allow player to fly until the effect ends.
    */
   @Override
   public void checkEffect ()
   {
      Player target = p.getServer().getPlayer(targetID);

      if (target != null)
      {
         age(1);
         if (duration > 1)
         {
            target.setAllowFlight(true);
            if (doSmokeEffect)
               target.getWorld().playEffect(target.getLocation(), org.bukkit.Effect.SMOKE, 4);
         }
         else
         {
            target.setAllowFlight(false);
         }
      }
      else
         kill();
   }

   /**
    * Do any cleanup related to removing this effect from the player
    */
   @Override
   public void doRemove ()
   {
      Player player = p.getServer().getPlayer(targetID);
      if (player != null)
      {
         if (!player.isOp())
         {
            player.setAllowFlight(false);
            player.setFlying(false);
         }
      }
   }

   /**
    * Do any on damage effects
    *
    * @param event the event
    */
   @Override
   public void doOnDamage (@NotNull EntityDamageByEntityEvent event) {}

   /**
    * Do any on player interact effects
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerInteract (@NotNull PlayerInteractEvent event) {}

   /**
    * Do any on player player chat effects
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerChat (@NotNull AsyncPlayerChatEvent event) {}

   /**
    * Do any effects when player sleeps
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerSleep (@NotNull PlayerBedEnterEvent event) {}

   /**
    * Do any effects when player toggles flight
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerToggleFlight (@NotNull PlayerToggleFlightEvent event) {}

   /**
    * Do any effects when player toggles sneaking
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerToggleSneak (@NotNull PlayerToggleSneakEvent event) {}

   /**
    * Do any effects when player toggles sneaking
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerToggleSprint (@NotNull PlayerToggleSprintEvent event) {}

   /**
    * Do any effects when player velocity changes
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerVelocityEvent (@NotNull PlayerVelocityEvent event) {}

   /**
    * Do any effects when player picks up an item
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerPickupItemEvent (@NotNull EntityPickupItemEvent event) {}

   /**
    * Do any effects when player holds an item
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerItemHeldEvent (@NotNull PlayerItemHeldEvent event) {}

   /**
    * Do any effects when player consumes an item
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerItemConsumeEvent (@NotNull PlayerItemConsumeEvent event) {}

   /**
    * Do any effects when player drops an item
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerDropItemEvent (@NotNull PlayerDropItemEvent event) {}

   /**
    * Do any effects when player drops an item
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerMoveEvent (@NotNull PlayerMoveEvent event) {}
}