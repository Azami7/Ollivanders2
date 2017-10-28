package net.pottercraft.Ollivanders2.StationarySpell;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.OLocation;

/**
 * Checks for entities going into a vanishing cabinet
 *
 * @author lownes
 */
public class HARMONIA_NECTERE_PASSUS extends StationarySpellObj implements StationarySpell
{
   private final OLocation twin;
   private Set<UUID> teleported = new HashSet<>();

   public HARMONIA_NECTERE_PASSUS (Player player, Location location, StationarySpells name, Integer radius,
                                   Integer duration, Location twin)
   {
      super(player, location, name, radius, duration);
      this.twin = new OLocation(twin);
   }

   @Override
   public void checkEffect (Ollivanders2 p)
   {
      HARMONIA_NECTERE_PASSUS twinHarm = null;
      for (StationarySpellObj stat : p.getStationary())
      {
         if (stat instanceof HARMONIA_NECTERE_PASSUS
               && stat.location.toLocation().getBlock().equals(twin.toLocation().getBlock()))
         {
            twinHarm = (HARMONIA_NECTERE_PASSUS) stat;
         }
      }
      if (twinHarm == null || !cabinetCheck(location.toLocation().getBlock()))
      {
         kill();
         return;
      }
      for (Entity entity : location.toLocation().getWorld().getEntities())
      {
         if (teleported.contains(entity.getUniqueId()))
         {
            if (!entity.getLocation().getBlock().equals(location.toLocation().getBlock()))
            {
               teleported.remove(entity.getUniqueId());
            }
         }
         else
         {
            if (entity.getLocation().getBlock().equals(location.toLocation().getBlock()))
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
   public boolean cabinetCheck (Block feet)
   {
      if (feet.getType() != Material.AIR && feet.getType() != Material.WALL_SIGN && feet.getType() != Material.SIGN_POST)
      {
         return false;
      }

      if (feet.getRelative(1, 0, 0).getType() == Material.AIR ||
            feet.getRelative(-1, 0, 0).getType() == Material.AIR ||
            feet.getRelative(0, 0, 1).getType() == Material.AIR ||
            feet.getRelative(0, 0, -1).getType() == Material.AIR ||
            feet.getRelative(1, 1, 0).getType() == Material.AIR ||
            feet.getRelative(-1, 1, 0).getType() == Material.AIR ||
            feet.getRelative(0, 1, 1).getType() == Material.AIR ||
            feet.getRelative(0, 1, -1).getType() == Material.AIR ||
            feet.getRelative(0, 2, 0).getType() == Material.AIR)
      {
         return false;
      }
      else
      {
         return true;
      }
   }

   /**
    * Send the entity to the twin cabinet.
    *
    * @param entity
    */
   public void teleport (Entity entity)
   {
      Location toLoc = location.toLocation();
      toLoc.setPitch(entity.getLocation().getPitch());
      toLoc.setYaw(entity.getLocation().getYaw());
      entity.teleport(toLoc);
      teleported.add(entity.getUniqueId());
   }
}
