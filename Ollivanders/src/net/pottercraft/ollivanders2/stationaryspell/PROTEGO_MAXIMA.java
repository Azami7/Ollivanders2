package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByCoordinatesEvent;
import net.pottercraft.ollivanders2.spell.events.OllivandersApparateByNameEvent;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

import net.pottercraft.ollivanders2.Ollivanders2;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Hurts any entities within 0.5 meters of the spell wall.
 *
 * @author lownes
 */
public class PROTEGO_MAXIMA extends ShieldSpell
{
   double damage;

   private final String damageLabel = "Damage";

   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public PROTEGO_MAXIMA(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.PROTEGO_MAXIMA;
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
    * @param damage   the damage done to other entities in this spell area
    */
   public PROTEGO_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration, double damage)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.PROTEGO_MAXIMA;
      this.damage = damage;
   }

   /**
    * Upkeep
    */
   @Override
   public void checkEffect ()
   {
      age();

      Collection<LivingEntity> nearbyEntities = Ollivanders2API.common.getLivingEntitiesInRadius(location, radius + 1);

      for (LivingEntity e : nearbyEntities)
      {
         double distance = e.getLocation().distance(location);
         if (distance > radius - 0.5 && distance < radius + 0.5)
         {
            e.damage(damage);

            flair(10);
         }
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
      Map<String, String> spellData = new HashMap<>();

      spellData.put(damageLabel, Double.toString(damage));

      return spellData;
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   @Override
   public void deserializeSpellData(@NotNull Map<String, String> spellData)
   {
      for (Map.Entry<String, String> e : spellData.entrySet())
      {
         try
         {
            if (e.getKey().equals(damageLabel))
            {
               damage = Double.parseDouble(e.getValue());
            }
         }
         catch (Exception exception)
         {
            common.printDebugMessage("Unable to read Protego Maxima damage", exception, null, false);
         }
      }
   }

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