package net.pottercraft.Ollivanders2;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Serializable location stored in OPlayer
 *
 * @author lownes
 * @deprecated this class was only needed to do binary serialization of MC Location objects
 */
@Deprecated
public class OLocation implements Serializable
{
   private String world;
   private UUID worldUUID;
   private double x;
   private double y;
   private double z;

   public OLocation (Location location)
   {
      world = location.getWorld().getName();
      worldUUID = location.getWorld().getUID();
      x = location.getX();
      y = location.getY();
      z = location.getZ();
   }

   /**
    * Gets distance between location and This
    *
    * @param location - Location to get distance between
    * @return Double distance
    */
   public double distance (Location location)
   {
      UUID uid = getWorldUUID();
      if (uid.equals(location.getWorld().getUID()))
      {
         Location newLoc = new Location(location.getWorld(), x, y, z);
         return newLoc.distance(location);
      }
      else
      {
         throw new IllegalArgumentException();
      }
   }

   public String getWorld ()
   {
      return world;
   }

   public UUID getWorldUUID ()
   {
      if (worldUUID == null)
      {
         worldUUID = Bukkit.getWorld(world).getUID();
      }
      return worldUUID;
   }

   /**
    * Transforms OLocation to Location
    *
    * @return Location
    */
   public Location toLocation ()
   {
      World world = Bukkit.getServer().getWorld(this.world);
      return new Location(world, x, y, z);
   }

}