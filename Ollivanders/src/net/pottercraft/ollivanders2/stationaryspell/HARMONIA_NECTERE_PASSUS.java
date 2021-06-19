package net.pottercraft.ollivanders2.stationaryspell;

import java.util.*;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

/**
 * Checks for entities going into a vanishing cabinet
 *
 * @author lownes
 */
public class HARMONIA_NECTERE_PASSUS extends StationarySpellObj implements StationarySpell
{
   private Location twin;
   private Set<UUID> teleported = new HashSet<>();

   private final String twinLabel = "Twin";

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
      this.twin = twin;
   }

   @Override
   public void checkEffect ()
   {
      HARMONIA_NECTERE_PASSUS twinHarm = null;
      for (StationarySpellObj stat : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         if (stat instanceof HARMONIA_NECTERE_PASSUS
                 && stat.location.getBlock().equals(twin.getBlock()))
         {
            twinHarm = (HARMONIA_NECTERE_PASSUS) stat;
         }
      }
      if (twinHarm == null || !cabinetCheck(location.getBlock()))
      {
         kill();
         return;
      }

      World world = location.getWorld();
      if (world == null)
      {
         p.getLogger().warning("HARMONIA_NECTERE_PASSUS.checkEffect: world is null");
         kill();
         return;
      }

      for (Entity entity : world.getEntities())
      {
         if (teleported.contains(entity.getUniqueId()))
         {
            if (!entity.getLocation().getBlock().equals(location.getBlock()))
            {
               teleported.remove(entity.getUniqueId());
            }
         }
         else
         {
            if (entity.getLocation().getBlock().equals(location.getBlock()))
            {
               twinHarm.teleport(entity);
            }
         }
      }
   }

   /**
    * Checks the integrity of the cabinet
    *
    * @param feet - The block at the player's feet if the player is standing in the cabinet
    * @return - True if the cabinet is whole, false if not
    */
   private boolean cabinetCheck(@NotNull Block feet)
   {
      if (feet.getType() != Material.AIR && !Ollivanders2Common.wallSigns.contains(feet.getType()))
      {
         return false;
      }

      return (feet.getRelative(1, 0, 0).getType() == Material.AIR || feet.getRelative(-1, 0, 0).getType() == Material.AIR ||
              feet.getRelative(0, 0, 1).getType() == Material.AIR || feet.getRelative(0, 0, -1).getType() == Material.AIR ||
              feet.getRelative(1, 1, 0).getType() == Material.AIR || feet.getRelative(-1, 1, 0).getType() == Material.AIR ||
              feet.getRelative(0, 1, 1).getType() == Material.AIR || feet.getRelative(0, 1, -1).getType() == Material.AIR ||
              feet.getRelative(0, 2, 0).getType() == Material.AIR);
   }

   /**
    * Send the entity to the twin cabinet.
    *
    * @param entity the entity being transported
    */
   private void teleport(@NotNull Entity entity)
   {
      location.setPitch(entity.getLocation().getPitch());
      location.setYaw(entity.getLocation().getYaw());
      entity.teleport(location);
      teleported.add(entity.getUniqueId());
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
         twin = loc;
   }
}
