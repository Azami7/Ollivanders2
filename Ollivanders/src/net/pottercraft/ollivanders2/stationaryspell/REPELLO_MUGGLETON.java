package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
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
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Hides all blocks within its area by sending out block changes.
 * Hides all players within its area. The code to hide players is located in OllivandersSchedule.invisPlayer()
 *
 * @author lownes
 */
public class REPELLO_MUGGLETON extends ShieldSpell
{
   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.REPELLO_MUGGLETON;
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
   public REPELLO_MUGGLETON(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.REPELLO_MUGGLETON;
   }

   /**
    * Upkeep
    */
   @Override
   public void checkEffect ()
   {
      age();

      if (duration % Ollivanders2Common.ticksPerSecond == 0)
      {
         Material toMat = getBlock().getType();
         byte toDat = getBlock().getData();
         double viewDistance = Math.sqrt(2 * Math.pow(((Bukkit.getServer().getViewDistance() + 1) * 16), 2));
         for (Player player : getBlock().getWorld().getPlayers())
         {
            if (player.getLocation().distance(location) < viewDistance && !isInside(player.getLocation()))
            {
               for (Block block : Ollivanders2API.common.getBlocksInRadius(location, radius))
               {
                  player.sendBlockChange(block.getLocation(), toMat, toDat);
               }
            }
            else if (isInside(player.getLocation()))
            {
               for (Block block : Ollivanders2API.common.getBlocksInRadius(location, radius))
               {
                  player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
               }
            }
         }
      }
      if (duration <= 1)
      {
         for (Player player : getBlock().getWorld().getPlayers())
         {
            for (Block block : Ollivanders2API.common.getBlocksInRadius(location, radius))
            {
               player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
            }
         }
      }
   }

   /**
    * Do not allow targeting if the target is inside the radius and the targeter is not
    *
    * @param event the event
    */
   @Override
   public void doOnEntityTargetEvent (@NotNull EntityTargetEvent event)
   {
      Entity target = event.getTarget();
      Entity entity = event.getEntity();

      if (target == null)
         return;

      if (isInside(target.getLocation()) && !isInside(entity.getLocation()))
      {
         event.setCancelled(true);
         common.printDebugMessage("REPELLO_MUGGLETON: canceled EntityTargetEvent", null, null, false);
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
   public void deserializeSpellData(@NotNull Map<String, String> spellData) { }

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
    * Handle player floo chat
    *
    * @param event the event
    */
   @Override
   public void doOnAsyncPlayerChatEvent (@NotNull AsyncPlayerChatEvent event) {}

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