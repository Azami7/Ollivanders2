package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Entity;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Negates fall damage.
 *
 * @author lownes
 * @author Azami7
 */
public class MOLLIARE extends O2StationarySpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public MOLLIARE(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.MOLLIARE;
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
   public MOLLIARE(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.MOLLIARE;
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
    * Handle player interact event
    *
    * @param event the event
    */
   @Override
   public void doOnEntityDamageEvent (@NotNull EntityDamageEvent event)
   {
      if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
         return;

      Entity entity = event.getEntity();

      if (isInside(entity.getLocation()))
      {
         event.setCancelled(true);
         common.printDebugMessage("MOLLIARE: canceled EntityDamageEvent", null, null, false);
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
   public void deserializeSpellData(@NotNull Map<String, String> spellData) {}

   /**
    * Handle player floo chat
    *
    * @param event the event
    */
   @Override
   public void doOnAsyncPlayerChatEvent (@NotNull AsyncPlayerChatEvent event) {}

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
    * Handle player interact event
    *
    * @param event the event
    */
   @Override
   public void doOnPlayerInteractEvent (@NotNull PlayerInteractEvent event) {}

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