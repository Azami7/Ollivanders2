package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Nullum apparebit creates a stationary spell which will not allow apparition into it.
 *
 * @author lownes
 * @author Azami7
 */
public class NULLUM_APPAREBIT extends O2StationarySpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.NULLUM_APPAREBIT;
   }

   /**
    * Constructor
    *
    * @param plugin   a callback to the MC plugin
    * @param pid      the player who cast the spell
    * @param location the center location of the spell
    * @param type     the type of this spell
    * @param radius   the radius for this spell
    * @param duration the duration of the spell
    */
   public NULLUM_APPAREBIT(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.NULLUM_APPAREBIT;
   }

   /**
    * Upkeep
    */
   @Override
   public void checkEffect ()
   {
      age();
   }

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @return a map of the serialized data
    */
   @Override
   @NotNull
   public Map<String, String> serializeSpellData ()
   {
      return new HashMap<>();
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   @Override
   public void deserializeSpellData(@NotNull Map<String, String> spellData) { }

   /**
    * Prevent player apparating to this location
    *
    * @param event the event
    */
   @Override
   public void doOnOllivandersApparateByNameEvent (@NotNull OllivandersApparateByNameEvent event)
   {
      Location destination = event.getDestination();

      if (isInside(destination))
      {
         event.setCancelled(true);
         common.printDebugMessage("NULLUM_APPAREBIT: canceled OllivandersApparateByNameEvent", null, null, false);
      }

      new BukkitRunnable()
      {
         @Override
         public void run()
         {
            if (event.isCancelled())
               playerFeedback(event.getPlayer());
         }
      }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
   }

   /**
    * Prevent player apparating to this location
    *
    * @param event the event
    */
   @Override
   public void doOnOllivandersApparateByCoordinatesEvent (@NotNull OllivandersApparateByCoordinatesEvent event)
   {
      Location destination = event.getDestination();

      if (isInside(destination))
      {
         event.setCancelled(true);
         common.printDebugMessage("NULLUM_APPAREBIT: canceled OllivandersApparateByCoordinatesEvent", null, null, false);
      }

      new BukkitRunnable()
      {
         @Override
         public void run()
         {
            if (event.isCancelled())
               playerFeedback(event.getPlayer());
         }
      }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
   }

   /**
    * Prevent entity teleporting to this location
    *
    * @param event the event
    */
   @Override
   public void doOnEntityTeleportEvent (@NotNull EntityTeleportEvent event)
   {
      Location destination = event.getTo();
      if (destination == null)
         return;

      if (isInside(destination))
      {
         event.setCancelled(true);
         common.printDebugMessage("NULLUM_APPAREBIT: canceled EntityTeleportEvent", null, null, false);
      }
   }

   /**
    * Prevent player teleporting to this location
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerTeleportEvent (@NotNull PlayerTeleportEvent event)
   {
      Location destination = event.getTo();
      if (destination == null)
         return;

      if (isInside(destination))
      {
         event.setCancelled(true);
         common.printDebugMessage("NULLUM_APPAREBIT: canceled PlayerTeleportEvent", null, null, false);
      }

      new BukkitRunnable()
      {
         @Override
         public void run()
         {
            if (event.isCancelled())
               playerFeedback(event.getPlayer());
         }
      }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
   }

   /**
    * Feedback to the player when they try to apparate.
    *
    * @param player the player
    */
   private void playerFeedback (@NotNull Player player)
   {
      player.sendMessage(Ollivanders2.chatColor + "A powerful magic protects that place.");
      player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
      flair(5);
   }

   /**
    * Handle players moving
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerMoveEvent (@NotNull PlayerMoveEvent event) {}

   /**
    * Handle creatures from spawning
    *
    * @param event the event
    */
   @Override
   public void doOnCreatureSpawnEvent (@NotNull CreatureSpawnEvent event) {}

   /**
    * Handle entities spawning
    *
    * @param event the event
    */
   @Override
   public void doOnEntityTargetEvent (@NotNull EntityTargetEvent event) {}

   /**
    * Handle block break event
    *
    * @param event the event
    */
   @Override
   public void doOnBlockBreakEvent (@NotNull BlockBreakEvent event) {}

   /**
    * Handle break door event
    *
    * @param event the event
    */
   @Override
   public void doOnEntityBreakDoorEvent (@NotNull EntityBreakDoorEvent event) {}

   /**
    * Handle entity change block event
    *
    * @param event the event
    */
   @Override
   public void doOnEntityChangeBlockEvent (@NotNull EntityChangeBlockEvent event) {}

   /**
    * Handle entity interact event
    *
    * @param event the event
    */
   @Override
   public void doOnEntityInteractEvent (@NotNull EntityInteractEvent event) {}

   /**
    * Handle player interact event
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerInteractEvent (@NotNull PlayerInteractEvent event) {}

   /**
    * Handle entity damage event
    *
    * @param event the event
    */
   @Override
   public void doOnEntityDamageEvent (@NotNull EntityDamageEvent event) {}

   /**
    * Handle player chat
    *
    * @param event the event
    */
   @Override
   public void doOnAsyncPlayerChatEvent (@NotNull AsyncPlayerChatEvent event) {}

   /**
    * Handle entity combust by block events
    *
    * @param event the event
    */
   @Override
   public void doOnEntityCombustEvent(@NotNull EntityCombustEvent event) {}
}