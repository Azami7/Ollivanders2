package net.pottercraft.Ollivanders2.StationarySpell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Hides all blocks within its area by sending out block changes.
 * Hides all players within its area. The code to hide players is located
 * in OllivandersSchedule.invisPlayer()
 *
 * @author lownes
 */
public class REPELLO_MUGGLETON extends StationarySpellObj implements StationarySpell
{
   public REPELLO_MUGGLETON (Player player, Location location, StationarySpells name, Integer radius, Integer duration)
   {
      super(player, location, name, radius, duration);
   }

   public void checkEffect (Ollivanders2 p)
   {
      age();
      if (p.getConfig().getBoolean("muggletonBlockChange"))
      {
         if (duration % 20 == 0)
         {
            Material toMat = getBlock().getType();
            byte toDat = getBlock().getData();
            double viewDistance = Math.sqrt(2 * Math.pow(((Bukkit.getServer().getViewDistance() + 1) * 16), 2));
            for (Player player : getBlock().getWorld().getPlayers())
            {
               if (player.isPermissionSet("Ollivanders2.BYPASS"))
               {
                  if (player.hasPermission("Ollivanders2.BYPASS"))
                  {
                     continue;
                  }
               }
               if (player.getLocation().distance(location.toLocation()) < viewDistance && !isInside(player.getLocation()))
               {
                  for (Block block : getBlocksInRadius(location.toLocation(), radius))
                  {
                     player.sendBlockChange(block.getLocation(), toMat, toDat);
                  }
               }
               else if (isInside(player.getLocation()))
               {
                  for (Block block : getBlocksInRadius(location.toLocation(), radius))
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
               for (Block block : getBlocksInRadius(location.toLocation(), radius))
               {
                  player.sendBlockChange(block.getLocation(), block.getType(), block.getData());
               }
            }
         }
      }
   }
}