package net.pottercraft.ollivanders2.stationaryspell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Checks for entities going into a vanishing cabinet
 *
 * @author lownes
 * @author Azami7
 */
public class HARMONIA_NECTERE_PASSUS extends O2StationarySpell
{
   private Location twinCabinetLocation;
   private final String twinLabel = "Twin";

   HashMap<Player, Integer> inUseBy = new HashMap<>();
   int useCooldown = Ollivanders2Common.ticksPerSecond * 30;

   /**
    * Simple constructor used for deserializing saved stationary spells at server start. Do not use to cast spell.
    *
    * @param plugin a callback to the MC plugin
    */
   public HARMONIA_NECTERE_PASSUS(@NotNull Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2StationarySpellType.HARMONIA_NECTERE_PASSUS;
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
    * @param twin     the location of this cabinet's twin
    */
   public HARMONIA_NECTERE_PASSUS(@NotNull Ollivanders2 plugin, @NotNull UUID pid, @NotNull Location location, @NotNull O2StationarySpellType type, int radius, int duration, @NotNull Location twin)
   {
      super(plugin, pid, location, type, radius, duration);

      spellType = O2StationarySpellType.HARMONIA_NECTERE_PASSUS;
      this.twinCabinetLocation = twin;
   }

   /**
    * Upkeep, disable the spell if the twin is broken
    */
   @Override
   public void checkEffect ()
   {
      World world = location.getWorld();
      if (world == null)
      {
         common.printDebugMessage("HARMONIA_NECTERE_PASSUS.checkEffect: world is null", null, null, false);
         kill();
         return;
      }

      HARMONIA_NECTERE_PASSUS twinCabinet = getTwin();

      if (twinCabinet == null)
      {
         common.printDebugMessage("Harmonia stationary: twin cabinet null", null, null, true);
         kill();
         return;
      }

      if (!cabinetCheck(location.getBlock()))
      {
         common.printDebugMessage("Harmonia stationary: twin cabinet malformed", null, null, true);
         kill();
         twinCabinet.kill();
         return;
      }

      // upkeep on inUseBy
      HashMap<Player, Integer> temp = new HashMap<>(inUseBy);

      for (Map.Entry<Player, Integer> entry : temp.entrySet())
      {
         Player player = entry.getKey();
         Integer cooldown = entry.getValue();

         if (common.locationEquals(player.getLocation(), location))
            continue;

         if (cooldown < 0)
            inUseBy.remove(player);
         else
            inUseBy.put(player, cooldown - 1);
      }
   }

   /**
    * Get the twin for this cabinet.
    *
    * @return the twin if found, null otherwise
    */
   @Nullable
   public HARMONIA_NECTERE_PASSUS getTwin ()
   {
      for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         if (stationarySpell instanceof HARMONIA_NECTERE_PASSUS && common.locationEquals(stationarySpell.location, twinCabinetLocation))
         {
            return (HARMONIA_NECTERE_PASSUS)stationarySpell;
         }
      }

      return null;
   }

   /**
    * Is this cabinet in use by the player
    *
    * @param player the player to check
    * @return true if this player is using this cabinet, false otherwise
    */
   public boolean isUsing (Player player)
   {
      if (inUseBy.containsKey(player))
         return true;

      return false;
   }

   /**
    * Checks the integrity of the cabinet
    *
    * @param feet - The block at the player's feet if the player is standing in the cabinet
    * @return - True if the cabinet is whole, false if not
    */
   private boolean cabinetCheck(@NotNull Block feet)
   {
      if (feet.getType() != Material.AIR && !Ollivanders2Common.signs.contains(feet.getType()))
         return false;

      return (feet.getRelative(1, 0, 0).getType() != Material.AIR && feet.getRelative(1, 1, 0).getType() != Material.AIR
              && feet.getRelative(-1, 0, 0).getType() != Material.AIR && feet.getRelative(-1, 1, 0).getType() != Material.AIR
              && feet.getRelative(0, 0, 1).getType() != Material.AIR && feet.getRelative(0, 1, 1).getType() != Material.AIR
              && feet.getRelative(0, 0, -1).getType() != Material.AIR && feet.getRelative(0, 1, -1).getType() != Material.AIR
              && feet.getRelative(0, 1, 0).getType() == Material.AIR && feet.getRelative(0, 2, 0).getType() != Material.AIR);
   }

   /**
    * Serialize all data specific to this spell so it can be saved.
    *
    * @return a map of the serialized data
    */
   @Override
   @NotNull
   public Map<String, String> serializeSpellData()
   {
      Map<String, String> serializedLoc = common.serializeLocation(location, twinLabel);

      if (serializedLoc == null)
         serializedLoc = new HashMap<>();

      return serializedLoc;
   }

   /**
    * Deserialize the data for this spell and load the data to this spell.
    *
    * @param spellData a map of the saved spell data
    */
   @Override
   public void deserializeSpellData(@NotNull Map<String, String> spellData)
   {
      Location loc = common.deserializeLocation(spellData, twinLabel);

      if (loc != null)
         twinCabinetLocation = loc;
   }

   /**
    * When the player moves in to the vanishing cabinet, teleport them to the twin
    *
    * @param event the event
    */
   @Override
   void doOnPlayerMoveEvent (@NotNull PlayerMoveEvent event)
   {
      Player player = event.getPlayer();
      // make sure player is not already using this vanishing cabinet
      if (isUsing(player))
         return;

      Location toLoc = event.getTo();
      Location fromLoc = event.getFrom();

      // make sure they actually moved locations, not turned head, etc
      if (toLoc == null || common.locationEquals(toLoc, fromLoc))
         return;

      HARMONIA_NECTERE_PASSUS twin = getTwin();
      if (twin == null)
      {
         kill();
         return;
      }

      // make sure they do not have a use cooldown on the twin or we'll just teleport them right back as soon as they arrive and trigger a move event
      if (twin.isUsing(player))
         return;

      if (isInside(toLoc))
      {
         inUseBy.put(player, useCooldown);

         new BukkitRunnable()
         {
            @Override
            public void run()
            {
               if (!event.isCancelled())
               {
                  p.addTeleportEvent(player, player.getLocation(), twinCabinetLocation, true);
               }
            }
         }.runTaskLater(p, Ollivanders2Common.ticksPerSecond);
      }
   }
}
