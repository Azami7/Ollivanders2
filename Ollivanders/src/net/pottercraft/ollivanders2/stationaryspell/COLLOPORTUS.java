package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.block.Block;
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
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

/**
 * Prevents opening of target door.
 *
 * @author Azami7
 */
public class COLLOPORTUS extends O2StationarySpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public COLLOPORTUS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.COLLOPORTUS;
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
   public COLLOPORTUS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.COLLOPORTUS;
   }

   /**
    * Upkeep
    */
   @Override
   public void checkEffect ()
   {
      // Colloportus duration can only be decreased by an alohomora spell
      if (duration < 1)
      {
         common.printDebugMessage("Colloportus stationary: kill spell", null, null, false);
         kill();
      }
   }

   /**
    * Prevent doors and trapdoors being broken
    *
    * @param event the event
    */
   @Override
   public void doOnBlockBreakEvent (@NotNull BlockBreakEvent event)
   {
      Block block = event.getBlock();

      if (!Ollivanders2Common.doors.contains(block.getType()) && !Ollivanders2Common.trapdoors.contains(block.getType()))
         return;

      if (isInside(block.getLocation()))
      {
         event.setCancelled(true);
         common.printDebugMessage("COLLOPORTUS: canceled BlockBreakEvent", null, null, false);
      }
   }

   /**
    * Prevent doors from being broken
    *
    * @param event the event
    */
   @Override
   public void doOnEntityBreakDoorEvent (@NotNull EntityBreakDoorEvent event)
   {
      Block block = event.getBlock();

      if (isInside(block.getLocation()))
      {
         event.setCancelled(true);
         common.printDebugMessage("COLLOPORTUS: canceled EntityBreakDoorEvent", null, null, false);
      }
   }

   /**
    * Prevent door and trapdoor blocks from being changed
    *
    * @param event the event
    */
   @Override
   public void doOnEntityChangeBlockEvent (@NotNull EntityChangeBlockEvent event)
   {
      Block block = event.getBlock();

      if (!Ollivanders2Common.doors.contains(block.getType()) && !Ollivanders2Common.trapdoors.contains(block.getType()))
         return;

      if (isInside(block.getLocation()))
      {
         event.setCancelled(true);
         common.printDebugMessage("COLLOPORTUS: canceled EntityChangeBlockEvent", null, null, false);
      }
   }

   /**
    * Prevent doors and trapdoors from being interacted with
    *
    * @param event the event
    */
   @Override
   public void doOnEntityInteractEvent (@NotNull EntityInteractEvent event)
   {
      Block block = event.getBlock();

      if (!Ollivanders2Common.doors.contains(block.getType()) && !Ollivanders2Common.trapdoors.contains(block.getType()))
         return;

      if (isInside(block.getLocation()))
      {
         event.setCancelled(true);
         common.printDebugMessage("COLLOPORTUS: canceled EntityInteractEvent", null, null, false);
      }
   }

   /**
    * Prevent doors and trapdoors from being interacted with
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerInteractEvent (@NotNull PlayerInteractEvent event)
   {
      Block block = event.getClickedBlock();
      if (block == null)
         return;

      if (!Ollivanders2Common.doors.contains(block.getType()) && !Ollivanders2Common.trapdoors.contains(block.getType()))
         return;

      if (isInside(block.getLocation()))
      {
         event.setCancelled(true);
         common.printDebugMessage("COLLOPORTUS: canceled PlayerInteractEvent", null, null, false);
      }
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
   public void deserializeSpellData(@NotNull Map<String, String> spellData)
   {
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
    * Handle player chat
    *
    * @param event the event
    */
   @Override
   public void doOnAsyncPlayerChatEvent (@NotNull AsyncPlayerChatEvent event) {}

   /**
    * Handle entity damage event
    *
    * @param event the event
    */
   @Override
   public void doOnEntityDamageEvent (@NotNull EntityDamageEvent event) {}

   /**
    * Handle apparate by name event
    *
    * @param event the event
    */
   @Override
   public void doOnOllivandersApparateByNameEvent (@NotNull OllivandersApparateByNameEvent event) {}

   /**
    * Handle apparate by coord event
    *
    * @param event the event
    */
   @Override
   public void doOnOllivandersApparateByCoordinatesEvent (@NotNull OllivandersApparateByCoordinatesEvent event) {}

   /**
    * Handle entity teleport event
    *
    * @param event the event
    */
   @Override
   public void doOnEntityTeleportEvent (@NotNull EntityTeleportEvent event) {}

   /**
    * Handle player teleport event
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerTeleportEvent (@NotNull PlayerTeleportEvent event) {}

   /**
    * Handle entity combust by block events
    *
    * @param event the event
    */
   @Override
   public void doOnEntityCombustEvent(@NotNull EntityCombustEvent event) {}
}