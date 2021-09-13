package net.pottercraft.ollivanders2.effect;

import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Location;
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

import java.util.UUID;

/**
 * Put the player to "sleep". Since we cannot actually force the player to do a sleep action, this
 * effect simulates sleep.
 *
 * @since 2.2.8
 * @author Azami7
 */
public class SLEEPING extends O2Effect
{
   boolean sleeping = false;

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param duration the duration of the effect
    * @param pid      the player this effect acts on
    */
   public SLEEPING(@NotNull Ollivanders2 plugin, int duration, @NotNull UUID pid)
   {
      super(plugin, duration, pid);

      effectType = O2EffectType.SLEEPING;
      informousText = legilimensText = "is affected by an unnatural sleep";

      divinationText.add("will fall silent");
      divinationText.add("shall fall in to a deep sleep");
      divinationText.add("shall pass beyond this realm");
      divinationText.add("will surrender to a sleeping death");

      permanent = false;
   }

   @Override
   public void checkEffect ()
   {
      if (!sleeping)
      {
         if (Ollivanders2API.getPlayers(p).playerEffects.hasEffect(targetID, O2EffectType.AWAKE))
         {
            kill();
         }
         else
         {
            playerSleep();
         }
      }

      if (!permanent)
      {
         age(1);
      }
   }

   /**
    * Put the player to sleep.
    */
   private void playerSleep ()
   {
      Player target = p.getServer().getPlayer(targetID);
      if (target == null)
      {
         kill();
         return;
      }

      // tilt player head down
      Location loc = target.getLocation();
      Location newLoc = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), 45);
      target.teleport(newLoc);

      // add sleep speech
      SLEEP_SPEECH effect = new SLEEP_SPEECH(p, -1, targetID);

      // immobilize them
      IMMOBILIZE immEffect = new IMMOBILIZE(p, -1, targetID);

      Ollivanders2API.getPlayers(p).playerEffects.addEffect(effect);

      sleeping = true;
   }

   /**
    * Do any cleanup related to removing this effect from the player
    */
   @Override
   public void doRemove ()
   {
      if (sleeping)
      {
         Ollivanders2API.getPlayers(p).playerEffects.removeEffect(targetID, O2EffectType.SLEEP_SPEECH);
         Ollivanders2API.getPlayers(p).playerEffects.removeEffect(targetID, O2EffectType.IMMOBILIZE);
         sleeping = false;

         Player target = p.getServer().getPlayer(targetID);
         if (target != null)
            target.sendMessage(Ollivanders2.chatColor + "You awaken from a deep sleep.");
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
    * @param event the player bed enter event
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
